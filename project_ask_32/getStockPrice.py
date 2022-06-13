import sys

import pandas as pd
import numpy as np
from pykiwoom.kiwoom import *
import Path
import os
from datetime import datetime, timedelta

import stockJson
from connectKiwoom import connectKiwoom
from db import insert_table_stock, create_table_stock
from getStockCode import getStockCode

import time

# row 생략 없이 출력
pd.set_option('display.max_rows', None)
# col 생략 없이 출력
pd.set_option('display.max_columns', None)


def getStockPrice(company, stockCode, s_day, e_day=None):
    dateFormat = "%Y%m%d"

    if e_day is None:
        e_day = s_day

    kiwoom = connectKiwoom()

    df = pd.DataFrame()

    while (e_day+timedelta(days=25) - s_day).days > 0:
        data = kiwoom.block_request("opt10086",
                                    종목코드=stockCode,
                                    조회일자=s_day.strftime(dateFormat),
                                    표시구분=1,
                                    output="일별주가",
                                    next=0)

        if data is not None:
            data['company'] = company
            data['날짜'] = data['날짜'].map(lambda x: datetime.strptime(x, date_format))

            numeric_cols = ['시가', '고가', '저가', '종가', '전일비', '등락률', '거래량', '금액(백만)', '신용비', '외인비', '체결강도', '외인보유', '외인비중',
                        '신용잔고율']

        df = df.append(data.astype(object).replace("", 0))

        s_day = s_day + timedelta(days=25)

        time.sleep(0.8)

    df.loc[:, numeric_cols] = df.loc[:, numeric_cols].apply(pd.to_numeric)
    insert_table_stock(df)


if __name__ == "__main__":
    print('start getStockPrice code')

    # argList = sys.argv[1:
    # ---------------------------------------------------------
    arg_list1 = ['카카오', '035720', '20220501', '20220530']
    arg_list2 = ['하이브', '352820', '20220501', '20220530']
    #arg_list3 = ['카카오', '035720', '20220501', '20220530']
    #arg_list4 = ['하이브', '352820', '20220501', '20220530']

    arg_list_list = [arg_list1, arg_list2]

    create_table_stock()

    for arg_list in arg_list_list:
        company = arg_list[0]
        query_list = arg_list[1]
        start_date_str = arg_list[2]
        end_date_str = arg_list[3]

        date_format = "%Y%m%d"

        start_date = datetime.strptime(start_date_str, date_format)
        end_date = datetime.strptime(end_date_str, date_format)

        print(company, query_list, start_date, end_date)

        getStockPrice(company, query_list, start_date, end_date)
    # ---------------------------------------------------------

