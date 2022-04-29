import pandas as pd
import pymysql

import sqlalchemy
from sqlalchemy import create_engine, Column, Integer, String, DateTime, ForeignKey


# class TbCompany(Base):
#     __tablename__ = "Company_TB"
#
#     id = Column(Integer, primary_key=True)
#
#     name = Column(String(64))
#


def get_db_obj():
    ask_db = pymysql.connect(
        user='root',
        passwd='ask1234!',
        host='13.209.122.152',
        db='ASK',
        charset='utf8'
    )

    return ask_db


def get_word(word):
    ask_db = get_db_obj()

    try:
        cursor = ask_db.cursor(pymysql.cursors.DictCursor)

        sql = f'''SELECT {word} FROM {table_name}'''

        cursor.execute(sql)

        result = cursor.fetchall()

    finally:
        ask_db.close()

    return result


def create_db():
    conn = pymysql.connect(host='localhost',
                           user='root',
                           password=pw,
                           charset='utf8mb4')

    try:
        with conn.cursor() as cursor:
            sql = 'CREATE DATABASE ASK'
            cursor.execute(sql)
        conn.commit()
    finally:
        conn.close()


def insert_table_company(company):
    dtypesql = {
        'id': sqlalchemy.types.INTEGER(),
        'company': sqlalchemy.types.VARCHAR(24)
    }

    df_company = pd.DataFrame([company], columns=['company'])

    db_connection = create_engine(db_connection_address)
    df_company.to_sql(name='company_table', con=db_connection, if_exists='append', index=False, dtype=dtypesql)


def insert_table_stock(df_stock, company):
    dtypesql = {
        'id': sqlalchemy.types.BIGINT(),
        'company': sqlalchemy.types.VARCHAR(64),
        '날짜': sqlalchemy.types.DATE(),
        '시가': sqlalchemy.types.INTEGER(),
        '고가': sqlalchemy.types.INTEGER(),
        '저가': sqlalchemy.types.INTEGER(),
        '종가': sqlalchemy.types.INTEGER(),
        '전일비': sqlalchemy.types.INTEGER(),
        '등락률': sqlalchemy.types.FLOAT(),
        '거래량': sqlalchemy.types.INTEGER(),
        '금액(백만)': sqlalchemy.types.INTEGER(),
        '신용비': sqlalchemy.types.FLOAT(),
        '개인': sqlalchemy.types.VARCHAR(16),
        '기관': sqlalchemy.types.VARCHAR(16),
        '외인수량': sqlalchemy.types.VARCHAR(16),
        '외국계': sqlalchemy.types.VARCHAR(16),
        '프로그램': sqlalchemy.types.VARCHAR(16),
        '외인비': sqlalchemy.types.FLOAT(),
        '체결강도': sqlalchemy.types.FLOAT(),
        '외인보유': sqlalchemy.types.FLOAT(),
        '외인비중': sqlalchemy.types.FLOAT(),
        '외인순매수': sqlalchemy.types.VARCHAR(16),
        '기관순매수': sqlalchemy.types.VARCHAR(16),
        '개인순매수': sqlalchemy.types.VARCHAR(16),
        '신용잔고율': sqlalchemy.types.FLOAT(),
    }

    df_stock['날짜'] = pd.to_datetime(df_stock['날짜'], format='%Y%m%d').dt.strftime("%Y-%m-%d")
    add_company_name(df_stock, company)

    df_stock = df_stock[df_stock['날짜'] >= "2002-03-20"]

    db_connection = create_engine(db_connection_address)
    df_stock.to_sql(name='stock_table', con=db_connection, if_exists='append', index=False, dtype=dtypesql)


def insert_table_news(df_news, company):
    dtypesql = {
        'id': sqlalchemy.types.BIGINT(),
        'company': sqlalchemy.types.VARCHAR(24),
        '날짜': sqlalchemy.types.DATE(),
        '기사 제목': sqlalchemy.types.VARCHAR(256),
        '언론사': sqlalchemy.types.VARCHAR(256),
        '언론사 링크': sqlalchemy.types.VARCHAR(256),
        '기사 링크': sqlalchemy.types.VARCHAR(256),
        '기사 내용': sqlalchemy.types.VARCHAR(2048),
    }

    df_news['날짜'] = pd.to_datetime(df_news['날짜'], format='%Y%m%d').dt.strftime("%Y-%m-%d")
    # df_news['날짜'] = pd.to_datetime(df_news['날짜']).dt.strftime("%Y-%m-%d") # news date format 바꾸기
    add_company_name(df_news, company)

    db_connection = create_engine(db_connection_address)
    df_news.to_sql(name='news_table', con=db_connection, if_exists='append', index=False, dtype=dtypesql)


def insert_table_tweet(df_tweet, company):
    dtypesql = {
        'id': sqlalchemy.types.BIGINT(),
        'company': sqlalchemy.types.VARCHAR(24),
        'date': sqlalchemy.types.DATE(),
        'rt_count': sqlalchemy.types.INTEGER(),
        'text': sqlalchemy.types.VARCHAR(1024)
    }

    df_tweet['date'] = pd.to_datetime(df_tweet['date']).dt.strftime("%Y-%m-%d")
    add_company_name(df_tweet, company)

    db_connection = create_engine(db_connection_address)
    df_tweet.to_sql(name='tweet_table', con=db_connection, if_exists='append', index=False, dtype=dtypesql)


