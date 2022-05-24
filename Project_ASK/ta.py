import pandas as pd
import talib

if __name__ == "__main__":
    df = pd.read_excel(r"./dataset/stockData/car/기아/기아_s.xlsx")

    print(df.columns)

    close = df['종가'].abs()

    print(close)

    # ub, mb, lb = talib.BBANDS(close, timeperiod=5)

    ma = talib.SMA(close, 20)

    print(ma)
