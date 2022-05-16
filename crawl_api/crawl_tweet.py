import re
import time
from datetime import datetime, timedelta
from selenium import webdriver
from bs4 import BeautifulSoup
import pandas as pd


class tweet_crawler:
    def __init__(self):
        self.driver = webdriver.Chrome('./chromedriver.exe')  # 크롬 드라이버 위치 -> DeprecationWarning 무시하기
        self.driver.set_window_size(800, 900)  # 필요없는 부분의 최소화를 위한 작은 창 설정

        # 각 크롤링 결과 저장하기 위한 리스트 선언
        # totaltweets = []

        # 크롤 대상에 적용된 class style
        self.span_class_txt = "css-901oao css-16my406 r-poiln3"
        self.span_class_bold_text = "css-901oao css-16my406 r-poiln3 r-b88u0q r-bcqeeo r-qvutc0"
        self.span_bold_tag = "r-b88u0q"

        # 제거할 트위터 기본 텍스트
        self.twitter_text = ["최신 소식을 놓치지 마세요", "트위터를 사용하면 가장 먼저 알게 됩니다.", "로그인", "가입하기",
                        "인기", "최신", "사용자", "사진", "동영상", "새 트윗 보기", "이 스레드 보기", "모두 보기", "팔로우", "공식 트위터"]
        self.user_at = "@"

        self.date_format = "%Y-%m-%d"

    def url_setting(self, query, start_date):
        since = '%20since%3A' + start_date.strftime(self.date_format)
        until = '%20until%3A' + (start_date + timedelta(days=1)).strftime(self.date_format)  # since + 1일 = 다음날, 즉 그날 하루만 검색
        # min_retweets = '%20min_retweets%3A' + str(min_retweet_value)
        lang = '%20lang%3A' + 'ko'
        url = 'https://twitter.com/search?q=' + query + since + until + lang
        return url

    def tweet_crawler(self, query_list: list, start_date: datetime, end_date: datetime = None):
        df = pd.DataFrame(columns=["text", "rt_count", "company", "date"])
        min_text_length = 16  # 크롤링 된 element중 저장할 text의 최소 길이
        # raw_tweet = []
        # curr_date = start_date

        if type(query_list) is not list:
            query_list = [query_list]

        if end_date is None:
            end_date = start_date

        while (end_date - start_date).days > 0:
            for query in query_list:
                url = self.url_setting(query, start_date)

                self.driver.get(url)
                html = self.driver.page_source
                soup = BeautifulSoup(html, 'html.parser')

                lastHeight = self.driver.execute_script("return document.body.scrollHeight")

                while True:
                    print('-------------------------------------------------------date:', start_date)

                    # 여기서 크로링 한번 진행
                    self.crawler(soup, query)

                    # 스크롤 다운
                    self.driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
                    time.sleep(2)
                    newHeight = self.driver.execute_script("return document.body.scrollHeight")

                    if newHeight != lastHeight:
                        html = self.driver.page_source
                        soup = BeautifulSoup(html, 'html.parser')

                        # 크롤링 결과 리스트로 저장
                        raw_tweet = self.crawler(soup, query)

                        # 결과 전처리
                        cleaned_tweet = self.clear_contents(raw_tweet, min_text_length)

                        # 전처리 완료 된 list들을 df에 추가
                        text_series = pd.Series(cleaned_tweet, dtype=pd.StringDtype())
                        df_to_insert = pd.DataFrame(text_series, columns=['text'])
                        df_to_insert['rt_count'] = 0
                        df_to_insert['date'] = start_date

                        df = pd.concat([df, df_to_insert], ignore_index=True)
                        print(df_to_insert)

                    else:
                        break

                    lastHeight = newHeight

                # end of the while
                start_date = start_date + timedelta(days=1)

        return df

    def crawler(self, soup, query):
        '''
        1. span_class_txt 에 해당하는 span.text 내용을 모두 크롤링
        2. 기본 전처리(트위터 기본 텍스트, 유저아이디 제외(continue)
        3. 원하는 검색어 텍스트는 bold 처리되어 하나의 element 취급되어 크롤링되므로 < 이전 + 키워드 + 이후 > 형식으로 맞춰주기
        4. 문장 첫 시작에 ·제거
        5. 그 외에는 totaltweets 에 해당 text 추가
        '''

        #  1. span_class_txt 에 해당하는 span.text 내용을 모두 크롤링

        tweets = soup.find_all(class_=re.compile(self.span_class_txt))  # type: 'bs4.element.ResultSet'
        is_last_query = False
        totaltweets = []

        for t in tweets:
            # print(t.text)
            #  2. 기본 전처리(트위터 기본 텍스트, 유저아이디 제외(continue)
            if t.text in self.twitter_text:
                # print("***** 기본 텍스트입니다. continue를 진행합니다")
                continue
            elif t.text.startswith(self.user_at):
                # print("***** 유저 아이디입니다. continue를 진행합니다")
                # 유저 @ 이전에 닉네임이 수집되었을것이니 -1을 지우기
                if len(totaltweets) > 0:
                    totaltweets.pop(-1)

                continue

            #  3. 원하는 검색어 텍스트는 bold 처리되어 하나의 element 취급되어 크롤링되므로 < 이전 + 키워드 + 이후 > 형식으로 맞춰주기
            elif t.text in query:
                is_last_query = True
                # print("t.text in query입니다! query:", query, "/ t.text:",t.text)
                if len(totaltweets) > 0:
                    totaltweets[-1] = totaltweets[-1] + t.text
                else:
                    totaltweets.append(t.text)
                # print("totaltweets[-1]:", totaltweets[-1])
                # totaltweets.append(t.text)

            elif is_last_query:
                # 키워드 + 이후
                # print("************ 이전 텍스트가 키워드입니다!")
                totaltweets[-1] = totaltweets[-1] + t.text
                is_last_query = False

            # elif span_bold_tag in t:
            #     # 이전 + 키워드
            #     print("************ 현재 텍스트가 키워드입니다! ************ 키워드:", t.text)
            #     totaltweets[-1] = totaltweets[-1] + t.text
            #     is_query = True

            #  4. 문장 첫 시작에 · 제거
            elif t.text.startswith('·'):
                # print("***** 현재 텍스트 시작이 · 입니다")
                # print(t.text)
                continue

            #  5. 그 외에는 totaltweets 에 해당 text 추가
            else:
                totaltweets.append(t.text)
            # print(t.text)

        # print("totaltweets입니다")
        # print(totaltweets)
        return totaltweets

    # 전처리
    def clear_contents(self, tweet_list, min_length) -> list:
        # 중복 제거(뉴스 기사 임베딩 등)
        tweet_list = list(dict.fromkeys(tweet_list))

        cleaned_list = []
        for i in tweet_list:
            # \n 제거
            i = re.sub(r'\n', ' ', i)

            # 기본 트윗 용어가 포함되면 제거
            if any(s in i for s in self.twitter_text):
                # print("***** 기본 텍스트입니다. continue를 진행합니다, i:", i)
                continue

            # 한글이 한글자라도 없으면 제거
            if not bool(re.search('[가-힣]', i)):
                continue

            # .kr, .com, http~ 제거
            page_str = [".kr", ".com", ".net", "http"]
            if any(s in i for s in page_str):
                continue

            # 광고 제거
            coup = "파트너스 활동으로 수수료를 받을수 있어요"
            if coup in i:
                continue

            # 최소 길이 미만 제거
            if len(i) < min_length:
                continue

            # 공백 제거
            i = i.strip()

            # 맨 앞이 한글, 또는 영어가 아니라면 나올때까지 맨 앞 제거(숫자, 닫는 기호 등)
            while not i[0].isalpha():
                if i[0] == "'" or i[0] == '"' or i[0] == '(' or i[0] == '[':
                    # print("*************현재 i가 quot_mark입니다. i:", i)
                    break
                else:
                    # print("*************현재 i가 not is alpha입니다. i:", i)
                    i = i[1:]
                    # print("*************i의 맨 앞을 삭제했습니다. i:", i)

            cleaned_list.append(i)

        # 한번 더 중복 제거(뉴스 기사 임베딩 등)
        cleaned_list = list(dict.fromkeys(cleaned_list))

        return cleaned_list


if __name__ == "__main__":
    arg_list = ['20211101', '20211104']

    start_date_str = arg_list[0]
    end_date_str = arg_list[1]

    query_list = ["아반떼", "쏘나타"]

    cl = tweet_crawler()
    date_format = '%Y%m%d'
    start_date = datetime.strptime(start_date_str, date_format)
    end_date = datetime.strptime(end_date_str, date_format)

    result = cl.tweet_crawler(query_list, start_date, end_date)  # dataframe
