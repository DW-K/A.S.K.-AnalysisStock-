twitter_consumer_key = "J9X4BPUHxcB6rWuD3CQSOe1dK"
twitter_consumer_secret = "x0kIXZfM6TTkZjBNmVTJkxtrvHIoRECt54Kdnj43gmMWYldoAP"
twitter_access_token = "1071250485078720512-FyaI7s6Zue92QwQN2dPplbmYfh1Q3T"
twitter_access_secret = "UzvZSM0XhW56azpKkRb4saMYorwODegcchZYMHCJzGbwi"
import twitter
twitter_api = twitter.Api(consumer_key=twitter_consumer_key,
                          consumer_secret=twitter_consumer_secret,
                          access_token_key=twitter_access_token,
                          access_token_secret=twitter_access_secret)

# # # 검색
# query = "현대차"
# statuses = twitter_api.GetSearch(term=query, count=10)
# result = []
# for status in statuses:
#     result.append(status.text)


# for status in statuses:
#     print(status.text)
#     print("#########################################")
#
# from collections import Counter
#
# query = "현대차"
# statuses = twitter_api.GetSearch(term=query, count=100)
# result = []
# for status in statuses:
#     for tag in status.hashtags:
#         result.append(tag.text)
#
# print(Counter(result).most_common(20))



# # 스트리밍
# import json
# query = ["카카오페이"]
# output_file_name = "stream_result.txt"
# with open(output_file_name, "w", encoding="utf-8") as output_file:
#     stream = twitter_api.GetStreamFilter(track=query)
#     while True:
#         for tweets in stream:
#             tweet = json.dumps(tweets, ensure_ascii=False)
#             print(tweet, file=output_file, flush=True)
#             print(tweet)


# # 스트리밍해서 텍스트만 저장
# import json
# query = ["카카오페이"]
# output_file_name = "stream_result.txt"
# with open(output_file_name, "w", encoding="utf-8") as output_file:
#     stream = twitter_api.GetStreamFilter(track=query)
#     while True:
#         for tweets in stream:
#             tweet = json.dumps(tweets["text"], ensure_ascii=False)
#             print(tweet, file=output_file, flush=True)
#             print(tweet, flush=True)



# Search 방식으로 텍스트만 저장
query = ["삼성전자"]
output_file_name = "search_result.txt"
statuses = twitter_api.GetSearch(term=query, count=100)
count = 0
for status in statuses:
    # print(status)
    if status.retweet_count < 5: continue
    print('** 작성시간:', status.created_at, '\n** 트윗 내용:\n', status.text, '\n** urls:\n', status.urls)
    print('########################################')
    count+=1
    # print(status)
    # print(status.text)





# print(status.text) # 딱 텍스트만 가져올 때


# # 여러 키워드를 가져오고 싶을 때 query에 담아주면 된다.
# query = ["카카오페이", "프리퀀시"]
# output_file_name = "search_result.txt"
# for i in range(len(query)):
#     with open(output_file_name, "w", encoding="utf-8") as output_file:
#         statuses = twitter_api.GetSearch(term=query[i], count=100)
#         for status in statuses:
#             print(status.text)
#             print('####################')
#             print(status.text, file=output_file, flush=True)
#     print("********** 현재 i는", query[i])

