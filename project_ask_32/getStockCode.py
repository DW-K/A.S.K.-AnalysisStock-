from connectKiwoom import connectKiwoom

enterThemeList = {'미디어_방송광고': '280', '컨텐츠_메니지먼트': '284', '컨텐츠_영상': '282', '컨텐츠_음원': '283', '컨텐츠_한류': '285'}


def getStockCode(kiwoom=None, themeList=enterThemeList):
    # '미디어_디지털방송전환': '281',
    if kiwoom is None:
        kiwoom = connectKiwoom()

    stockCode = dict()
    for key, value in themeList.items():
        # print(f'-------------{key}--------------')
        tickers = kiwoom.GetThemeGroupCode(value)

        for ticker in tickers:
            name = kiwoom.GetMasterCodeName(ticker)
            stockCode[name] = ticker
            # print(f'{name} : {stockCode[name]}')

    return stockCode


if __name__ == "__main__":
    kiwoom = connectKiwoom()
    stockCode = getStockCode(kiwoom, enterThemeList)
    print(stockCode)