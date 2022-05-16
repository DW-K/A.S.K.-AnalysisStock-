import re
import sys
import time
from datetime import datetime, timedelta
from selenium import webdriver
from bs4 import BeautifulSoup
import pandas as pd

from database.models import create_tables
from database.word_db_sql import insert_table_tweet, insert_table_company
from sentiment_tweet import sentiment_tweet

driver = webdriver.Chrome('./chromedriver.exe')  # 크롬 드라이버 위치 -> DeprecationWarning 무시하기
driver.set_window_size(800, 900)  # 필요없는 부분의 최소화를 위한 작은 창 설정

# 각 크롤링 결과 저장하기 위한 리스트 선언
# totaltweets = []

# 크롤 대상에 적용된 class style
span_class_txt = "css-901oao css-16my406 r-poiln3"
span_class_bold_text = "css-901oao css-16my406 r-poiln3 r-b88u0q r-bcqeeo r-qvutc0"
span_bold_tag = "r-b88u0q"

# 제거할 트위터 기본 텍스트
twitter_text = ["최신 소식을 놓치지 마세요", "트위터를 사용하면 가장 먼저 알게 됩니다.", "로그인", "가입하기",
                "인기", "최신", "사용자", "사진", "동영상", "새 트윗 보기", "이 스레드 보기", "모두 보기", "팔로우", "공식 트위터"]
user_at = "@"

input_date_format = "%Y%m%d"
date_format = "%Y-%m-%d"

# row 생략 없이 출력
pd.set_option('display.max_rows', None)
# col 생략 없이 출력
pd.set_option('display.max_columns', None)

def url_setting(query, start_date):
    since = '%20since%3A' + start_date.strftime(date_format)
    until = '%20until%3A' + (start_date + timedelta(days=1)).strftime(date_format)  # since + 1일 = 다음날, 즉 그날 하루만 검색
    # min_retweets = '%20min_retweets%3A' + str(min_retweet_value)
    lang = '%20lang%3A' + 'ko'
    url = 'https://twitter.com/search?q=' + query + since + until + lang
    return url


def tweet_crawler(company: str, query: str, start_date: datetime, end_date: datetime = None):
    df = pd.DataFrame(columns=["text", "rt_count", "company", "date"])
    min_text_length = 16  # 크롤링 된 element중 저장할 text의 최소 길이
    # raw_tweet = []
    # curr_date = start_date

    if end_date is None:
        end_date = start_date

    while (end_date - start_date).days > 0:

        url = url_setting(query, start_date)

        driver.get(url)
        html = driver.page_source
        soup = BeautifulSoup(html, 'html.parser')

        lastHeight = driver.execute_script("return document.body.scrollHeight")

        while True:
            print('-------------------------------------------------------date:', start_date)

            # 여기서 크로링 한번 진행
            crawler(soup, query)

            # 스크롤 다운
            driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            time.sleep(2)
            newHeight = driver.execute_script("return document.body.scrollHeight")

            if newHeight != lastHeight:
                html = driver.page_source
                soup = BeautifulSoup(html, 'html.parser')

                # 크롤링 결과 리스트로 저장
                raw_tweet = crawler(soup, query)

                # 결과 전처리
                cleaned_tweet = clear_contents(raw_tweet, min_text_length)

                # 전처리 완료 된 list들을 df에 추가
                text_series = pd.Series(cleaned_tweet, dtype=pd.StringDtype())
                df_to_insert = pd.DataFrame(text_series, columns=['text'])
                df_to_insert['company'] = company
                df_to_insert['rt_count'] = 0
                df_to_insert['date'] = start_date

                df = pd.concat([df, df_to_insert], ignore_index=True)

            else:
                break

            lastHeight = newHeight

        # end of the while
        start_date = start_date + timedelta(days=1)

    driver.quit()
    print(f'{df.shape[0]} is crawled')
    sentiment_tweet(df)


def crawler(soup, query):
    '''
    1. span_class_txt 에 해당하는 span.text 내용을 모두 크롤링
    2. 기본 전처리(트위터 기본 텍스트, 유저아이디 제외(continue)
    3. 원하는 검색어 텍스트는 bold 처리되어 하나의 element 취급되어 크롤링되므로 < 이전 + 키워드 + 이후 > 형식으로 맞춰주기
    4. 문장 첫 시작에 ·제거
    5. 그 외에는 totaltweets 에 해당 text 추가
    '''

    #  1. span_class_txt 에 해당하는 span.text 내용을 모두 크롤링

    tweets = soup.find_all(class_=re.compile(span_class_txt))  # type: 'bs4.element.ResultSet'
    is_last_query = False
    totaltweets = []

    for t in tweets:
        # print(t.text)
        #  2. 기본 전처리(트위터 기본 텍스트, 유저아이디 제외(continue)
        if t.text in twitter_text:
            # print("***** 기본 텍스트입니다. continue를 진행합니다")
            continue
        elif t.text.startswith(user_at):
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
            if len(totaltweets) > 0:
                totaltweets[-1] = totaltweets[-1] + t.text
                is_last_query = False
            else:
                totaltweets.append(t.text)
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
def clear_contents(tweet_list, min_length) -> list:
    # 중복 제거(뉴스 기사 임베딩 등)
    tweet_list = list(dict.fromkeys(tweet_list))

    cleaned_list = []
    for i in tweet_list:
        # \n 제거
        i = re.sub(r'\n', ' ', i)

        # 기본 트윗 용어가 포함되면 제거
        if any(s in i for s in twitter_text):
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


# # 메인함수
# def main():
#     company = "기아"
#     query = "기아 자동차"  # 검색어
#     # until_date = start_date  # 시작날짜 + 1
#     start_date = datetime(year=2021, month=11, day=1)  # 시작날짜
#     end_date = datetime(year=2022, month=3, day=28)  # 끝날짜
#     result = tweet_crawler(company, query, start_date, end_date)  # dataframe
#
#     pd.set_option('display.max_rows', None)
#     pd.set_option('display.max_columns', None)
#     print(result)
#
#     # 엑셀로 출력해보자.
#     # result.to_excel('tweet_df_result_sel2.xlsx')
#     insert_table_tweet(result, company)

if __name__ == "__main__":
    # arg_list = sys.argv[1:]  # argument 받아서 실행
    arg_list = ['LG전자', 'LG전자', '20211103', '20211109']

    company = arg_list[0]
    query = arg_list[1]
    start_date_str = arg_list[2]
    end_date_str = arg_list[3]

    create_tables()
    insert_table_company(company)

    start_date = datetime.strptime(start_date_str, input_date_format)
    end_date = datetime.strptime(end_date_str, input_date_format)

    tweet_crawler(company, query, start_date, end_date)  # dataframe
