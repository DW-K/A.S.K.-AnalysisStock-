import pandas as pd
from pororo import Pororo

sa = Pororo(task="sentiment", model="brainbert.base.ko.shopping", lang="ko")

a = ['불법', '의혹', '우려', '강세', '차별화되다', '달성하다', '호조']

df = pd.DataFrame(a)

for i in a:
    sentimentResult = sa(i)
    print(f'{i}: {sentimentResult}')

# # twitter API test
# import config
# import twitter
# twitter_api = twitter.Api(consumer_key=config.twitter_consumer_key,
#                           consumer_secret=config.twitter_consumer_secret,
#                           access_token_key=config.twitter_access_token,
#                           access_token_secret=config.twitter_access_secret)
#
# status = twitter_api.GetSearch(term='기아', count=1)
# print(status)
