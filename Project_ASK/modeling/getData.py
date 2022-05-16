try:
    from db.db import read_table_stock, read_table_news, read_table_tweet
except:
    from db import read_table_stock, read_table_news, read_table_tweet

import pandas as pd


def get_data(company, s_date, e_date):
    df_stock = read_table_stock(company, s_date, e_date)
    df_news = read_table_news(company, s_date, e_date)
    df_tweet = read_table_tweet(company, s_date, e_date)

    # for index in df_stock.


