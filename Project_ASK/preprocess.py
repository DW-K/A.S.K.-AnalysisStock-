import numpy as np
import pandas as pd
from sklearn.preprocessing import StandardScaler
import matplotlib.pyplot as plt
from Path import RESULT_PATH_COMBINE

path = r'dataset\combine\entertainment\result.xlsx'

# pd.set_option('display.max_rows', None)
# pd.set_option('display.max_columns', None)
# pd.set_option('display.width', None)
# pd.set_option('display.max_colwidth', -1)

step_size = 65
col_size = 7


def scaling(arr):
    # scaling
    ss = StandardScaler()

    arr_scaled = np.zeros(shape=(len(arr), step_size, col_size))  # quarter, isUp 제외하고 scaling
    for i in range(len(arr)):
        arr_scaled[i] = np.concatenate(
            [arr[i, :, 0].reshape(1, -1, 1),
             ss.fit_transform(arr[i, :, 1:]).reshape(1, step_size, col_size - 1)],
            axis=2)
    return arr_scaled


def preprocessing(category, companyName):
    filePath = fr"{RESULT_PATH_COMBINE}\{category}\{companyName}"
    fileName = f"{companyName}_combine.xlsx"
    total_name = f"{filePath}\{fileName}"

    str_col = ["개인", "기관", "외인수량", "외국계", "프로그램", "외인순매수", "기관순매수", "개인순매수"]

    df = pd.read_excel(total_name, index_col='날짜')

    df.drop("체결강도", axis=1, inplace=True)
    # df.drop("news_sentiment_logit", axis=1, inplace=True)
    df = df.iloc[-236:, :]

    # for col in str_col:
    #     for row in df.index:
    #         if df.loc[row, col]

    for col in str_col:
        df.loc[:, col] = df.loc[:, col].str.replace("--", "-", regex=False)

    # 1 preprocess per batch

    # print(df.columns)
    # print(df)

    y = df["전일비"].values
    y = y.reshape(-1, 1)
    X = df.drop("전일비", axis=1)

    train_index = 160

    X_train = X.iloc[:train_index, :]
    X_test = X.iloc[train_index:, :]
    y_train = y[:train_index, :]
    y_test = y[train_index:, :]

    ss = StandardScaler()

    ss.fit(X_train)
    X_train_scaled = ss.transform(X_train)
    X_test_scaled = ss.transform(X_test)

    ss.fit(y_train)
    y_train_scaled = ss.transform(y_train)
    y_test_scaled = ss.transform(y_test)

    return X_train_scaled, X_test_scaled, y_train_scaled, y_test_scaled


if __name__ == "__main__":
    category = "car"
    companyName = "기아"
    s_date = "20211014"
    e_date = "20211203"
    preprocessing(category=category, companyName=companyName)
