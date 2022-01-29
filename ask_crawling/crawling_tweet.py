import datetime
import os
import sys

import pandas as pd
from dateutil.parser import parse

import twitter
import Path
from Path import RESULT_PATH_TWEET
from Path import writeToExcel
from sentiment import sentiment
import config

twitter_api = twitter.Api(consumer_key=config.twitter_consumer_key,
                          consumer_secret=config.twitter_consumer_secret,
                          access_token_key=config.twitter_access_token,
                          access_token_secret=config.twitter_access_secret)

http = 'http'
encode_method = 'utf-8'
keyword_trend = []
output_file_name = []

target_col = "text"


def clear_link(text):
    # print("*** 사진 또는 영상 링크 존재. 링크를 제거할 예정입니다. 위치:", text.find(http))
    splited_link = text.split(http, maxsplit=2)
    # for i in range(len(splited_link)):
        # print('splited_link', i, "번째:", splited_link[i])
    text = splited_link[0]
    # print('*** 링크 제거 후의 내용:\n', text)
    return text


def clear_id(text, mention_id):
    user_id = []
    # print("*** 아이디 태그 존재. 제거할 예정입니다.")
    for i in range(len(mention_id)):
        # print(mention_id[i].screen_name)

        user_id.append(mention_id[i].screen_name)
        # print('user_mentions:', user_id)
        text = text.replace(user_id[i], "")
        text = text.replace("@", "")

    # print('*** 아이디 제거 후의 내용:\n', text)
    return text


def clear_comma(text):
    # print("*** 텍스트 내의 콤마를 제거합니다.")
    text = text.replace(",", "")
    # print('*** 콤마 제거 후의 내용:\n', text)
    return text


def get_date(created_at):
    created_at = created_at.replace('+0000', '')
    dt = parse(created_at)
    dt = dt + datetime.timedelta(hours=9)
    return dt


def add_content(text, content):
    text = content + "," + text
    return text


def tweet_crawler(category, companyName, query, num, s_date, e_date):
    datetime_list = []
    rt_list = []
    text_list = []

    output_file_name = f'{companyName}_{e_date}_t.xlsx'
    filePath = fr"{RESULT_PATH_TWEET}\{category}\{companyName}\tweet"
    output_path = fr"{filePath}\{output_file_name}"
    Path.createFolder(filePath)

    num = (int(num) - 1) * 10 + 1
    dateFormat = ("%Y-%m-%d")

    s_dateObj = datetime.datetime.strptime(s_date, "%Y%m%d")
    s_date = s_dateObj.strftime(dateFormat)

    e_dateObj = datetime.datetime.strptime(e_date, "%Y%m%d")
    e_dateObj = e_dateObj + datetime.timedelta(days=1)
    e_date = e_dateObj.strftime(dateFormat)

    print(f'{s_date}, {e_date}')

    statuses = twitter_api.GetSearch(term=query, count=num, lang='ko', since=s_date, until=e_date)  # 한국어로 작성된 트윗만

    # print(statuses)
    # print(statuses[0]["created_at"])

    for status in statuses:
        # print(statuses)
        text = status.text
        # RT 확인
        rt_count = status.retweet_count
        if status.retweeted_status:
            # retweet_count가 0 이상이면(rt 가 존재하면) 해당 내용의 text를 text로 지정. RT @아이디를 빠진 데이터임.
            text = status.retweeted_status.text
        else:
            rt_count = 0
            continue  # rt_count가 0이면 해당 트윗은 버리고 다음 트윗으로

        # 멘션에 아이디 제거
        # print('status.user_mentions:', status.user_mentions)
        # print(len(status.user_mentions))
        if len(status.user_mentions):
            text = clear_id(text, status.user_mentions)

        # 첨부된 이미지 제거
        if text.find(http) != -1:
            text = clear_link(text)

        # 텍스트의 엔터 제거
        text = text.replace('\n', ' ')

        # 텍스트에 포함된 , 제거(csv 파일 저장 오류 방지)
        text = clear_comma(text)

        # text 정리 완료, list로 만들기
        text_list.append(text)

        # RT 횟수 list로 만들기
        rt_list.append(rt_count)

        # 날짜 list로 만들기
        datetime_list.append(get_date(status.created_at))

    if len(text_list) >= 1:
        df = pd.DataFrame(list(zip(datetime_list, rt_list, text_list)), columns=['datetime', 'rt_count', 'text'])
        # df = pd.DataFrame(list(zip(datetime_list, text_list)), columns=['datetime', 'text'])
        # print(df)

        # df.columns = ['keyword', 'datetime', 'rt_count', 'text']
        df = df.drop_duplicates('text', ignore_index=True)  # 중복 제거
        df.index.name = "index"

        df.dropna(axis=0, inplace=True)
        print(df)

        if os.path.exists(output_path):
            writeToExcel(output_path=output_path, df=df, sheet_name=query, isWrite=False)
        else:
            writeToExcel(output_path=output_path, df=df, sheet_name=query, isWrite=True)
    else:
        return False, False

    return filePath, output_file_name


if __name__ == "__main__":
    arg_list = sys.argv[1:]  # argument 받아서 실행
    category = arg_list[0]
    companyName = arg_list[1]
    num = arg_list[2]
    query = arg_list[3]
    s_date = arg_list[4]
    e_date = arg_list[5]

    filePath, output_file_name =tweet_crawler(category=category, companyName=companyName, query=query, num=num, s_date=s_date, e_date=e_date)

    if filePath is not False:
        sentiment(filePath=filePath, output_file_name=output_file_name, sheetName=query, target_col=target_col)
