import traceback
from datetime import date

import pandas as pd

import sqlalchemy
from sqlalchemy import create_engine, insert, MetaData

try:
    from database.db_config import db_connection_address
    from database.models import get_table_obj_tweet, get_table_obj_news, \
    get_table_obj_news_count, create_tables, get_table_obj_company, get_table_obj_result
except:
    from db_config import db_connection_address
    from models import get_table_obj_tweet, get_table_obj_news, \
        get_table_obj_news_count, create_tables

start_date = date(2002, 6, 1)


def insert_table_company(company):
    db_connection = create_engine(db_connection_address)
    with db_connection.connect() as conn:
        meta = MetaData(bind=conn)
        company_table = get_table_obj_company(meta)

        try:
            result = conn.execute(
                insert(company_table),
                [
                    {
                        "company": company,
                    }
                ]
            )
        except Exception as e:
            print(f'{e}')


# def insert_table_log(company):
#     db_connection = create_engine(db_connection_address)
#
#     with db_connection.connect() as conn:
#         meta = MetaData(bind=conn)
#         log_table = get_table_obj_log(meta)
#
#         try:
#             result = conn.execute(
#                 insert(log_table),
#                 [
#                     {
#                         "company": company,
#                     }
#                 ]
#             )
#         except Exception as e:
#             print(f'{e}')


def insert_table_stock(df_stock):
    db_connection = create_engine(db_connection_address)
    with db_connection.connect() as conn:
        meta = MetaData(bind=conn)
        news_table = get_table_obj_news(meta)
        for idx, row in df_stock.iterrows():
            try:
                result = conn.execute(
                    insert(news_table),
                    [
                        {
                            "company": row['company'],
                            "??????": row['??????'],
                            "??????": row['??????'],
                            "??????": row['??????'],
                            "??????": row['??????'],
                            "??????": row['??????'],
                            "?????????": row['?????????'],
                            "?????????": row['?????????'],
                            "?????????": row['?????????'],
                            "??????(??????)": row['??????(??????)'],
                            "?????????": row['?????????'],
                            "??????": row['??????'],
                            "??????": row['??????'],
                            "????????????": row['????????????'],
                            "?????????": row['?????????'],
                            "????????????": row['????????????'],
                            "?????????": row['?????????'],
                            "????????????": row['????????????'],
                            "????????????": row['????????????'],
                            "????????????": row['????????????'],
                            "???????????????": row['???????????????'],
                            "???????????????": row['???????????????'],
                            "???????????????": row['???????????????'],
                            "???????????????": row['???????????????']
                        }
                    ]
                )
            except Exception as e:
                print('error in insert_table_news')
                print(f'{traceback.format_exc()}')
                print(f'{e}')


def insert_table_news(df_news):  # dataframe ????????? ??????
    # df_news['??????'] = pd.to_datetime(df_news['??????']).dt.strftime("%Y-%m-%d")

    db_connection = create_engine(db_connection_address)

    with db_connection.connect() as conn:
        meta = MetaData(bind=conn)
        news_table = get_table_obj_news(meta)
        for idx, row in df_news.iterrows():
            try:
                result = conn.execute(
                    insert(news_table),
                    [
                        {
                            "date": row['??????'],
                            "company": row['company'],
                            "title": row['title'],
                            "query": row['query'],
                            "press": row['?????????'],
                            "press_link": row['????????? ??????'],
                            "article_link": row['?????? ??????'],
                            "article_content": row['?????? ??????'],
                            "content_abs": row['content_abs'],
                            "positive": row['positive'],
                            "negative": row['negative']
                        }
                    ]
                )
            except Exception as e:
                print('error in insert_table_news')
                print(f'{traceback.format_exc()}')
                print(f'{e}')


def insert_table_result(df_result):  # dataframe ????????? ??????
    db_connection = create_engine(db_connection_address)

    with db_connection.connect() as conn:
        meta = MetaData(bind=conn)
        result_table = get_table_obj_result(meta)
        for idx, row in df_result.iterrows():
            try:
                result = conn.execute(
                    insert(result_table),
                    [
                        {
                            "date": row['??????'],
                            "company": row['company'],
                            "result": row['result']
                        }
                    ]
                )
            except Exception as e:
                print('error in insert_table_news')
                print(f'{traceback.format_exc()}')
                print(f'{e}')


