import numpy as np

from pororo import Pororo
import pandas as pd

from database.word_db_sql import insert_table_news
from database.models import create_tables


def sentiment(df):
    target_col = '기사 내용'

    sa = Pororo(task="sentiment", model="brainbert.base.ko.shopping", lang="ko")
    abs_summ = Pororo(task="text_summarization", lang="ko", model="abstractive")

    for i in df.index:
        # print(df.loc[i, target_col])
        if df.loc[i, target_col] is not np.NAN:
            # print(df.loc[i, target_col])
            abs = abs_summ(df.loc[i, target_col])
            if len(df.loc[i, target_col]) > 500:
                text = abs
            else:
                text = df.loc[i, target_col]

            sentimentResult = sa(text, show_probs=True)

            df.loc[i, "content_abs"] = abs
            df.loc[i, "positive"] = sentimentResult['positive']
            df.loc[i, "negative"] = sentimentResult['negative']

    insert_table_news(df)
