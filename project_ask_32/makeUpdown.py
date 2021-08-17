import pandas as pd
import os
import sys

import Path


def upDown(df):
    upDownList = []

    for i in range(0, len(df.index) - 1):
        if df.iloc[i].loc['시가'] != 0 and df.iloc[i+1].loc['시가'] != 0:
            percent = (df.iloc[i].loc['시가'] - df.iloc[i+1].loc['시가']) / df.iloc[i+1].loc['시가']
            upDownList.append(percent)
        else:
            upDownList.append(0)

    df = df[1:] # 다음날의 가격을 예측하는 것이므로 가장 최근 날은 뺀다
    df = df.assign(isUp=upDownList)
    df.reset_index()
    return df


def makeUpdown(date):
    filePath = Path.RESULT_PATH_STOCK
    # dateList = os.listdir(filePath)
    fileList = os.listdir(fr'{filePath}\{date}')

    # print('진행률 :')
    for i, fileName in enumerate(fileList):
        # print(f'{i}/{len(fileList)}')
        outName = fr'{filePath}\{date}\{fileName}'
        df = pd.read_excel(outName, index_col="index")

        col_list = ['현재가', '거래량', '거래대금', '일자', '시가', '고가', '저가']

        df = upDown(df[col_list])
        df.to_excel(outName, index_label='index')


if __name__ == "__main__":
    date = sys.argv[1]

    makeUpdown(date)

    print('make updown 완료')