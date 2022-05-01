from datetime import date

import pandas as pd

import sqlalchemy
from sqlalchemy import create_engine, insert, MetaData

pw = 'ask1234!'

db_connection_address = f'mysql+pymysql://root:{pw}@localhost:3306/ASK'
# db_connection_address = f'mysql+pymysql://root:{pw}@13.209.122.152:3306/ASK'


def read_table_stock(company, s_date, e_date):
    SQL = f'SELECT * FROM crawl_stock_table WHERE company="{company}" AND "{s_date}" <= date AND date <= "{e_date}"'
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)
    return df


def read_table_tweet(company, s_date, e_date):
    SQL = f'SELECT * FROM crawl_tweet_table WHERE company="{company}" AND "{s_date}" <= date AND date <= "{e_date}"'
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)
    return df


def read_table_news(company, s_date, e_date):
    SQL = f'SELECT * FROM crawl_news_table WHERE company="{company}" AND "{s_date}" <= date AND date <= "{e_date}"'
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)
    return df


if __name__ == "__main__":
    df = read_table_news(company='기아', s_date=date(2022, 4, 1), e_date=date(2022, 4, 29))
    print(df)