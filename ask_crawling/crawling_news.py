from user_agent import generate_user_agent, generate_navigator

from bs4 import BeautifulSoup, Comment
from datetime import date, datetime, timedelta
import requests
import pandas as pd
import re
import time
import chardet
import numpy as np

import Path
# from make_word_count import make_word_count
from database.word_db_sql import insert_table_company

from sentiment import sentiment
from database.models import create_tables

# row 생략 없이 출력
pd.set_option('display.max_rows', None)
# col 생략 없이 출력
pd.set_option('display.max_columns', None)

now = datetime.now()  # 파일이름 현 시간으로 저장하기
today = now.strftime("%y%m%d")


# press_params = {"국민일보": {"id": "articleBody"}, "미주한국일보": {"id": "print_arti"}, "서울경제": {"itemprop": "articleBody"},
#                     "파이낸셜뉴스언론사 선정": {"id": "article_content"}, "에너지경제": {"id": "news_body_area_contents"},
#                     "국제뉴스": {"class": "article-body"}, "연합뉴스": {"class": "story-news article"}, "이데일리": {"class": "news_body"}
#                 }

press_params = {"국민일보": {"id": "articleBody"}, "미주한국일보": {"id": "print_arti"}, "서울경제": {"itemprop": "articleBody"},
                "파이낸셜뉴스언론사 선정": {"id": "article_content"}, "에너지경제": {"id": "news_body_area_contents"},
                "국제뉴스": {"class": "article-body"}, "연합뉴스": {"class": "story-news article"},
                "이데일리": {"class": "news_body"},
                "노컷뉴스": {"itemprop": "articleBody"}, "ZDNet Korea언론사 선정": {"itemprop": "articleBody"},
                "전남매일": {"itemprop": "articleBody"}, "한국경제언론사 선정": {"id": "articletxt"},
                "조선비즈언론사 선정": {"itemprop": "articleBody"},
                "뉴시스": {"itemprop": "articleBody"}, "인사이트코리아": {"itemprop": "articleBody"},
                "미디어펜": {"class": "boxWidthForm"}, "이코노믹리뷰": {"itemprop": "articleBody"},
                "한스경제": {"itemprop": "articleBody"},
                "뉴스투데이": {"class": "news_body"}
                }

# headers = {
#     'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36'}


def news_crawler(company, query_list, start_date, end_date=None):
    if end_date is None:
        end_date = start_date

    # if query_list is not list:
    #     query_list = [query_list]

    while (end_date - start_date).days >= 0:
        request_headers = {
            'User-Agent': generate_user_agent(os='win', device_type='desktop')
        }

        print(request_headers)

        for query in query_list:
            df = pd.DataFrame(columns=["title", "언론사", "언론사 링크", "기사 링크", "기사 내용", "query", "날짜"])

            page = 0
            max_page = 10

            start_date_str = start_date.strftime("%Y-%m-%d")
            end_date_str = end_date.strftime("%Y-%m-%d")

            temp_df = pd.DataFrame(columns=["title", "언론사", "언론사 링크", "기사 링크", "기사 내용", "query", "날짜"])

            print(f'---------{start_date_str}')

            # 기사 링크 수집
            while page < max_page:
                search_url = "https://search.naver.com/search.naver?where=news&query=" + query + "&sort=" + "0" + "&nso=so%3Ar%2Cp%3Afrom" + \
                             start_date_str + "to" + end_date_str + "%2Ca%3A&start=" + str(page * 10 + 1)

                try:
                    response = requests.get(search_url, headers=request_headers)  # html 가져오기
                    response.raise_for_status()
                except:
                    time.sleep(2)
                    response = requests.get(search_url, headers=request_headers)  # html 가져오기
                    response.raise_for_status()

                search_html = response.text
                soup = BeautifulSoup(search_html, "lxml")

                info_press = soup.find_all("a", attrs={"class": "info press"})  # press: 언론사 , 언론사 정보

                for l in info_press:  # 기사 링크와 언론사 링크 가져오기
                    news = l.parent.parent.find_next_sibling("a")
                    if l.get_text() in press_params.keys():
                        df_row = {"title": news.get_text(), "언론사": l.get_text(), "언론사 링크": l['href'], "기사 링크": news["href"],
                                  "날짜": start_date, "query": query}
                        temp_df = temp_df.append(df_row, ignore_index=True)
                    else:
                        # print(f'{l.get_text()} is not in press_params')
                        pass
                for i in range(temp_df.shape[0]):  # 본문 기사 크롤링
                    temp_df.iloc[i, :]["기사 내용"] = get_contents(temp_df.iloc[i, :]["기사 링크"], temp_df.iloc[i, :]["언론사"], headers=request_headers)

                page += 1
                time.sleep(1)

            temp_df = temp_df[temp_df['기사 내용'].str.len() > 10]  # 기사 내용 10글자 이하 drop

            df = df.append(temp_df.copy(), ignore_index=True)

            start_date = start_date + timedelta(days=1)

            df = df.drop_duplicates(subset=["title"], ignore_index=True)  # title 기준으로 중복제거
            df['company'] = company

            sentiment(df.copy())

            del df


def get_contents(url, press, headers):
    news_response = requests.get(url, headers=headers)
    news_response.raise_for_status()
    news_html = news_response.content
    news_html_decoded = news_html.decode(chardet.detect(news_html)["encoding"],
                                         "replace")  # chardet: html 문서의 encoding 형식 추측해주는 lib

    news_soup = BeautifulSoup(news_html_decoded, "lxml")

    contents = ""
    target_html = news_soup.find_all(attrs=press_params[press])  # 전역 변수로 선언된 press_param 딕셔너리 기준으로 본문 추출

    for target_part in target_html:
        for element in target_part(text=lambda text: isinstance(text, Comment)):  # remove comment
            element.extract()

        for line in target_part.children:
            removed_newline = str(line).replace("\n", "")  # 줄바꿈 제거
            removed_double_tag = re.sub(r'<(?!/|p).+?>.+?</.+?>', '', removed_newline,
                                        0)  # remove double tag except <p>
            removed_single_tag = re.sub(r'<.+?>', '', removed_double_tag, 0).strip()  # remove single tag
            if len(removed_single_tag) > 5:  # 5 글자 이상인 문장만 추가
                contents += removed_single_tag

    return contents


if __name__ == "__main__":
    date_format = "%Y%m%d"
    # arg_list = sys.argv[1:]  # argument 받아서 실행
    arg_list = ['현대차', '기아 자동차', '20210101', '20220428']

    company = arg_list[0]
    query = arg_list[1]
    start_date_str = arg_list[2]
    end_date_str = arg_list[3]

    insert_table_company(company)
    create_tables()

    start_date = datetime.strptime(start_date_str, date_format)
    end_date = datetime.strptime(end_date_str, date_format)

    query_list = ["정의선", "아반떼",
                  "쏘나타", "팰리세이드", "제네시스", "GV70", "GV80", "G80",
                  "현대차", "기아차", "코나EV", "아이오닉5"]

    news_crawler(company, query_list, start_date, end_date)
