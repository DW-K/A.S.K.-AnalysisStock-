from datetime import date

import pandas as pd

import sqlalchemy
from sqlalchemy import create_engine, insert, MetaData
from sqlalchemy import Table, Column, ForeignKey, Integer, BigInteger, FLOAT, VARCHAR, DATE
import traceback

pw = 'ask1234!'

# db_connection_address = f'mysql+pymysql://root:{pw}@localhost:3306/ASK'
db_connection_address = f'mysql+pymysql://root:{pw}@13.209.122.152:3306/ASK'

meta = MetaData()
engine = create_engine(db_connection_address)


def read_table_stock(company, s_date, e_date):
    SQL = f'SELECT * FROM crawl_stock_table WHERE company="{company}" AND "{s_date}" <= 날짜 AND 날짜 <= "{e_date}"'
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


def create_table_result(db_meta=meta):
    get_table_obj_result(db_meta)
    meta.create_all(engine)


def get_table_obj_result(db_meta=meta):
    result_table = Table(
        'result_table', db_meta,
        Column('id', BigInteger, primary_key=True, autoincrement=True),
        Column('date', DATE, nullable=False),
        Column('company', VARCHAR(64), nullable=False),
        Column('result', FLOAT),
    )

    return result_table


def insert_table_result(df_result):  # dataframe 하나씩 넣기
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
                            "date": row['date'],
                            "company": row['company'],
                            "result": row['result']
                        }
                    ]
                )
            except Exception as e:
                print('error in insert_table_news')
                print(f'{traceback.format_exc()}')
                print(f'{e}')


if __name__ == "__main__":
<<<<<<< HEAD
    # df = read_table_stock(company='현대차', s_date=date(2021, 1, 1), e_date=date(2022, 4, 30))
    # print(df)

    df = pd.DataFrame([[date(2021, 5, 5), "현대차", 0.5], [date(2021, 6, 6), "현대차", -0.1]], columns=["date", "company", "result"])

    create_table_result()

    insert_table_result(df_result=df)
=======
    df = read_table_news(company='현대차', s_date=date(2022, 4, 1), e_date=date(2022, 4, 29))
    print(df)
>>>>>>> c85876a (no message)
