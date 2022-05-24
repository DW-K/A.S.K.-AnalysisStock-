import numpy as np

from pororo import Pororo
import pandas as pd

from database.word_db_sql import  insert_table_tweet
from database.models import create_tables


def sentiment_tweet(df):
    target_col = 'text'

    if len(df) <= 0:
        return

    sa = Pororo(task="sentiment", model="brainbert.base.ko.shopping", lang="ko")

    tweet_df = df.copy()

    for i in tweet_df.index:
        # print(df.loc[i, target_col])

        text = tweet_df.loc[i, target_col]

        sentimentResult = sa(text, show_probs=True)

        tweet_df.loc[i, "positive"] = sentimentResult['positive']
        tweet_df.loc[i, "negative"] = sentimentResult['negative']

    print(tweet_df)
    insert_table_tweet(tweet_df)