def insert_table_finance(df_fin, company):
    dtypesql = {
        'id': sqlalchemy.types.BIGINT(),
        'company': sqlalchemy.types.VARCHAR(24),
        'date': sqlalchemy.types.DATE(),
        '유동자산': sqlalchemy.types.BIGINT(),
        '현금및현금성자산': sqlalchemy.types.BIGINT(),
        '단기금융상품': sqlalchemy.types.BIGINT(),
        '기타유동금융자산': sqlalchemy.types.BIGINT(),
        '매출채권': sqlalchemy.types.BIGINT(),
        '미수금': sqlalchemy.types.BIGINT(),
        '선급금': sqlalchemy.types.BIGINT(),
        '재고자산': sqlalchemy.types.BIGINT(),
        '당기법인세자산': sqlalchemy.types.BIGINT(),
        '기타유동자산': sqlalchemy.types.BIGINT(),
        '비유동자산': sqlalchemy.types.BIGINT(),
        '장기금융상품': sqlalchemy.types.BIGINT(),
        '기타비유동금융자산': sqlalchemy.types.BIGINT(),
        '장기성매출채권': sqlalchemy.types.BIGINT(),
        '종속기업, 공동기업 및 관계기업투자': sqlalchemy.types.BIGINT(),
        '유형자산': sqlalchemy.types.BIGINT(),
        '투자부동산': sqlalchemy.types.BIGINT(),
        '무형자산': sqlalchemy.types.BIGINT(),
        '이연법인세자산': sqlalchemy.types.BIGINT(),
        '기타비유동자산': sqlalchemy.types.BIGINT(),
        '자산총계': sqlalchemy.types.BIGINT(),
        '유동부채': sqlalchemy.types.BIGINT(),
        '매입채무': sqlalchemy.types.BIGINT(),
        '단기차입금': sqlalchemy.types.BIGINT(),
        '미지급금': sqlalchemy.types.BIGINT(),
        '선수금': sqlalchemy.types.BIGINT(),
        '미지급비용': sqlalchemy.types.BIGINT(),
        '당기법인세부채': sqlalchemy.types.BIGINT(),
        '유동성장기부채': sqlalchemy.types.BIGINT(),
        '충당부채': sqlalchemy.types.BIGINT(),
        '기타유동부채': sqlalchemy.types.BIGINT(),
        '비유동부채': sqlalchemy.types.BIGINT(),
        '사채': sqlalchemy.types.BIGINT(),
        '장기차입금': sqlalchemy.types.BIGINT(),
        '장기선수금': sqlalchemy.types.BIGINT(),
        '순확정급여부채': sqlalchemy.types.BIGINT(),
        '장기종업원급여충당부채': sqlalchemy.types.BIGINT(),
        '장기충당부채': sqlalchemy.types.BIGINT(),
        '이연법인세부채': sqlalchemy.types.BIGINT(),
        '기타비유동부채': sqlalchemy.types.BIGINT(),
        '부채총계': sqlalchemy.types.BIGINT(),
        '지배기업 소유주지분': sqlalchemy.types.BIGINT(),
        '보통주자본금': sqlalchemy.types.BIGINT(),
        '주식발행초과금': sqlalchemy.types.BIGINT(),
        '이익잉여금': sqlalchemy.types.BIGINT(),
        '기타포괄손익누계액': sqlalchemy.types.BIGINT(),
        '기타자본항목': sqlalchemy.types.BIGINT(),
        '비지배지분': sqlalchemy.types.BIGINT(),
        '자본총계': sqlalchemy.types.BIGINT(),
        '부채와자본총계': sqlalchemy.types.BIGINT()
    }

    df_fin['date'] = pd.to_datetime(df_fin['date'], format='%Y%m%d').dt.strftime("%Y-%m-%d")
    # df_fin['date'] = pd.to_datetime(df_fin['date']).dt.strftime("%Y-%m-%d") # news date format 바꾸기
    add_company_name(df_fin, company)

    db_connection = create_engine(db_connection_address)
    df_fin.to_sql(name='finance_table', con=db_connection, if_exists='append', index=False, dtype=dtypesql)


def read_table_stock():
    SQL = "SELECT * FROM stock_table"
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)
    return df


def read_table_finance():
    SQL = 'SELECT * FROM finance_table WHERE company="기아"'
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)
    return df


def read_table_tweet():
    SQL = "SELECT * FROM tweet_table"
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)


def read_table_news():
    SQL = "SELECT * FROM news_table"
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)


def add_company_name(df, company):
    df['company'] = company


if __name__ == "__main__":
    # indata = pd.read_sql_query("select * from kopo_product_volume", engine)

    company = '기아'

    # insert_table_company(company)

    # df_stock = pd.read_excel(r'../dataset/stockData/car/기아/기아_s.xlsx')
    # insert_table_stock(df_stock, company)
    #
    # df_news = pd.read_csv(r'../dataset/crawling.csv')
    # insert_table_news(df_news, company)
    #
    # df_tweet = pd.read_excel(r'../d/tweet.xlsx')
    # insert_table_tweet(df_tweet, company)

    df_fin = pd.read_excel(r'../d/fs2.xlsx')
    insert_table_finance(df_fin, company)

    # df_stock = read_table_stock()
    # print(df_stock)

    # df_fin = read_table_finance()
    # print(df_fin)

    # db_connection_str = f'mysql+pymysql://root:{pw}@localhost:3306/ASK'
    # db_connection = create_engine(db_connection_str)
    # indata = pd.read_sql_query("select * from 기아_finance", db_connection)
    # print(indata)

