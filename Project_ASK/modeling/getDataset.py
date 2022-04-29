import pandas as pd
from sklearn.preprocessing import StandardScaler
import torch
from torch.utils.data import Dataset, DataLoader
import matplotlib.pyplot as plt
import seaborn as sns


pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)


def preprocess(df_stock, get_test=False, train_bound_e=0, train_bound_s=0):  # 0: 가장 최근, 1: 1분기 전
    if train_bound_e > train_bound_s:
        raise Exception("boundary error")

    df_fin = pd.read_excel(r'../dataset/fs2.xlsx', index_col='Unnamed: 0')
    # df_fin.index = pd.to_datetime(df_fin.index, format="%Y%m%d")
    df_fin = df_fin.iloc[:, :]
    df_fin = df_fin[::-1]

    df_stock['날짜'] = df_stock['날짜'].astype('int')

    na_col = ['체결강도', '기타유동금융자산', '비지배지분']

    fin_date_list = list(df_fin.index)
    for i in range(len(fin_date_list)-1):
        start_date = fin_date_list[i]
        end_date = fin_date_list[i+1]
        temp_df = df_stock[(start_date <= df_stock['날짜']) & (df_stock['날짜'] < end_date)].copy()
        # print(df_stock[(start_date <= df_stock['날짜']) & (df_stock['날짜'] < end_date)])

        # for j in range(temp_df.shape[0]):
        #     temp_df.iloc[j, :] = pd.concat([temp_df.iloc[j, :], df_fin.loc[start_date, :]])
        # print(df_fin.columns)
        concat_df = pd.DataFrame([df_fin.loc[start_date, :] for _ in range(temp_df.shape[0])], columns=df_fin.columns)

        temp_concat_df = pd.concat([temp_df.reset_index().drop("index", axis=1), concat_df.reset_index().drop("index", axis=1)], axis=1)

        df_drop_na = temp_concat_df.drop(na_col, axis=1)

        print(df_drop_na.to_excel("ffdsaf.xlsx"))

        df_drop_na.set_index("날짜", drop=True, append=True, inplace=True)

        d = df_drop_na.dtypes
        string_cols = d[d == "object"].index.tolist()
        df_drop_na[string_cols] = df_drop_na[string_cols].replace('^--|\+-(.*)', r'-\1',
                                                                  regex=True)  # 앞에 --나 +-가 붙어있으면 -로 바꿈
        df_drop_na[string_cols] = df_drop_na[string_cols].apply(pd.to_numeric)

        print(f'{i}/{len(fin_date_list)-1}')
        plt.rc('font', family='Malgun Gothic')
        sns.pairplot(data=df_drop_na)
        plt.show()






    # target_col = "등락률"
    # df_stock['날짜'] = pd.to_datetime(df_stock['날짜'], format='%Y%m%d', errors='raise')
    # df_drop_na = df_stock.drop(["체결강도", "외인수량", "외인비", "외인보유", "외인비중", "외인순매수"], axis=1)
    # d = df_drop_na.dtypes
    # string_cols = d[d == "object"].index.tolist()
    # df_drop_na[string_cols] = df_drop_na[string_cols].replace('^--|\+-(.*)', r'-\1',
    #                                                           regex=True)  # 앞에 --나 +-가 붙어있으면 -로 바꿈
    #
    # # print(string_cols)
    # df_drop_na[string_cols] = df_drop_na[string_cols].apply(pd.to_numeric)
    # df_drop_price = df_drop_na.drop(["시가", "고가", "저가", "종가", "전일비", "신용비", "개인", "기관"], axis=1)
    #
    # ss = StandardScaler()
    # ss.fit(df_drop_price.drop(["날짜", target_col], axis=1))
    #
    # df_drop_price['quarter'] = pd.PeriodIndex(df_drop_price['날짜'], freq='Q')
    # quarter_list = df_drop_price['quarter'].unique()
    #
    # train_bound_s = (train_bound_s + 1) * -1
    # if train_bound_e == 0:
    #     train_bound_e = None
    #
    # tensor_X_list = []
    # tensor_y_list = []
    #
    # for q in quarter_list:
    #     quarter_df = df_drop_price[df_drop_price['quarter'] == q]
    #     X = quarter_df.drop(["날짜", "quarter", target_col], axis=1)
    #     y = quarter_df[target_col]
    #
    #     tensor_X = torch.tensor(ss.transform(X))
    #     tensor_y = torch.tensor(y.values)
    #
    #     tensor_X_list.append(tensor_X)
    #     tensor_y_list.append(tensor_y)
    #
    # return tensor_X_list[:train_bound_s], tensor_y_list[:train_bound_s], \
    #        tensor_X_list[train_bound_s: train_bound_e], tensor_y_list[train_bound_s: train_bound_e]


class myDataset(Dataset):
    def __init__(self, X, y):
        self.X = X
        self.y = y

    def __len__(self):
        return len(self.X)

    def __getitem__(self, idx):
        X_output = self.X[idx]
        y_output = self.y[idx]

        return X_output, y_output


class myDatasetSameSize(Dataset):
    def __init__(self, X, y):
        self.X = X
        self.y = y

        self.max_seq_size = -1  # for batch
        for q in self.X:
            if self.max_seq_size < len(q):
                self.max_seq_size = len(q)

    def __len__(self):
        return len(self.X)

    def __getitem__(self, idx):
        X_output = self.X[idx]
        y_output = self.y[idx]

        add_size = self.max_seq_size - len(X_output)  # for batch

        X_output = torch.cat([X_output, torch.zeros(add_size, X_output.shape[-1])], dim=0)
        y_output = torch.cat([y_output, torch.zeros(add_size)], dim=0)

        return X_output, y_output


if __name__ == "__main__":
    df = pd.read_excel(r"../dataset/stockData/car/기아/기아_s.xlsx")
    # train_X, train_y, test_X, test_y = preprocess(df, get_test=True, train_bound_e=0, train_bound_s=0)
    preprocess(df, get_test=True, train_bound_e=0, train_bound_s=0)

    # ds = myDatasetSameSize(train_X, train_y)

    # for i in range(ds.__len__()):
    #     print(ds.__getitem__(i)[0].shape)
