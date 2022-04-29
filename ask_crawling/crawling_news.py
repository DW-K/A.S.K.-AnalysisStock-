import os

from bs4 import BeautifulSoup, Comment
from datetime import date, datetime, timedelta
import requests
import pandas as pd
import re
import time
import chardet
import numpy as np

# 한글깨짐 방지
import sys
import io

import Path
from Path import writeToExcel
# from make_word_count import make_word_count

from sentiment import sentiment

from database.word_db_sql import insert_table_news
from database.models import create_tables

# row 생략 없이 출력
pd.set_option('display.max_rows', None)
# col 생략 없이 출력
pd.set_option('display.max_columns', None)

# sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')
# sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding='utf-8')

'''''''''''''''''''''''''''''''''''''''''''''''''''''''''
< naver 뉴스 검색시 리스트 크롤링하는 프로그램 > _select사용
- 크롤링 해오는 것 : 링크,제목,신문사,내용요약본
- 내용요약본  -> 정제 작업 필요
- 리스트 -> 딕셔너리 -> df -> 엑셀로 저장
- 날짜 크롤링 하게되면 6시간전 6일전 이런식으로 뜸 -> 크롤링 검색방식 최신순으로 하는게 good!
'''''''''''''''''''''

# 각 크롤링 결과 저장하기 위한 리스트 선언
title_text = []
link_text = []
source_text = []
date_list = []
contents_text = []
result = {}

# 엑셀로 저장하기 위한 변수
RESULT_PATH_NEWS = Path.RESULT_PATH_NEWS
now = datetime.now()  # 파일이름 현 시간으로 저장하기
today = now.strftime("%y%m%d")

# press_params = {"국민일보": {"id": "articleBody"}, "미주한국일보": {"id": "print_arti"}, "서울경제": {"itemprop": "articleBody"},
#                     "파이낸셜뉴스언론사 선정": {"id": "article_content"}, "에너지경제": {"id": "news_body_area_contents"},
#                     "국제뉴스": {"class": "article-body"}, "연합뉴스": {"class": "story-news article"}, "이데일리": {"class": "news_body"}
#                 }

press_params = {"국민일보": {"id": "articleBody"}, "미주한국일보": {"id": "print_arti"}, "서울경제": {"itemprop": "articleBody"},
                    "파이낸셜뉴스언론사 선정": {"id": "article_content"}, "에너지경제": {"id": "news_body_area_contents"},
                    "국제뉴스": {"class": "article-body"}, "연합뉴스": {"class": "story-news article"}, "이데일리": {"class": "news_body"},
                    "노컷뉴스": {"itemprop": "articleBody"}, "ZDNet Korea언론사 선정": {"itemprop": "articleBody"},
                    "전남매일": {"itemprop": "articleBody"}, "한국경제언론사 선정": {"id": "articletxt"}, "조선비즈언론사 선정": {"itemprop": "articleBody"},
                    "뉴시스": {"itemprop": "articleBody"}, "인사이트코리아": {"itemprop": "articleBody"},
                    "미디어펜": {"class": "boxWidthForm"}, "이코노믹리뷰": {"itemprop": "articleBody"}, "한스경제":{"itemprop": "articleBody"},
                    "뉴스투데이":{"class": "news_body"}
                }

headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36'}


