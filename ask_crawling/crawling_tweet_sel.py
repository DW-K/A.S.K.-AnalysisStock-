import re
import time
import datetime as dt
from selenium import webdriver
from bs4 import BeautifulSoup

driver = webdriver.Chrome('./chromedriver.exe')  # 크롬 드라이버 위치 -> DeprecationWarning 무시하기
driver.set_window_size(800, 900)  # 필요없는 부분의 최소화를 위한 작은 창 설정

# 각 크롤링 결과 저장하기 위한 리스트 선언
totaltweets = []

# 크롤 대상에 적용된 class style
span_class_txt = "css-901oao css-16my406 r-poiln3"

# 수집되면 제거할 기본 텍스트들
twitter_text = ["최신 소식을 놓치지 마세요", "트위터를 사용하면 가장 먼저 알게 됩니다.", "로그인", "가입하기",
                "인기", "최신", "사용자", "사진", "동영상", "새 트윗 보기", "이 스레드 보기"]
user_at = "@"


# 크롤링 시작
def crawl_start(query, startdate, untildate, enddate, min_retweet_value, min_length):
    while not enddate == startdate:
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
                crawler(soup, query)  # 크롤링

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
    4. 그 외에는 totaltweets 에 해당 text 추가
    '''

    #  1. span_class_txt 에 해당하는 span.text 내용을 모두 크롤링

    tweets = soup.find_all(class_=re.compile(span_class_txt))  # type: 'bs4.element.ResultSet'
    is_query = False

    for t in tweets:
        #  2. 기본 전처리(트위터 기본 텍스트, 유저아이디 제외(continue)
        if t.text in twitter_text:
            print("***** 기본 텍스트입니다. continue를 진행합니다")
            continue
        elif t.text.startswith(user_at):
            print("***** 유저 아이디입니다. continue를 진행합니다")
            continue

        #  3. 원하는 검색어 텍스트는 bold 처리되어 하나의 element 취급되어 크롤링되므로 < 이전 + 키워드 + 이후 > 형식으로 맞춰주기
        elif is_query:
            # 키워드 + 이후
            print("************ 이전 텍스트가 키워드입니다!")
            totaltweets[-1] = totaltweets[-1] + t.text
            is_query = False
        elif t.text == query:
            # 이전 + 키워드
            print("************ 현재 텍스트가 키워드입니다! ************")
            totaltweets[-1] = totaltweets[-1] + t.text
            is_query = True

        #  4. 그 외에는 totaltweets 에 해당 text 추가
        else:
            totaltweets.append(t.text)
        print(t.text)


def clear_contents(tweet_list, min_length):
    # 뉴스 기사 임베딩 등 중복 제거
    tweet_list = list(dict.fromkeys(tweet_list))

    cleaned_list = []
    for i in tweet_list:
        i = re.sub(r'\n', ' ', i)  # \n 제거
        if len(i) > min_length:  # 최소 길이 이상만 cleaned_list 에 추가
            cleaned_list.append(i)

    return cleaned_list


# 메인함수
def main():
    query = "현대차" # 검색어
    startdate = dt.date(year=2022, month=1, day=4)  # 시작날짜
    untildate = startdate + dt.timedelta(days=1)  # 시작날짜 + 1
    enddate = dt.date(year=2022, month=1, day=6)  # 끝날짜
    min_retweet_value = 1  # 최소 1 RT 이상의 글만
    min_text_length = 10  # 크롤링 된 element중 저장할 text의 최소 길이

    result = crawl_start(query, startdate, untildate, enddate, min_retweet_value, min_text_length)

    import pprint
    print("============================= 결과 =============================")
    pprint.pprint(result)


if __name__ == "__main__":
    main()
