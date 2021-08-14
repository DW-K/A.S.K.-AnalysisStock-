import datetime
import pandas as pd
from dateutil.parser import parse

import twitter
import Path

twitter_consumer_key = "HO2ximfzQ99cNXP9KNVQG0wqU"
twitter_consumer_secret = "S5bie299yxJBZJD9Ig74mMNtZ9muvJ42P1bz8eZxbZR6hnUhha"
twitter_access_token = "1071250485078720512-mw9VzarzdCIF9rvZJ4ps95NqeFWTwZ"
twitter_access_secret = "pSw8b7nfY5dT30H9nSrnuzJsSj2AHkB9qwGz3dmuQXOdq"

twitter_api = twitter.Api(consumer_key=twitter_consumer_key,
                          consumer_secret=twitter_consumer_secret,
                          access_token_key=twitter_access_token,
                          access_token_secret=twitter_access_secret)

http = 'http'
encode_method = 'utf-8'
keyword_trend = []
output_file_name = []


def clear_link(text):
    print("*** 사진 또는 영상 링크 존재. 링크를 제거할 예정입니다. 위치:", text.find(http))
    splited_link = text.split(http, maxsplit=2)
    for i in range(len(splited_link)):
        print('splited_link', i, "번째:", splited_link[i])
    text = splited_link[0]
    print('*** 링크 제거 후의 내용:\n', text)
    return text


def clear_id(text, mention_id):
    user_id = []
    print("*** 아이디 태그 존재. 제거할 예정입니다.")
    for i in range(len(mention_id)):
        print(mention_id[i].screen_name)

        user_id.append(mention_id[i].screen_name)
        print('user_mentions:', user_id)
        text = text.replace(user_id[i], "")
        text = text.replace("@", "")

    print('*** 아이디 제거 후의 내용:\n', text)
    return text


def clear_comma(text):
    print("*** 텍스트 내의 콤마를 제거합니다.")
    text = text.replace(",", "")
    print('*** 콤마 제거 후의 내용:\n', text)
    return text


def get_date(created_at):
    created_at = created_at.replace('+0000', '')
    dt = parse(created_at)
    dt = dt + datetime.timedelta(hours=9)
    return dt


def add_content(text, content):
    text = content + "," + text
    return text


def set_file_name(keyword):
    date = datetime.datetime.today().strftime('%Y-%m-%d ')
    return date + keyword + '_t'


def get_tweets(query, num):
    datetime_list = []
    rt_list = []
    text_list = []
    file_name_list = []

    print('query:', query)
    for i in range(len(query)):  # 각 키워드별로 파일 생성하기로함.
        print('*** 현재 i:', query[i])

        statuses = twitter_api.GetSearch(term=query[i], count=num, lang='ko')  # 한국어로 작성된 트윗만

        for status in statuses:
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

        print('text_list:', text_list)
        # text 정리 완료, df로 만들기
        pd.set_option('display.max_columns', None)
        df = pd.DataFrame(list(zip(datetime_list, rt_list, text_list)), columns=['datetime', 'rt_count', 'text'])

        # df.columns = ['keyword', 'datetime', 'rt_count', 'text']
        df = df.drop_duplicates('text', ignore_index=True)  # 중복 제거
        print(df)
        file_name = set_file_name(query[i]) + '.xlsx'
        print('*** file_name:', file_name)

        # 파일 이름만 저장하기
        file_name_list.append(file_name)
        print('현재까지 file_name_list', file_name_list)

        df.to_excel(file_name, index=False)  # 엑셀파일로 만들기
        datetime_list.clear()
        rt_list.clear()
        text_list.clear()


    with open('file_name_list.txt', "w", encoding=encode_method) as f:
        f.write('\n'.join(file_name_list))


def tweet_crawler(num):
    keyword_trend = open('ent_trend.txt', 'r', encoding='utf-8').read().split(sep="\n")
    print(keyword_trend)
    get_tweets(keyword_trend, num)


# def crawling_tweet(query, date):
#     # 여러 키워드를 가져오고 싶을 때 query에 리스트 형태로 담아주면 된다.
#     # query = ["스엠"]
#     output_file_name = f"{query}_{date}_t"
#     http = 'http'
#     RESULT_PATH_TWEET = fr'{Path.RESULT_PATH_TWEET}\{date}'  # 결과 저장할 경로
#     Path.createFolder(RESULT_PATH_TWEET)
#
#     with open(fr'{RESULT_PATH_TWEET}\{output_file_name}', "w", encoding="utf-8") as output_file:
#         statuses = twitter_api.GetSearch(term=query, count=10, lang='ko')
#
#         for status in statuses:
#
#             rt_count = status.retweet_count
#             print('rt_count:', rt_count)
#             # RT 제거
#             if status.retweeted_status:
#                 # retweet_count가 0 이상이면(rt 가 존재하면) 해당 내용의 text를 text로 지정. RT @아이디를 빠진 데이터임.
#                 print('status.retweeted_status:', status.retweeted_status)
#                 text = status.retweeted_status.text
#                 print("*** RT 데이터입니다. RT된 횟수:", rt_count)
#                 print("*** RT 데이터입니다. 원 트윗:\n", text)
#                 rt = str(rt_count) + ":"
#                 text = rt + text
#             else:
#                 text = status.text
#                 print(text)
#
#             # 첨부된 이미지 제거
#             if text.find(http) != -1:
#                 print("*** 사진 존재. 링크를 제거할 예정입니다. 위치:", text.find(http))
#                 splited_link = text.split(http, maxsplit=2)
#                 text = splited_link[0]
#                 print('*** 링크 제거 후의 내용:', text)
#
#             # 멘션에 아이디 제거
#             mention_id = status.in_reply_to_screen_name
#
#             if mention_id is not None and text.find(mention_id) != -1:
#                 print("*** 아이디 태그 존재. 제거할 예정입니다. 위치:", text.find(mention_id))
#                 splited_link = text.split(mention_id + " ", maxsplit=2)
#                 text = splited_link[1]
#                 print('*** 아이디 제거 후의 내용:', text)
#
#             print('*** 최종 text:', text)
#             print('####################')
#             print(text, file=output_file, flush=True)
#             print('### 구분선 ###', file=output_file, flush=True)


if __name__ == "__main__":
    tweet_crawler(num=300)