# def news_crawler(start_date, end_date, company, query):
#     page = 0
#     max_page = 10
#
#     df = pd.DataFrame(columns=["title", "언론사", "언론사 링크", "기사 링크", "기사 내용", "query", "날짜"])
#
#     while (end_date - start_date).days > 1:
#         start_date_str = start_date.strftime("%Y-%m-%d")
#         end_date_str = end_date.strftime("%Y-%m-%d")
#
#         temp_df = pd.DataFrame(columns=["title", "언론사", "언론사 링크", "기사 링크", "기사 내용", "query", "날짜"])
#
#         print(f'---------{start_date_str}')
#
#         # 기사 링크 수집
#         while page < max_page:
#             search_url = "https://search.naver.com/search.naver?where=news&query=" + query + "&sort=" + "0" + "&nso=so%3Ar%2Cp%3Afrom" + \
#                          start_date_str + "to" + start_date_str + "%2Ca%3A&start=" + str(page*10 + 1)
#             response = requests.get(search_url)  # html 가져오기
#             response.raise_for_status()
#             search_html = response.text
#             soup = BeautifulSoup(search_html, "lxml")
#
#             info_press = soup.find_all("a", attrs={"class": "info press"})  # press: 언론사 , 언론사 정보
#
#             for l in info_press:        # 기사 링크와 언론사 링크 가져오기
#                 news = l.parent.parent.find_next_sibling("a")
#                 if l.get_text() in press_params.keys():
#                     df_row = {"title": news.get_text(), "언론사": l.get_text(), "언론사 링크": l['href'], "기사 링크": news["href"],
#                               "날짜": start_date, "query": query}
#                     df = df.append(df_row, ignore_index=True)
#                 else:
#                     # print(f'{l.get_text()} is not in press_params')
#                     pass
#
#             # df.drop_duplicates(subset=["title"], ignore_index=True, inplace=True)   # title 기준으로 중복제거
#
#             for i in range(df.shape[0]):    # 본문 기사 크롤링
#                 df["기사 내용"].iloc[i, :] = get_contents(df.iloc[i, :]["기사 링크"], df.iloc[i, :]["언론사"])
#
#             page += 1
#             time.sleep(1)
#
#         df = df[df['기사 내용'].str.len() > 10]  # 기사 내용 10글자 이하 drop
#
#     # df.to_csv(fr"dataset/crawling.csv", encoding="utf-8-sig")
#     insert_table_news(df, company)
#     # print(df)

def news_crawler(company, query, start_date, end_date=None):
    df = pd.DataFrame(columns=["title", "언론사", "언론사 링크", "기사 링크", "기사 내용", "query", "날짜"])

    if end_date is None:
        end_date = start_date

    while (end_date - start_date).days >= 0:
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
            response = requests.get(search_url)  # html 가져오기
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
                temp_df["기사 내용"].iloc[i, :] = get_contents(temp_df.iloc[i, :]["기사 링크"], temp_df.iloc[i, :]["언론사"])

            page += 1
            time.sleep(1)

        temp_df = temp_df[temp_df['기사 내용'].str.len() > 10]  # 기사 내용 10글자 이하 drop

        df = df.append(temp_df, ignore_index=True)

        start_date = start_date + timedelta(days=1)

    df.drop_duplicates(subset=["title"], ignore_index=True, inplace=True)  # title 기준으로 중복제거
    # df.to_csv(fr"dataset/crawling.csv", encoding="utf-8-sig")
    insert_table_news(df, company)
    print(df)


def get_contents(url, press):
    news_response = requests.get(url, headers=headers)
    news_response.raise_for_status()
    news_html = news_response.content
    news_html_decoded = news_html.decode(chardet.detect(news_html)["encoding"], "replace")  # chardet: html 문서의 encoding 형식 추측해주는 lib

    news_soup = BeautifulSoup(news_html_decoded, "lxml")

    contents = ""
    target_html = news_soup.find_all(attrs=press_params[press])     # 전역 변수로 선언된 press_param 딕셔너리 기준으로 본문 추출

    for target_part in target_html:
        for element in target_part(text=lambda text: isinstance(text, Comment)):  # remove comment
            element.extract()

        for line in target_part.children:
            removed_newline = str(line).replace("\n", "")      # 줄바꿈 제거
            removed_double_tag = re.sub(r'<(?!/|p).+?>.+?</.+?>', '', removed_newline,
                                        0)  # remove double tag except <p>
            removed_single_tag = re.sub(r'<.+?>', '', removed_double_tag, 0).strip()    # remove single tag
            if len(removed_single_tag) > 5:     # 5 글자 이상인 문장만 추가
                contents += removed_single_tag

    return contents


if __name__ == "__main__":
    date_format = "%Y%m%d"
    arg_list = sys.argv[1:]  # argument 받아서 실행
    # arg_list = ['기아', '기아 자동차', '20211022', '20211030']

    company = arg_list[0]
    query = arg_list[1]
    start_date_str = arg_list[2]
    end_date_str = arg_list[3]

    start_date = datetime.strptime(start_date_str, date_format)
    end_date = datetime.strptime(end_date_str, date_format)

    create_tables()
    # start_date = date(2002, 6, 20)
    # end_date = date(2002, 11, 20)

    news_crawler(company, query, start_date, end_date)
