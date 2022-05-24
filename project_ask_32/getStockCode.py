import json
import sys
from connectKiwoom import connectKiwoom
from stockJson import writeJson

enterThemeList = {'미디어_방송광고': '280', '컨텐츠_메니지먼트': '284', '컨텐츠_영상': '282', '컨텐츠_음원': '283', '컨텐츠_한류': '285'}


def getStockCode(filePath, fileName, kiwoom=None):
    # '미디어_디지털방송전환': '281',
    if kiwoom is None:
        kiwoom = connectKiwoom()

    with open(fr'{filePath}\{fileName}', 'r', encoding='UTF8') as f:
        stockDict = json.load(f)

    allStockCode = dict()
    targetStockCode = dict()

    kospi_code_list = kiwoom.GetCodeListByMarket(0)     # 코스피
    kospi_code_list += kiwoom.GetCodeListByMarket(10)   # 코스닥

    for code in kospi_code_list:
        name = kiwoom.GetMasterCodeName(code)
        print(name)
        allStockCode[name] = code

    for k in stockDict.keys():
        targetStockCode[k] = {}

    for k, v in stockDict.items():
        for companyName in v:
            targetStockCode[k][companyName] = allStockCode[companyName]

    writeJson(targetStockCode, filePath, fileName)
    return targetStockCode


if __name__ == "__main__":
    print("start getStockCode")

    kiwoom = connectKiwoom()

    argList = sys.argv
    del argList[0]

    # print(argList)

    stockCode = getStockCode(argList[0], argList[1], kiwoom)
    # print(stockCode)