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


def getStockPrice(company, stockCode, day):
    dateFormat = "%Y%m%d"

    day_str = day.strftime(dateFormat)

    kiwoom = connectKiwoom()

    data = kiwoom.block_request("opt10086",
                                종목코드=stockCode,
                                조회일자=day_str,
                                표시구분=1,
                                output="일별주가",
                                next=0)

    if data is not None:
        data['company'] = company
        data['날짜'] = data['날짜'].map(lambda x: datetime.strptime(x, date_format))

        numeric_cols = ['시가', '고가', '저가', '종가', '전일비', '등락률', '거래량', '금액(백만)', '신용비', '외인비', '체결강도', '외인보유', '외인비중',
                        '신용잔고율']

        df = data.where(pd.notnull(data), None)
        df.loc[:, numeric_cols] = df.loc[:, numeric_cols].apply(pd.to_numeric)
        insert_table_stock(df)


if __name__ == "__main__":
    print('start getStockPrice code')

    # argList = sys.argv[1:]

    argList = ['현대차', '005380', '20220101', '20220430']

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

    while (end_date - cur_date).days >= 0:
        getStockPrice(company, stockCode, cur_date)
        cur_date = cur_date + timedelta(days=1)
        time.sleep(0.6)
