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


def getStockPrice_to_excel(category, companyName, stockCode, e_date=None):
    s_date = "20000125"
    start = time.time()

    index_col = '날짜'
    dateFormat = "%Y%m%d"

    # 문자열로 오늘 날짜 얻기
    if e_date is None:
        now = datetime.now()
        today = now.strftime(dateFormat)
        e_date = today

    path = fr'{Path.RESULT_PATH_STOCK}\{category}\{companyName}'
    output_file_name = fr"{path}\{companyName}_s.xlsx"

    df = pd.DataFrame()
    if os.path.exists(output_file_name):
        df = pd.read_excel(output_file_name, dtype={index_col: str})
        df.set_index(index_col, drop=True, inplace=True)
        df.dropna(how='all', inplace=True)
        s_date = df.index.tolist()[-1]
        print(F'{type(s_date)}_________{s_date}')

    if s_date == e_date:
        print(f"already latest data: {s_date}")
        return False
    else:
        print(f"date: {s_date}   {e_date}")
        kiwoom = connectKiwoom()

        call_date = datetime.strptime(s_date, dateFormat)

        while (datetime.strptime(e_date, dateFormat) - call_date).days > 0:
            if (datetime.strptime(e_date, dateFormat) - call_date).days > 35:
                days = 25
            else:
                days = 1
            call_date = call_date + timedelta(days=days)
            data = kiwoom.block_request("opt10086",
                                        종목코드=stockCode,
                                        조회일자=call_date.strftime(dateFormat),
                                        표시구분=1,
                                        output="일별주가",
                                        next=0)

            if data is None:
                print(f"There is no data got: {s_date}-{e_date}")
                return 0

            data.sort_values(by=index_col, axis=0, ascending=True, inplace=True, kind='quicksort')
            data.set_index(index_col, inplace=True)
            df = pd.concat([df, data])
            print(f'{call_date.strftime(dateFormat)}/{e_date}')
            time.sleep(0.6)

        df.dropna(how='all', inplace=True)
        df.drop_duplicates(subset=None, keep='first', inplace=True, ignore_index=False)
        Path.createFolder(path)
        with pd.ExcelWriter(output_file_name, mode='w', engine='openpyxl') as writer:
            df.to_excel(writer, index_label=index_col)

        print("time :", time.time() - start)

        return path, output_file_name


def getStockPrice(company, stockCode, e_date=None):
    s_date = "20000125"
    start = time.time()

    index_col = '날짜'
    dateFormat = "%Y%m%d"

    # 문자열로 오늘 날짜 얻기
    if e_date is None:
        now = datetime.now()
        today = now.strftime(dateFormat)
        e_date = today


    df = pd.DataFrame()

    if s_date == e_date:
        print(f"already latest data: {s_date}")
        return False
    else:
        print(f"date: {s_date}   {e_date}")
        kiwoom = connectKiwoom()

        call_date = datetime.strptime(s_date, dateFormat)

        while (datetime.strptime(e_date, dateFormat) - call_date).days > 0:
            if (datetime.strptime(e_date, dateFormat) - call_date).days > 35:
                days = 25
            else:
                days = 1
            call_date = call_date + timedelta(days=days)
            data = kiwoom.block_request("opt10086",
                                        종목코드=stockCode,
                                        조회일자=call_date.strftime(dateFormat),
                                        표시구분=1,
                                        output="일별주가",
                                        next=0)

            if data is None:
                print(f"There is no data got: {s_date}-{e_date}")
                return False

            data.sort_values(by=index_col, axis=0, ascending=True, inplace=True, kind='quicksort')
            data.set_index(index_col, inplace=True)
            df = pd.concat([df, data])
            print(f'{call_date.strftime(dateFormat)}/{e_date}')
            time.sleep(0.6)

        df.dropna(how='all', inplace=True)
        df['company'] = company
        df.drop_duplicates(subset=None, keep='first', inplace=True, ignore_index=False)

        insert

        print("time :", time.time() - start)

        return True


if __name__ == "__main__":
    print('start getStockPrice code')
    argList = sys.argv

    date = None

    if len(argList) >= 2:
        argList = argList[1:]
    if len(argList) >= 3:
        category = argList[0]
        companyName = argList[1]
        stockCode = argList[2]

        if len(argList) >= 4:
            date = argList[3]

        if getStockPrice(company=companyName, stockCode=stockCode, e_date=date):
            print(f'{category} / {companyName} get Stock price complete')

    else:
        print("Error: getStockPrice has no parameter")

    # category = "car"
    # companyName = "현대차"
    # index_col = '날짜'
    # dateFormat = "%Y%m%d"
    # stockCode = "005380"
    # # s_date = "20000101"

    getStockPrice(category, companyName, stockCode)
