import sys

import pandas as pd
from pykiwoom.kiwoom import *
import Path
import os
from datetime import datetime, timedelta

import stockJson
from connectKiwoom import connectKiwoom
from db import insert_table_stock
from getStockCode import getStockCode

import time

# row 생략 없이 출력
pd.set_option('display.max_rows', None)
# col 생략 없이 출력
pd.set_option('display.max_columns', None)


def getStockPrice(company, stockCode, s_date, e_date=None):
    kiwoom = connectKiwoom()
    date_format = "%Y%m%d"

    df = pd.DataFrame()

    while (e_date - s_date).days > 0:
        if (e_date - s_date).days > 35:
            days = 25
        else:
            days = 1
        s_date = s_date + timedelta(days=days)
        data = kiwoom.block_request("opt10086",
                                    종목코드=stockCode,
                                    조회일자=s_date.strftime(date_format),
                                    표시구분=1,
                                    output="일별주가",
                                    next=0)

        time.sleep(0.6)

        df = df.append(data)

    df.drop_duplicates(subset=None, keep='first', inplace=True, ignore_index=True)
    df['날짜'] = df['날짜'].map(lambda x: datetime.strptime(x, date_format))

    numeric_cols = ['시가', '고가', '저가', '종가', '전일비', '등락률', '거래량', '금액(백만)', '신용비', '외인비', '체결강도', '외인보유', '외인비중', '신용잔고율']

    df.loc[:, numeric_cols] = df.loc[:, numeric_cols].apply(pd.to_numeric)
    df = df.where(pd.notnull(df), 0)

    df.sort_values(by='날짜', axis=0, ascending=True, inplace=True, kind='quicksort')
    df['company'] = company
    insert_table_stock(df)


if __name__ == "__main__":
    print('start getStockPrice')

    # argList = sys.argv[1:]
    argList = ['현대차', '005380', '20120101', '20220430']
    company = argList[0]
    stockCode = argList[1]
    start_date_str = argList[2]

    if len(argList) == 3:
        end_date_str = start_date_str
    elif len(argList) == 4:
        end_date_str = argList[3]

    date_format = "%Y%m%d"

    cur_date = datetime.strptime(start_date_str, date_format)
    end_date = datetime.strptime(end_date_str, date_format)

    getStockPrice(company, stockCode, cur_date, end_date)
