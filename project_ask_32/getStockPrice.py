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



def getStockPrice(category, companyName, stockCode, e_date=None):
    # # 전종목 종목코드
    # kospi = kiwoom.GetCodeListByMarket('0')
    # kosdaq = kiwoom.GetCodeListByMarket('10')
    # codes = kospi + kosdaq
    start = time.time()

    kiwoom = connectKiwoom()

    index_col = '날짜'
    dateFormat = "%Y%m%d"
    s_date = "20000125"

    # 문자열로 오늘 날짜 얻기
    if e_date is None:
        now = datetime.now()
        today = now.strftime(dateFormat)
        e_date = today

    path = fr'{Path.RESULT_PATH_STOCK}\{category}\{companyName}'
    output_file_name = fr"{path}\{companyName}_s.xlsx"

    df = pd.DataFrame()
    if os.path.exists(output_file_name):
        df = pd.read_excel(output_file_name, index_col=index_col)

    call_date = datetime.strptime(s_date, dateFormat)

    while (datetime.strptime(e_date, dateFormat) - call_date).days > 0:
        call_date = call_date + timedelta(days=25)
        data = kiwoom.block_request("opt10086",
                                    종목코드=stockCode,
                                    조회일자=call_date.strftime(dateFormat),
                                    표시구분=1,
                                    output="일별주가",
                                    next=0)

        data.sort_values(by=index_col, axis=0, ascending=True, inplace=True, kind='quicksort')
        data.set_index(index_col, inplace=True)
        df = pd.concat([df, data])
        print(f'{call_date.strftime(dateFormat)}/{e_date}')
        time.sleep(0.48)

    df.drop_duplicates(subset=None, keep='first', inplace=True, ignore_index=False)
    Path.createFolder(path)
    with pd.ExcelWriter(output_file_name, mode='w', engine='openpyxl') as writer:
        df.to_excel(writer, index_label=index_col)

    print("time :", time.time() - start)

    return path, output_file_name


if __name__ == "__main__":
    # print('start getStockPrice code')
    # argList = sys.argv
    #
    # date = None
    #
    # if len(argList) >= 2:
    #     argList = argList[1:]
    # if len(argList) >= 3:
    #     category = argList[0]
    #     companyName = argList[1]
    #     stockCode = argList[2]
    #
    #     if len(argList) >= 4:
    #         date = argList[3]
    #
    #     if getStockPrice(category=category, companyName=companyName, stockCode=stockCode, e_date=date):
    #         print(f'{category} / {companyName} get Stock price complete')
    #
    # else:
    #     print("Error: getStockPrice has no parameter")
    # date = getStockPrice(category="car", companyName="현대차", stockCode="005380")

    category = "car"
    companyName = "현대차"
    e_date = "20180101"
    index_col = '날짜'
    dateFormat = "%Y%m%d"
    s_date = "20000101"

    # 문자열로 오늘 날짜 얻기
    if e_date is None:
        now = datetime.now()
        today = now.strftime(dateFormat)
        e_date = today

    path = fr'{Path.RESULT_PATH_STOCK}\{category}\{companyName}'
    output_file_name = fr"{path}\{companyName}_s.xlsx"

    df = pd.DataFrame()
    if os.path.exists(output_file_name):
        df = pd.read_excel(output_file_name, index_col=index_col)
        date_list = df.index
        f_date = date_list[0]
        l_date = date_list[-1]

        print(f'{f_date}, {l_date}')