def insert_table_tweet(df_tweet):
    df_tweet['date'] = pd.to_datetime(df_tweet['date']).dt.strftime("%Y-%m-%d")

    db_connection = create_engine(db_connection_address)

    with db_connection.connect() as conn:
        meta = MetaData(bind=conn)
        tweet_table = get_table_obj_tweet(meta)
        for idx, row in df_tweet.iterrows():
            try:
                result = conn.execute(
                    insert(tweet_table),
                    [
                        {
                            "date": row['date'],
                            "company": row['company'],
                            "rt_count": row['rt_count'],
                            "text": row['text'],
                            "positive": row['positive'],
                            "negative": row['negative']
                        }
                    ]
                )
            except Exception as e:
                print(e)


# def insert_table_news_sentiment(df_news_sentiment):  # dataframe ????????? ??????
#     db_connection = create_engine(db_connection_address)
#
#     with db_connection.connect() as conn:
#         meta = MetaData(bind=conn)
#         news_sentiment_table = get_table_obj_news_sentiment(meta)
#         for idx, row in df_news_sentiment.iterrows():
#             try:
#                 result = conn.execute(
#                     insert(news_sentiment_table),
#                     [
#                         {
#                             "id": row['id'],
#                             "positive": row['positive'],
#                             "negative": row['negative']
#                         }
#                     ]
#                 )
#             except Exception as e:
#                 print(f'{e}')
#
#
# def insert_table_tweet_sentiment(df_tweet_sentiment):  # dataframe ????????? ??????
#     db_connection = create_engine(db_connection_address)
#
#     with db_connection.connect() as conn:
#         meta = MetaData(bind=conn)
#         tweet_sentiment_table = get_table_obj_tweet_sentiment(meta)
#         for idx, row in df_tweet_sentiment.iterrows():
#             try:
#                 result = conn.execute(
#                     insert(tweet_sentiment_table),
#                     [
#                         {
#                             "id": row['id'],
#                             "positive": row['positive'],
#                             "negative": row['negative']
#                         }
#                     ]
#                 )
#             except Exception as e:
#                 print(f'{e}')


def insert_table_news_count(df_news_count):  # dataframe ????????? ??????
    db_connection = create_engine(db_connection_address)

    with db_connection.connect() as conn:
        meta = MetaData(bind=conn)
        news_count_table = get_table_obj_news_count(meta)
        for idx, row in df_news_count.iterrows():
            try:
                result = conn.execute(
                    insert(news_count_table),
                    [
                        {
                            "date": row['date'],
                            "word": row['word'],
                            "count": row['count'],
                            "company": row['company'],
                            "positive": row['positive'],
                            "negative": row['negative']
                        }
                    ]
                )
            except Exception as e:
                print(f'{e}')


# def insert_table_finance(df_fin, company):
#     dtypesql = {
#         'company': sqlalchemy.types.VARCHAR(64),
#         'date': sqlalchemy.types.DATE(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '????????????????????????': sqlalchemy.types.BIGINT(),
#         '??????????????????': sqlalchemy.types.BIGINT(),
#         '????????????????????????': sqlalchemy.types.BIGINT(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '?????????': sqlalchemy.types.BIGINT(),
#         '?????????': sqlalchemy.types.BIGINT(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????': sqlalchemy.types.BIGINT(),
#         '??????????????????': sqlalchemy.types.BIGINT(),
#         '???????????????': sqlalchemy.types.BIGINT(),
#         '??????????????????': sqlalchemy.types.BIGINT(),
#         '???????????????????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????': sqlalchemy.types.BIGINT(),
#         '????????????, ???????????? ??? ??????????????????': sqlalchemy.types.BIGINT(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '???????????????': sqlalchemy.types.BIGINT(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????': sqlalchemy.types.BIGINT(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '???????????????': sqlalchemy.types.BIGINT(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '?????????': sqlalchemy.types.BIGINT(),
#         '???????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????': sqlalchemy.types.BIGINT(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '??????????????????': sqlalchemy.types.BIGINT(),
#         '???????????????': sqlalchemy.types.BIGINT(),
#         '??????': sqlalchemy.types.BIGINT(),
#         '???????????????': sqlalchemy.types.BIGINT(),
#         '???????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????????????????': sqlalchemy.types.BIGINT(),
#         '??????????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????': sqlalchemy.types.BIGINT(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '???????????? ???????????????': sqlalchemy.types.BIGINT(),
#         '??????????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????': sqlalchemy.types.BIGINT(),
#         '???????????????': sqlalchemy.types.BIGINT(),
#         '???????????????????????????': sqlalchemy.types.BIGINT(),
#         '??????????????????': sqlalchemy.types.BIGINT(),
#         '???????????????': sqlalchemy.types.BIGINT(),
#         '????????????': sqlalchemy.types.BIGINT(),
#         '?????????????????????': sqlalchemy.types.BIGINT()
#     }
#
#     df_fin['date'] = pd.to_datetime(df_fin['date'], format='%Y%m%d').dt.strftime("%Y-%m-%d")
#     # df_fin['date'] = pd.to_datetime(df_fin['date']).dt.strftime("%Y-%m-%d") # news date format ?????????
#     add_company_name(df_fin, company)
#
#     db_connection = create_engine(db_connection_address)
#     df_fin.to_sql(name='crawl_finance_table', con=db_connection, if_exists='append', index=False, dtype=dtypesql)


