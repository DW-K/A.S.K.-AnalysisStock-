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


def crawling_tweet(query, date):
    # 여러 키워드를 가져오고 싶을 때 query에 리스트 형태로 담아주면 된다.
    # query = ["스엠"]
    output_file_name = f"{query}_{date}_t"
    http = 'http'
    RESULT_PATH_TWEET = fr'{Path.RESULT_PATH_TWEET}\{date}'  # 결과 저장할 경로
    Path.createFolder(RESULT_PATH_TWEET)

    with open(fr'{RESULT_PATH_TWEET}\{output_file_name}', "w", encoding="utf-8") as output_file:
        statuses = twitter_api.GetSearch(term=query, count=10, lang='ko')

        for status in statuses:

            rt_count = status.retweet_count
            print('rt_count:', rt_count)
            # RT 제거
            if status.retweeted_status:
                # retweet_count가 0 이상이면(rt 가 존재하면) 해당 내용의 text를 text로 지정. RT @아이디를 빠진 데이터임.
                print('status.retweeted_status:', status.retweeted_status)
                text = status.retweeted_status.text
                print("*** RT 데이터입니다. RT된 횟수:", rt_count)
                print("*** RT 데이터입니다. 원 트윗:\n", text)
                rt = str(rt_count) + ":"
                text = rt + text
            else:
                text = status.text
                print(text)

            # 첨부된 이미지 제거
            if text.find(http) != -1:
                print("*** 사진 존재. 링크를 제거할 예정입니다. 위치:", text.find(http))
                splited_link = text.split(http, maxsplit=2)
                text = splited_link[0]
                print('*** 링크 제거 후의 내용:', text)

            # 멘션에 아이디 제거
            mention_id = status.in_reply_to_screen_name

            if mention_id is not None and text.find(mention_id) != -1:
                print("*** 아이디 태그 존재. 제거할 예정입니다. 위치:", text.find(mention_id))
                splited_link = text.split(mention_id + " ", maxsplit=2)
                text = splited_link[1]
                print('*** 아이디 제거 후의 내용:', text)

            print('*** 최종 text:', text)
            print('####################')
            print(text, file=output_file, flush=True)
            print('### 구분선 ###', file=output_file, flush=True)


if __name__ == "__main__":
    crawling_tweet("스엠", 2020812)
