import re
import time
import datetime as dt
from selenium import webdriver
from bs4 import BeautifulSoup
import pandas as pd

from database.word_db_sql import insert_table_tweet, insert_table_company

driver = webdriver.Chrome('./chromedriver.exe')  # 크롬 드라이버 위치 -> DeprecationWarning 무시하기
driver.set_window_size(800, 900)  # 필요없는 부분의 최소화를 위한 작은 창 설정

# 각 크롤링 결과 저장하기 위한 리스트 선언
# totaltweets = []

# 크롤 대상에 적용된 class style
span_class_txt = "css-901oao css-16my406 r-poiln3"

# 제거할 트위터 기본 텍스트
twitter_text = ["최신 소식을 놓치지 마세요", "트위터를 사용하면 가장 먼저 알게 됩니다.", "로그인", "가입하기",
                "인기", "최신", "사용자", "사진", "동영상", "새 트윗 보기", "이 스레드 보기"]
user_at = "@"


# 크롤링 시작
def crawl_start(query, startdate, untildate, enddate, min_retweet_value, min_length):
    totaltweets = []
    since = '%20since%3A' + str(startdate)
    until = '%20until%3A' + str(untildate)
    min_retweets = '%20min_retweets%3A' + str(min_retweet_value)
    lang = '%20lang%3A' + 'ko'

    url = 'https://twitter.com/search?q=' + query + since + until + min_retweets + lang

    driver.get(url)
    html = driver.page_source
    soup = BeautifulSoup(html, 'html.parser')

    lastHeight = driver.execute_script("return document.body.scrollHeight")

    while True:
        print('-------------------------------------------------------date:', startdate)

        # 여기서 크로링 한번 진행
        crawler(soup, query)

        # 스크롤 다운하며 계속 크롤링 진행
        driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(1)

        newHeight = driver.execute_script("return document.body.scrollHeight")

        if newHeight != lastHeight:
            html = driver.page_source
            soup = BeautifulSoup(html, 'html.parser')
            totaltweets = crawler(soup, query)  # 크롤링

        else:
            startdate = untildate
            untildate += dt.timedelta(days=1)
            break

        lastHeight = newHeight

    # end of the while
    return clear_contents(totaltweets, min_length)


def crawler(soup, query):
    '''
    1. span_class_txt 에 해당하는 span.text 내용을 모두 크롤링
    2. 기본 전처리(트위터 기본 텍스트, 유저아이디 제외(continue)
    3. 원하는 검색어 텍스트는 bold 처리되어 하나의 element 취급되어 크롤링되므로 < 이전 + 키워드 + 이후 > 형식으로 맞춰주기
    4. 문장 첫 시작에 · 제거
    5. 그 외에는 totaltweets 에 해당 text 추가
    '''

    #  1. span_class_txt 에 해당하는 span.text 내용을 모두 크롤링

    tweets = soup.find_all(class_=re.compile(span_class_txt))  # type: 'bs4.element.ResultSet'
    is_query = False
    totaltweets = []

    for t in tweets:
        #  2. 기본 전처리(트위터 기본 텍스트, 유저아이디 제외(continue)
        if t.text in twitter_text:
            # print("***** 기본 텍스트입니다. continue를 진행합니다")
            continue
        elif t.text.startswith(user_at):
            # print("***** 유저 아이디입니다. continue를 진행합니다")
            continue

        #  3. 원하는 검색어 텍스트는 bold 처리되어 하나의 element 취급되어 크롤링되므로 < 이전 + 키워드 + 이후 > 형식으로 맞춰주기
        elif is_query:
            # 키워드 + 이후
            # print("************ 이전 텍스트가 키워드입니다!")
            totaltweets[-1] = totaltweets[-1] + t.text
            is_query = False
        elif t.text == query:
            # 이전 + 키워드
            # print("************ 현재 텍스트가 키워드입니다! ************")
            totaltweets[-1] = totaltweets[-1] + t.text
            is_query = True

        #  4. 문장 첫 시작에 · 제거
        elif t.text.startswith('·'):
            # print("***** 현재 텍스트 시작이 · 입니다")
            # print(t.text)
            continue


        #  5. 그 외에는 totaltweets 에 해당 text 추가
        else:
            totaltweets.append(t.text)
        # print(t.text)

    return totaltweets


# 전처리
def clear_contents(tweet_list, min_length):

    # 중복 제거(뉴스 기사 임베딩 등)
    tweet_list = list(dict.fromkeys(tweet_list))

    cleaned_list = []
    for i in tweet_list:
        # \n 제거
        i = re.sub(r'\n', ' ', i)

        # 최소 길이 미만 제거
        if len(i) < min_length:
            continue

        # 한글이 한글자라도 없으면 제거
        if not bool(re.search('[가-힣]', i)):
            continue

        # .kr, .com, http~ 제거
        page_str = [".kr", ".com", "http"]
        if any(s in i for s in page_str):
            continue

        cleaned_list.append(i)

    return cleaned_list


# 메인함수
def main():
    company = "기아"
    query = "기아 자동차" # 검색어
    start_date = dt.date(year=2012, month=1, day=13)  # 시작날짜
    until_date = start_date  # 시작날짜 + 1
    end_date = dt.date(year=2022, month=3, day=21)  # 끝날짜
    min_retweet_value = 1  # 최소 1 RT 이상의 글만
    min_text_length = 10  # 크롤링 된 element중 저장할 text의 최소 길이

    while (end_date - start_date).days > 0:
        # result type: list
        result = crawl_start(query, start_date, start_date + dt.timedelta(days=1), start_date, min_retweet_value, min_text_length)

        df_result = pd.DataFrame({"text": result, "date": start_date, 'rt_count': 0})
        # df_result['rt_count'] = 0
        # df_result['date'] = start_date
        # df_result.to_excel('tweet_df_result.xlsx')
        # print(df_result)
        insert_table_tweet(df_result, company)

        start_date = start_date + dt.timedelta(days=1)


if __name__ == "__main__":
    # insert_table_company('기아')
    main()
