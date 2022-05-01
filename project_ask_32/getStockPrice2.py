import sys

import pandas as pd
from pykiwoom.kiwoom import *
import Path
import os
from datetime import datetime, timedelta

import stockJson
from connectKiwoom import connectKiwoom
from getStockCode import getStockCode

import time


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
    df.sort_values(by='날짜', axis=0, ascending=True, inplace=True, kind='quicksort')
    df['company'] = company
    print(df)


if __name__ == "__main__":
    print('start getStockPrice code')

    # argList = sys.argv[1:]
    argList = ['기아', '000270', '20220101', '20220430']
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
