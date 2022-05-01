from sqlalchemy import Table, Column, ForeignKey, Integer, BigInteger, FLOAT, VARCHAR, DATE, BOOLEAN, MetaData, \
    create_engine
from sqlalchemy import insert, update
import sqlalchemy
import pandas as pd

pw = 'ask1234!'
db_connection_address = f'mysql+pymysql://root:{pw}@localhost:3306/ASK'
# db_connection_address = f'mysql+pymysql://root:{pw}@13.209.122.152:3306/ASK'

meta = MetaData()
engine = create_engine(db_connection_address)


def create_table_stock(db_meta=meta):
    get_table_obj_stock(db_meta)
    meta.create_all(engine)


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


def read_table_stock(company):
    SQL = f'SELECT * FROM crawl_stock_table WHERE company="{company}"'
    db_connection = create_engine(db_connection_address)
    df = pd.read_sql(SQL, db_connection)
    return df


def insert_table_stock(df_stock):
    create_table_stock()
    db_connection = create_engine(db_connection_address)

    with db_connection.connect() as conn:
        meta = MetaData(bind=conn)
        tweet_table = get_table_obj_stock(meta)
        for idx, row in df_stock.iterrows():
            try:
                result = conn.execute(
                    insert(tweet_table),
                    [
                        {
                            "company": row["company"],
                            "날짜": row["날짜"],
                            "시가": row["시가"],
                            "고가": row["고가"],
                            "저가": row["저가"],
                            "종가": row["종가"],
                            "전일비": row["전일비"],
                            "등락률": row["등락률"],
                            "거래량": row["거래량"],
                            "금액(백만)": row["금액(백만)"],
                            "신용비": row["신용비"],
                            "개인": row["개인"],
                            "기관": row["기관"],
                            "외인수량": row["외인수량"],
                            "외국계": row["외국계"],
                            "프로그램": row["프로그램"],
                            "외인비": row["외인비"],
                            "체결강도": row["체결강도"],
                            "외인보유": row["외인보유"],
                            "외인비중": row["외인비중"],
                            "외인순매수": row["외인순매수"],
                            "기관순매수": row["기관순매수"],
                            "개인순매수": row["개인순매수"],
                            "신용잔고율": row["신용잔고율"]
                        }
                    ]
                )
            except Exception as e:
                print(e)


if __name__ == '__main__':
    # l = []
    # with open('te.txt', 'r') as f:
    #     lines = f.readlines()
    #
    #     for line in lines:
    #         l.append(line.strip().split('\'')[1])
    #
    # res = [f'"{w}": row["{w}"],\n' for w in l]
    #
    # with open('tex.txt', 'w') as f:
    #     f.writelines(res)
    pass
