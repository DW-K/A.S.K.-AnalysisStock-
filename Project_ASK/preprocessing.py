import numpy as np
import pandas as pd
from sklearn.preprocessing import StandardScaler
import matplotlib.pyplot as plt

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


def preprocessing():
    df = pd.read_excel(path, index_col='date')
    # df.loc[:, '시가'].plot(grid=True)
    # plt.show()
    # 1 preprocess per batch

    col_name = list(df.columns)  # df column 순서 바꾸기
    col_name.remove('quarter')
    col_name.remove('isUp')
    col_name.insert(0, 'quarter')

    y = df['isUp'].to_numpy().reshape(-1, 1)
    y = y[:-step_size]

    # print(y.shape)

    df = df[col_name]
    arr_index_list = []

    arr = np.zeros(shape=(1, step_size, col_size))

    for i in range(0, df.shape[0] - step_size, 1):
        temp = df.iloc[i:i+step_size, :]

        arr_index_list.append(list(temp.index))

        temp_arr = temp.to_numpy()
        arr = np.vstack((arr, temp_arr.reshape(1, step_size, col_size)))
        # pre_temp = temp

    arr = np.delete(arr, 0, axis=0)  # 빈 array 행 제거 (첫번째 행)

    arr_scaled = scaling(arr)
    # print(arr_scaled.shape)
    # print(arr_index_list)

    # x = 2
    # print(pd.DataFrame(arr_scaled[x], index=date_list[x], columns=col_name))
    # print(pd.DataFrame(recent_scaled, index=recent_date, columns=col_name))

    return arr_scaled, y, arr_index_list


if __name__ == "__main__":
    preprocessing()
