import os

from bs4 import BeautifulSoup
from datetime import datetime, timedelta
import requests
import pandas as pd
import re

# 한글깨짐 방지
import sys
import io

import Path
from Path import writeToExcel

from sentiment import sentiment

sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding='utf-8')

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

target_col = "contents"


# 내용 정제화 함수
def contents_cleansing(contents):
    first_cleansing_contents = re.sub('<dl>.*?</a> </div> </dd> <dd>', '', str(contents)).strip()  # 앞에 필요없는 부분 제거
    second_cleansing_contents = re.sub('<ul class="relation_lst">.*?</dd>', '',
                                       first_cleansing_contents).strip()  # 뒤에 필요없는 부분 제거 (새끼 기사)
    third_cleansing_contents = re.sub('<.+?>', '', second_cleansing_contents).strip()
    contents_text.append(third_cleansing_contents)
    # print(contents_text)


# 크롤링 시작
def crawler(category, companyName, maxpage, query, sort, s_date, e_date):
    s_from = s_date.replace(".", "")
    e_to = e_date.replace(".", "")
    page = 1

    # 새로 만들 파일이름 지정
    output_file_name = f'{companyName}_{e_date}_n.xlsx'
    filePath = fr"{RESULT_PATH_NEWS}\{category}\{companyName}\news"
    output_path = fr"{filePath}\{output_file_name}"
    Path.createFolder(filePath)

    df_result = pd.DataFrame()

    maxpage_t = (int(maxpage) - 1) * 10 + 1  # 11= 2페이지 21=3페이지 31=4페이지  ...81=9페이지 , 91=10페이지, 101=11페이지
    while page <= maxpage_t:
        url = "https://search.naver.com/search.naver?where=news&query=" + query + "&sort=" + sort + "&ds=" + s_date + "&de=" + e_date + "&nso=so%3Ar%2Cp%3Afrom" + s_from + "to" + e_to + "%2Ca%3A&start=" + str(
            page)
        response = requests.get(url)
        html = response.text

        # 뷰티풀소프의 인자값 지정
        soup = BeautifulSoup(html, 'html.parser')

        # <a>태그에서 제목과 링크주소 (a 태그 중 class 명이 news_tit인 것)
        atags = soup.find_all('a', 'news_tit')
        for atag in atags:
            title = atag.get('title')
            title_text.append(title)  # 제목
            link_text.append(atag['href'])  # 링크주소

        # 신문사 추출 (a 태그 중 class 명이 info press인 것)
        source_lists = soup.find_all('a', 'info press')
        for source_list in source_lists:
            source_text.append(source_list.text)  # 신문사

        # span_lists= soup.find_all('span','info')
        # for span_list in span_lists:
        #     span_text.append(span_list.text)
        dates = [date.get_text() for date in soup.find_all('span', {'class': 'info'})]

        for date in dates:
            if re.search(r'\d+.\d+.\d+.', date) is not None:
                date_list.append(date)
            if re.search(r'\d+분 전', date) is not None:
                date_list.append(now.strftime('%Y.%m.%d'))
            if re.search(r'\d+시간 전', date) is not None:
                date_list.append(now.strftime('%Y.%m.%d'))
            if re.search(r'\d+일 전', date) is not None:
                date_list.append((now - timedelta(days=int(date.split('일')[0]))).strftime('%Y.%m.%d'))

        # 본문요약본 (a 태그 중 class 명이 api_txt_lines dsc_txt_wrap인 것)
        contents_lists = soup.find_all('a', 'api_txt_lines dsc_txt_wrap')
        for contents_list in contents_lists:
            contents_cleansing(contents_list)  # 본문요약 정제화

        # 모든 리스트 딕셔너리형태로 저장
        result = {"title": title_text, "source": source_text, "span": date_list, "contents": contents_text,
                  "link": link_text}
        df = pd.DataFrame(result)  # df로 변환
        df.index.name = "index"

        df_result = pd.concat([df_result, df], axis=0)
        page += 10

    if os.path.exists(output_path):
        writeToExcel(output_path=output_path, df=df_result, sheet_name=query, isWrite=False)
    else:
        writeToExcel(output_path=output_path, df=df_result, sheet_name=query, isWrite=True)

    return filePath, output_file_name


# 메인함수
def main():
    info_main = input("=" * 50 + "\n" + "입력 형식에 맞게 입력해주세요." + "\n" + " 시작하시려면 Enter를 눌러주세요." + "\n" + "=" * 50)
    category = "에스엠"
    maxpage = input("최대 크롤링할 페이지 수 입력하시오: ")  # 10,20...
    query = input("검색어 입력: ")  # 네이버, 부동산...
    sort = input("뉴스 검색 방식 입력(관련도순=0  최신순=1  오래된순=2): ")  # 관련도순=0  최신순=1  오래된순=2
    s_date = input("시작날짜 입력(2021.06.01):")  # 2021.06.01
    e_date = input("끝날짜 입력(2021.07.00):")  # 2021.07.
    crawler(category, maxpage, query, sort, s_date, e_date)


if __name__ == "__main__":
    arg_list = sys.argv[1:]  # argument 받아서 실행
    category = arg_list[0]
    companyName = arg_list[1]
    maxpage = int(arg_list[2])
    query = arg_list[3]
    sort = arg_list[4]
    s_date = arg_list[5]
    e_date = arg_list[6]

    # filePath = crawler(category="에스엠", maxpage=20, query="이수만", sort="0", s_date="20211024", e_date="20201025")

    filePath, output_file_name = crawler(category=category, companyName=companyName, maxpage=query, sort=sort,
                                         s_date=s_date, e_date=e_date)

    sentiment(filePath=filePath, output_file_name=output_file_name, sheetName=query, target_col=target_col)
