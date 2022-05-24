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
from database.models import create_tables
from database.word_db_sql import insert_table_company

from sentiment import sentiment

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
pd.set_option('mode.chained_assignment', None)


def news_crawler(company, query_list, start_date, end_date=None):
    if end_date is None:
        end_date = start_date

    if type(query_list) is not list:
        query_list = [query_list]
    print(query_list)
    date_format = '%Y%m%d'

    while (end_date - start_date).days >= 0:
        print(f'---------{start_date}')
        for query in query_list:
            print(f'------{query}')
            request_headers = {
                'User-Agent': generate_user_agent(os='win', device_type='desktop')
            }

            temp_df = pd.DataFrame(columns=["title", "언론사", "언론사 링크", "기사 링크", "기사 내용", "query", "날짜"])

            page = 0
            max_page = 20

            start_date_str = start_date.strftime(date_format)
            end_date_str = end_date.strftime(date_format)

            # temp_df = pd.DataFrame(columns=["title", "언론사", "언론사 링크", "기사 링크", "기사 내용", "query", "날짜"])

            # 기사 링크 수집
            while page < max_page:
                search_url = "https://search.naver.com/search.naver?where=news&query=" + query + "&sort=" + "0" + "&nso=so%3Ar%2Cp%3Afrom" + \
                             start_date_str + "to" + start_date_str + "%2Ca%3A&start=" + str(page * 10 + 1)

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
                        df_row = {"title": news.get_text(), "언론사": l.get_text(), "언론사 링크": l['href'],
                                  "기사 링크": news["href"],
                                  "날짜": start_date, "query": query}
                        temp_df = temp_df.append(df_row, ignore_index=True)
                    else:
                        # print(f'{l.get_text()} is not in press_params')
                        pass
                # print(temp_df)

                # print(f'temp_df\'s shape: {temp_df.shape}')
                for i in range(temp_df.shape[0]):  # 본문 기사 크롤링
                    content = get_contents(temp_df.iloc[i, :]["기사 링크"], temp_df.iloc[i, :]["언론사"],
                                           request_headers=request_headers)
                    if content and len(content) <= 4096:
                        temp_df["기사 내용"].iloc[i, :] = content
                    time.sleep(0.05)

                page += 1

                if page % 5 == 0 and page != 0:
                    time.sleep(2)

            df = temp_df[temp_df['기사 내용'].str.len() > 10].copy()  # 기사 내용 10글자 이하 drop

            # print(f'df\'s shape {df.shape}')

            df = df.drop_duplicates(subset=["title"], ignore_index=True)  # title 기준으로 중복제거
            df['company'] = company

            print(f'{df.shape[0]} is crawled')

            sentiment(df.copy())

        start_date = start_date + timedelta(days=1)


def get_contents(url, press, request_headers):
    try:
        news_response = requests.get(url, headers=request_headers)
        news_response.raise_for_status()
    except:
        print('호스트 차단')
        request_headers = {
            'User-Agent': generate_user_agent(os='win', device_type='desktop')
        }
        time.sleep(2)
        try:
            news_response = requests.get(url, headers=request_headers)
            news_response.raise_for_status()
        except:
            return None

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
    arg_list1 = ['현대차', '현대차', '20220420', '20220430']
    arg_list2 = ['LG전자', 'LG전자', '20220420', '20220430']
    arg_list3 = ['카카오', '카카오', '20220420', '20220430']
    arg_list4 = ['하이브', '하이브', '20220420', '20220430']

    arg_list_list = [arg_list1, arg_list2, arg_list3, arg_list4]

    for arg_list in arg_list_list:
        company = arg_list[0]
        query_list = arg_list[1]
        start_date_str = arg_list[2]
        end_date_str = arg_list[3]

        create_tables()
        insert_table_company(company)

        start_date = datetime.strptime(start_date_str, date_format)
        end_date = datetime.strptime(end_date_str, date_format)

        news_crawler(company, query_list, start_date, end_date)