def read_table_news_for_count(company, day, word):
    SQL = f'SELECT * FROM crawl_news_table WHERE company="{company}" AND date="{day}" AND article_content LIKE "%%{word}%%"'
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)
    return df


# def read_table_news_for_sentiment(word):
#     SQL = f"SELECT * FROM crawl_news_table WHERE article_content LIKE '%%{word}%%'"
#     db_connection = create_engine(db_connection_address)
#     df = pd.read_sql(SQL, db_connection)
#     return df


def read_table_stock(company):
    SQL = f'SELECT * FROM crawl_stock_table WHERE company="{company}"'
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)
    return df


# def read_table_finance(company):
#     SQL = f'SELECT * FROM crawl_finance_table WHERE company="{company}"'
#     db_connection = create_engine(db_connection_address)
#     df = pd.read_sql(SQL, db_connection)
#     return df


def read_table_tweet(company):
    SQL = f'SELECT * FROM crawl_tweet_table WHERE company="{company}"'
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)
    return df


def read_table_news(company, day):
    SQL = f'SELECT * FROM crawl_news_table WHERE company="{company}" AND date="{day}"'
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)
    return df


# def read_table_news_sentiment():
#     SQL = "SELECT * FROM crawl_news_sentiment_table"
#     db_connection = create_engine(db_connection_address)
#     df = pd.read_sql(SQL, db_connection)
#     return df
#
#
# def read_table_tweet_sentiment():
#     SQL = "SELECT * FROM crawl_tweet_sentiment_table"
#     db_connection = create_engine(db_connection_address)
#     df = pd.read_sql(SQL, db_connection)
#     return df


def add_company_name(df, company):
    df['company'] = company


if __name__ == "__main__":
    # indata = pd.read_sql_query("select * from kopo_product_volume", engine)

    company = '??????'

    # insert_table_company(company)

    # df_stock = pd.read_excel(r'../dataset/stockData/car/??????/??????_s.xlsx')
    # insert_table_stock(df_stock, company)
    # create_tables()
    #
    # df_news = pd.read_csv(r'../dataset/crawling.csv')
    # print(df_news.columns)
    # insert_table_news(df_news, company)

    # day = date(2021, 9, 11)
    # df = read_table_news_for_word_count(day)
    # print(df)

    # df_tweet = pd.read_excel(r'../d/tweet.xlsx')
    # insert_table_tweet(df_tweet, company)
    #
    # df_fin = pd.read_excel(r'../d/fs2.xlsx')
    # insert_table_finance(df_fin, company)

    # df_stock = read_table_stock()
    # print(df_stock)

    # df_fin = read_table_finance()
    # print(df_fin)

    # db_connection_str = f'mysql+pymysql://root:{pw}@localhost:3306/ASK'
    # db_connection = create_engine(db_connection_str)
    # indata = pd.read_sql_query("select * from ??????_finance", db_connection)
    # print(indata)
