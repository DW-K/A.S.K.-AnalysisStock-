import numpy as np

try:
    from db.db import read_table_stock, read_table_news, read_table_tweet
except:
    from db import read_table_stock, read_table_news, read_table_tweet

import pandas as pd
from datetime import date

from datetime import date


def get_data(company, s_date, e_date):
    df_stock = read_table_stock(company, s_date, e_date)
    df_news = read_table_news(company, s_date, e_date)
    df_tweet = read_table_tweet(company, s_date, e_date)

    df_stock = df_stock.set_index("날짜")
    df_news = df_news.set_index("date")
    df_tweet = df_tweet.set_index("date")

    sent_cols = ["positive", "negative"]

    stock_cols = ["종가", "거래량"]

    target_col = "등락률"

    df_sentiment = df_news[sent_cols].add(df_tweet[sent_cols], fill_value=0)
    df_sentiment_avg = pd.DataFrame()

    for i in df_stock.index:
        if i in df_sentiment.index:
            for col in sent_cols:
                if type(df_sentiment.loc[i, col]) is not float:
                    df_sentiment_avg.loc[i, col] = df_sentiment.loc[i, col].sum()/df_sentiment.loc[i, col].shape[0]
                else:
                    df_sentiment_avg.loc[i, col] = df_sentiment.loc[i, col]
        else:
            for col in sent_cols:
                df_sentiment_avg.loc[i, col] = 0

    result = {"sentiment": df_sentiment_avg, "stock": df_stock[stock_cols], "target": df_stock[target_col], "date": list(df_stock.index)}

    return result


if __name__ == "__main__":
    result = get_data("현대차", date(2022, 1, 1), date(2022, 4, 30))