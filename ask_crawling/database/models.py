import pymysql
from sqlalchemy import Table, Column, ForeignKey, Integer, BigInteger, FLOAT, VARCHAR, DATE, BOOLEAN,MetaData, create_engine
from sqlalchemy import insert, update
from datetime import date, timedelta

import exchange_calendars as ecals

try:
    from database.db_config import pw, db_connection_address
except:
    from db_config import pw, db_connection_address


meta = MetaData()
engine = create_engine(db_connection_address)`

start_date = date(2002, 6, 1)


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


def create_table_date(db_meta=meta):
    obj = get_table_obj_date(db_meta)
    meta.create_all(engine)
    return obj


def create_table_company(db_meta=meta):
    get_table_obj_company(db_meta)
    meta.create_all(engine)


# def create_table_crawl_log(db_meta=meta):
#     get_table_obj_log(db_meta)
#     meta.create_all(engine)


def create_table_stock(db_meta=meta):
    get_table_obj_stock(db_meta)
    meta.create_all(engine)


def create_table_news(db_meta=meta):
    get_table_obj_news(db_meta)
    meta.create_all(engine)


def create_table_tweet(db_meta=meta):
    get_table_obj_tweet(db_meta)
    meta.create_all(engine)


# def create_table_news_sentiment(db_meta=meta):
#     get_table_obj_news_sentiment(db_meta)
#     meta.create_all(engine)


# def create_table_tweet_sentiment(db_meta=meta):
#     get_table_obj_tweet_sentiment(db_meta)
#     meta.create_all(engine)


def create_table_obj_news_count(db_meta=meta):
    get_table_obj_news_count(db_meta)
    meta.create_all(engine)


def create_table_obj_result(db_meta=meta):
    get_table_obj_result(db_meta)
    meta.create_all(engine)



# def create_table_finance(db_meta=meta):
#     get_table_obj_finance(db_meta)
#     meta.create_all(engine)


def init_table_date(date_table):
    with engine.connect() as conn:
        result = conn.execute(
            insert(date_table),
            [
                {
                    "date": start_date + timedelta(days=i)
                 }
                for i in range(7500)    # 2023-01-06일까지 초기화
            ]
        )


def update_trading_date(date_table):    # 개장 날짜 업데이트 코드 스킵..
    XKRX = ecals.get_calendar("XKRX")  # 한국 코드

    with engine.connect() as conn:
        for i in range(7500):
            cur_date = start_date + timedelta(days=i)

            # print(f'{cur_date}: {XKRX.is_session(cur_date)}')

            if XKRX.is_session(cur_date):   # 개장일인지 확인
                stmt = (
                    update(date_table).where(date_table.c.date == cur_date).
                        values(trading=1)
                )

                result = conn.execute(stmt)


def get_table_obj_date(db_meta=meta):
    date_table = Table(
        'crawl_date_table', db_meta,
        Column('date', DATE, primary_key=True),
        Column('trading', BOOLEAN, unique=False, nullable=False, default=0),
    )

    return date_table


def get_table_obj_company(db_meta=meta):
    company_table = Table(
        'crawl_company_table', db_meta,
        Column('company_id', BigInteger, primary_key=True, autoincrement=True),
        Column('company', VARCHAR(64), unique=True, nullable=False),
    )

    return company_table


# def get_table_obj_log(db_meta=meta):
#     log_table = Table(
#         'crawl_log_table', db_meta,
#         Column('company', VARCHAR(64), ForeignKey("crawl_company_table.company"), primary_key=True),
#         Column('progress', DATE)
#     )
#
#     return log_table


def get_table_obj_tweet(db_meta=meta):
    tweet_table = Table(
        'crawl_tweet_table', db_meta,
        Column('tweet_id', BigInteger, primary_key=True, autoincrement=True),
        Column('date', DATE, ForeignKey("crawl_date_table.date"), nullable=False),
        Column('company', VARCHAR(64), ForeignKey("crawl_company_table.company"), nullable=False),
        Column('rt_count', Integer),
        Column('text', VARCHAR(256), unique=True),  # 512
        Column('positive', FLOAT, nullable=False),
        Column('negative', FLOAT, nullable=False)
    )

    return tweet_table


def get_table_obj_news(db_meta=meta):
    news_table = Table(
        'crawl_news_table', db_meta,
        Column('news_id', BigInteger, primary_key=True, autoincrement=True),
        Column('date', DATE, ForeignKey("crawl_date_table.date"), nullable=False),
        Column('company', VARCHAR(64), ForeignKey("crawl_company_table.company"), nullable=False),
        Column('query', VARCHAR(64), nullable=False),
        Column('title', VARCHAR(256), nullable=False, unique=True),
        Column('press', VARCHAR(64)),
        Column('press_link', VARCHAR(256)),
        Column('article_link', VARCHAR(256)),
        Column('article_content', VARCHAR(8192)),
        Column('content_abs', VARCHAR(2048)),
        Column('positive', FLOAT, nullable=False),
        Column('negative', FLOAT, nullable=False)
    )

    return news_table


def get_table_obj_result(db_meta=meta):
    result_table = Table(
        'result_table', db_meta,
        Column('id', BigInteger, primary_key=True, autoincrement=True),
        Column('date', DATE, ForeignKey("crawl_date_table.date"), nullable=False),
        Column('company', VARCHAR(64), ForeignKey("crawl_company_table.company"), nullable=False),
        Column('result', FLOAT),
    )

    return result_table

# def get_table_obj_tweet_sentiment(db_meta=meta):
#     tweet_sentiment_table = Table(
#         'crawl_tweet_table_sentiment', db_meta,
#         Column('id', BigInteger, ForeignKey("crawl_tweet_table.date"), primary_key=True),
#         Column('positive', FLOAT, nullable=False),
#         Column('negative', FLOAT, nullable=False)
#     )
#
#     return tweet_sentiment_table


# def get_table_obj_news_sentiment(db_meta=meta):
#     news_sentiment_table = Table(
#         'crawl_news_table_sentiment', db_meta,
#         Column('id', BigInteger, ForeignKey("crawl_news_table.date"), primary_key=True),
#         Column('positive', FLOAT, nullable=False),
#         Column('negative', FLOAT, nullable=False)
#     )
#
#     return news_sentiment_table


def get_table_obj_news_count(db_meta=meta):
    news_count_table = Table(
        'crawl_news_count_table', db_meta,
        Column('news_count_id', BigInteger, primary_key=True, autoincrement=True),
        Column('date', DATE, ForeignKey("crawl_date_table.date"), nullable=False),
        Column('word', VARCHAR(32), nullable=False),
        Column('count', Integer, nullable=False),
        Column('company', VARCHAR(64), ForeignKey("crawl_company_table.company"), nullable=False),
        Column('positive', FLOAT, nullable=False),
        Column('negative', FLOAT, nullable=False)
    )

    return news_count_table


def get_table_obj_stock(db_meta=meta):
    stock_table = Table(
        'crawl_stock_table', db_meta,
        Column('날짜', DATE, ForeignKey("crawl_date_table.date"), primary_key=True, nullable=False),
        Column('company', VARCHAR(64), ForeignKey("crawl_company_table.company"), primary_key=True, nullable=False),
        Column('시가', Integer),
        Column('고가', Integer),
        Column('저가', Integer),
        Column('종가', Integer),
        Column('전일비', Integer),
        Column('등락률', FLOAT),
        Column('거래량', Integer),
        Column('금액(백만)', Integer),
        Column('신용비', FLOAT),
        Column('개인', VARCHAR(16)),
        Column('기관', VARCHAR(16)),
        Column('외인수량', VARCHAR(16)),
        Column('외국계', VARCHAR(16)),
        Column('프로그램', VARCHAR(16)),
        Column('외인비', FLOAT),
        Column('체결강도', FLOAT),
        Column('외인보유', FLOAT),
        Column('외인비중', FLOAT),
        Column('외인순매수', VARCHAR(16)),
        Column('기관순매수', VARCHAR(16)),
        Column('개인순매수', VARCHAR(16)),
        Column('신용잔고율', FLOAT),
    )

    return stock_table


# def get_table_obj_finance(db_meta=meta):
#     finance_table = Table(
#         'crawl_finance_table', db_meta,
#         Column('date', DATE, ForeignKey("crawl_date_table.date"), primary_key=True, nullable=False),
#         Column('company', VARCHAR(64), ForeignKey("crawl_company_table.company"), primary_key=True, nullable=False),
#         Column('유동자산', BigInteger),
#         Column('현금및현금성자산', BigInteger),
#         Column('단기금융상품', BigInteger),
#         Column('기타유동금융자산', BigInteger),
#         Column('매출채권', BigInteger),
#         Column('미수금', BigInteger),
#         Column('선급금', BigInteger),
#         Column('재고자산', BigInteger),
#         Column('당기법인세자산', BigInteger),
#         Column('기타유동자산', BigInteger),
#         Column('비유동자산', BigInteger),
#         Column('장기금융상품', BigInteger),
#         Column('기타비유동금융자산', BigInteger),
#         Column('장기성매출채권', BigInteger),
#         Column('종속기업, 공동기업 및 관계기업투자', BigInteger),
#         Column('유형자산', BigInteger),
#         Column('투자부동산', BigInteger),
#         Column('무형자산', BigInteger),
#         Column('이연법인세자산', BigInteger),
#         Column('기타비유동자산', BigInteger),
#         Column('자산총계', BigInteger),
#         Column('유동부채', BigInteger),
#         Column('매입채무', BigInteger),
#         Column('단기차입금', BigInteger),
#         Column('미지급금', BigInteger),
#         Column('선수금', BigInteger),
#         Column('미지급비용', BigInteger),
#         Column('당기법인세부채', BigInteger),
#         Column('유동성장기부채', BigInteger),
#         Column('충당부채', BigInteger),
#         Column('기타유동부채', BigInteger),
#         Column('비유동부채', BigInteger),
#         Column('사채', BigInteger),
#         Column('장기차입금', BigInteger),
#         Column('장기선수금', BigInteger),
#         Column('순확정급여부채', BigInteger),
#         Column('장기종업원급여충당부채', BigInteger),
#         Column('장기충당부채', BigInteger),
#         Column('이연법인세부채', BigInteger),
#         Column('기타비유동부채', BigInteger),
#         Column('부채총계', BigInteger),
#         Column('지배기업 소유주지분', BigInteger),
#         Column('보통주자본금', BigInteger),
#         Column('주식발행초과금', BigInteger),
#         Column('이익잉여금', BigInteger),
#         Column('기타포괄손익누계액', BigInteger),
#         Column('기타자본항목', BigInteger),
#         Column('비지배지분', BigInteger),
#         Column('자본총계', BigInteger),
#         Column('부채와자본총계', BigInteger)
#     )
#
#     return finance_table


def create_tables():
    create_table_date()
    create_table_company()
    create_table_stock()
    create_table_news()
    create_table_tweet()
    create_table_obj_news_count()
    # create_table_finance()
    create_table_obj_result()


if __name__ == "__main__":
    date_table = create_table_date()
    init_table_date(date_table)
    update_trading_date(date_table)

    create_tables()
