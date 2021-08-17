import sys

from pykiwoom.kiwoom import *
import Path
from connectKiwoom import connectKiwoom
from getStockCode import getStockCode
from makeUpdown import makeUpdown


def getStockPrice(date=None):
    # # 전종목 종목코드
    # kospi = kiwoom.GetCodeListByMarket('0')
    # kosdaq = kiwoom.GetCodeListByMarket('10')
    # codes = kospi + kosdaq
    kiwoom = connectKiwoom()

    enterCodes = getStockCode(kiwoom=kiwoom)

    # 문자열로 오늘 날짜 얻기
    now = datetime.datetime.now()
    today = now.strftime("%Y%m%d")

    if date is None:
        date = today

    path = fr'{Path.RESULT_PATH_STOCK}\{date[2:]}'
    # 전 종목의 일봉 데이터
    for i, (name, code) in enumerate(enterCodes.items()):
        # print(f"{i}/{len(codes)} {name} : {code}")
        df = kiwoom.block_request("opt10081",
                                  종목코드=code,
                                  기준일자=date,
                                  수정주가구분=1,
                                  output="주식일봉차트조회",
                                  next=0)

        Path.createFolder(path)
        out_name = fr"{path}\{name}_{date[2:]}_s.xlsx"
        df['일자'] = df['일자'].astype(str)
        df.to_excel(out_name, index_label="index")
        time.sleep(3.6)
    return date[2:]


if __name__ == "__main__":
    print('start getStockPrice code')
    argList = sys.argv
    del argList[0]
    date = None

    if len(argList) > 0:
        date = argList[0]

    if date is None:
        date = getStockPrice()
    else:
        date = getStockPrice(date)

    makeUpdown(date=date)
