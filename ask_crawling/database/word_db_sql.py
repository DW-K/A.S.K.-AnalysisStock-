import traceback
from datetime import date

import pandas as pd

import sqlalchemy
from sqlalchemy import create_engine, insert, MetaData

try:
    from database.db_config import db_connection_address
    from database.models import get_table_obj_tweet, get_table_obj_news, \
    get_table_obj_news_count, create_tables, get_table_obj_company
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
                            "날짜": row['날짜'],
                            "시가": row['시가'],
                            "고가": row['고가'],
                            "저가": row['저가'],
                            "종가": row['종가'],
                            "전일비": row['전일비'],
                            "등락률": row['등락률'],
                            "거래량": row['거래량'],
                            "금액(백만)": row['금액(백만)'],
                            "신용비": row['신용비'],
                            "개인": row['개인'],
                            "기관": row['기관'],
                            "외인수량": row['외인수량'],
                            "외국계": row['외국계'],
                            "프로그램": row['프로그램'],
                            "외인비": row['외인비'],
                            "체결강도": row['체결강도'],
                            "외인보유": row['외인보유'],
                            "외인비중": row['외인비중'],
                            "외인순매수": row['외인순매수'],
                            "기관순매수": row['기관순매수'],
                            "개인순매수": row['개인순매수'],
                            "신용잔고율": row['신용잔고율']
                        }
                    ]
                )
            except Exception as e:
                print('error in insert_table_news')
                print(f'{traceback.format_exc()}')
                print(f'{e}')


def insert_table_news(df_news):  # dataframe 하나씩 넣기
    # df_news['날짜'] = pd.to_datetime(df_news['날짜']).dt.strftime("%Y-%m-%d")

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
                            "date": row['날짜'],
                            "company": row['company'],
                            "title": row['title'],
                            "query": row['query'],
                            "press": row['언론사'],
                            "press_link": row['언론사 링크'],
                            "article_link": row['기사 링크'],
                            "article_content": row['기사 내용'],
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


# def insert_table_news_sentiment(df_news_sentiment):  # dataframe 하나씩 넣기
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
# def insert_table_tweet_sentiment(df_tweet_sentiment):  # dataframe 하나씩 넣기
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


def insert_table_news_count(df_news_count):  # dataframe 하나씩 넣기
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
#         '유동자산': sqlalchemy.types.BIGINT(),
#         '현금및현금성자산': sqlalchemy.types.BIGINT(),
#         '단기금융상품': sqlalchemy.types.BIGINT(),
#         '기타유동금융자산': sqlalchemy.types.BIGINT(),
#         '매출채권': sqlalchemy.types.BIGINT(),
#         '미수금': sqlalchemy.types.BIGINT(),
#         '선급금': sqlalchemy.types.BIGINT(),
#         '재고자산': sqlalchemy.types.BIGINT(),
#         '당기법인세자산': sqlalchemy.types.BIGINT(),
#         '기타유동자산': sqlalchemy.types.BIGINT(),
#         '비유동자산': sqlalchemy.types.BIGINT(),
#         '장기금융상품': sqlalchemy.types.BIGINT(),
#         '기타비유동금융자산': sqlalchemy.types.BIGINT(),
#         '장기성매출채권': sqlalchemy.types.BIGINT(),
#         '종속기업, 공동기업 및 관계기업투자': sqlalchemy.types.BIGINT(),
#         '유형자산': sqlalchemy.types.BIGINT(),
#         '투자부동산': sqlalchemy.types.BIGINT(),
#         '무형자산': sqlalchemy.types.BIGINT(),
#         '이연법인세자산': sqlalchemy.types.BIGINT(),
#         '기타비유동자산': sqlalchemy.types.BIGINT(),
#         '자산총계': sqlalchemy.types.BIGINT(),
#         '유동부채': sqlalchemy.types.BIGINT(),
#         '매입채무': sqlalchemy.types.BIGINT(),
#         '단기차입금': sqlalchemy.types.BIGINT(),
#         '미지급금': sqlalchemy.types.BIGINT(),
#         '선수금': sqlalchemy.types.BIGINT(),
#         '미지급비용': sqlalchemy.types.BIGINT(),
#         '당기법인세부채': sqlalchemy.types.BIGINT(),
#         '유동성장기부채': sqlalchemy.types.BIGINT(),
#         '충당부채': sqlalchemy.types.BIGINT(),
#         '기타유동부채': sqlalchemy.types.BIGINT(),
#         '비유동부채': sqlalchemy.types.BIGINT(),
#         '사채': sqlalchemy.types.BIGINT(),
#         '장기차입금': sqlalchemy.types.BIGINT(),
#         '장기선수금': sqlalchemy.types.BIGINT(),
#         '순확정급여부채': sqlalchemy.types.BIGINT(),
#         '장기종업원급여충당부채': sqlalchemy.types.BIGINT(),
#         '장기충당부채': sqlalchemy.types.BIGINT(),
#         '이연법인세부채': sqlalchemy.types.BIGINT(),
#         '기타비유동부채': sqlalchemy.types.BIGINT(),
#         '부채총계': sqlalchemy.types.BIGINT(),
#         '지배기업 소유주지분': sqlalchemy.types.BIGINT(),
#         '보통주자본금': sqlalchemy.types.BIGINT(),
#         '주식발행초과금': sqlalchemy.types.BIGINT(),
#         '이익잉여금': sqlalchemy.types.BIGINT(),
#         '기타포괄손익누계액': sqlalchemy.types.BIGINT(),
#         '기타자본항목': sqlalchemy.types.BIGINT(),
#         '비지배지분': sqlalchemy.types.BIGINT(),
#         '자본총계': sqlalchemy.types.BIGINT(),
#         '부채와자본총계': sqlalchemy.types.BIGINT()
#     }
#
#     df_fin['date'] = pd.to_datetime(df_fin['date'], format='%Y%m%d').dt.strftime("%Y-%m-%d")
#     # df_fin['date'] = pd.to_datetime(df_fin['date']).dt.strftime("%Y-%m-%d") # news date format 바꾸기
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

    company = '기아'

    # insert_table_company(company)

    # df_stock = pd.read_excel(r'../dataset/stockData/car/기아/기아_s.xlsx')
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
    # indata = pd.read_sql_query("select * from 기아_finance", db_connection)
    # print(indata)
