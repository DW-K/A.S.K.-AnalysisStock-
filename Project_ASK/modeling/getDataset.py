import pandas as pd
from sklearn.preprocessing import StandardScaler
import torch
from torch.utils.data import Dataset, DataLoader


def preprocess(df_stock, get_test=False, train_bound_e=0, train_bound_s=0):  # 0: 가장 최근, 1: 1분기 전
    if train_bound_e > train_bound_s:
        raise Exception("boundary error")

    target_col = "등락률"
    df_stock['날짜'] = pd.to_datetime(df_stock['날짜'], format='%Y%m%d', errors='raise')
    df_drop_na = df_stock.drop(["체결강도", "외인수량", "외인비", "외인보유", "외인비중", "외인순매수"], axis=1)
    d = df_drop_na.dtypes
    string_cols = d[d == "object"].index.tolist()
    df_drop_na[string_cols] = df_drop_na[string_cols].replace('^--|\+-(.*)', r'-\1',
                                                              regex=True)  # 앞에 --나 +-가 붙어있으면 -로 바꿈
    df_drop_na[string_cols] = df_drop_na[string_cols].apply(pd.to_numeric)
    df_drop_price = df_drop_na.drop(["시가", "고가", "저가", "종가", "전일비", "신용비", "개인", "기관"], axis=1)

    ss = StandardScaler()
    ss.fit(df_drop_price.drop(["날짜", target_col], axis=1))

    df_drop_price['quarter'] = pd.PeriodIndex(df_drop_price['날짜'], freq='Q')
    quarter_list = df_drop_price['quarter'].unique()

    train_bound_s = (train_bound_s + 1) * -1
    if train_bound_e == 0:
        train_bound_e = None

    tensor_X_list = []
    tensor_y_list = []

    for q in quarter_list:
        quarter_df = df_drop_price[df_drop_price['quarter'] == q]
        X = quarter_df.drop(["날짜", "quarter", target_col], axis=1)
        y = quarter_df[target_col]

        tensor_X = torch.tensor(ss.transform(X))
        tensor_y = torch.tensor(y.values)

        tensor_X_list.append(tensor_X)
        tensor_y_list.append(tensor_y)

    return tensor_X_list[:train_bound_s], tensor_y_list[:train_bound_s], \
           tensor_X_list[train_bound_s: train_bound_e], tensor_y_list[train_bound_s: train_bound_e]


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

        self.max_seq_size = -1        # for batch
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
    train_X, train_y, test_X, test_y = preprocess(df, get_test=True, train_bound_e=0, train_bound_s=0)

    ds = myDatasetSameSize(train_X, train_y)

    for i in range(ds.__len__()):
        print(ds.__getitem__(i)[0].shape)

# def scaling(df, target_col):
#     X = df.drop(target_col, axis=1)
#     y = df[target_col]
#
#     ss = StandardScaler()
#     X_scaled = ss.fit_transform(X)
#
#     return X_scaled, y
#
#
# def preprocess(df_stock, get_test=False, train_bound_e=0, train_bound_s=0):  # 0: 가장 최근, 1: 1분기 전
#     if train_bound_e > train_bound_s:
#         raise Exception("boundary error")
#
#     target_col = "등락률"
#     df_stock['날짜'] = pd.to_datetime(df_stock['날짜'], format='%Y%m%d', errors='raise')
#     df_drop_na = df_stock.drop(["체결강도", "외인수량", "외인비", "외인보유", "외인비중", "외인순매수"], axis=1)
#     d = df_drop_na.dtypes
#     string_cols = d[d == "object"].index.tolist()
#     df_drop_na[string_cols] = df_drop_na[string_cols].replace('^--|\+-(.*)', r'-\1',
#                                                               regex=True)  # 앞에 --나 +-가 붙어있으면 -로 바꿈
#     df_drop_na[string_cols] = df_drop_na[string_cols].apply(pd.to_numeric)
#     df_drop_price = df_drop_na.drop(["시가", "고가", "저가", "종가", "전일비", "신용비", "개인", "기관"], axis=1)
#
#     if get_test:
#         df_drop_price['quarter'] = pd.PeriodIndex(df_drop_price['날짜'], freq='Q')
#         quarter_list = df_drop_price['quarter'].unique()
#
#         train_bound_s = (train_bound_s + 1) * -1
#         if train_bound_e == 0:
#             train_bound_e = None
#
#         test_df = df_drop_price[df_drop_price['quarter'].isin(quarter_list[train_bound_s:train_bound_e])].copy()
#         train_df = df_drop_price[~df_drop_price['quarter'].isin(quarter_list[train_bound_s:train_bound_e])].copy()
#         test_df.drop(["날짜", "quarter"], axis=1, inplace=True)
#         train_df.drop(["날짜", "quarter"], axis=1, inplace=True)
#
#         test_df_X, test_df_y = scaling(test_df, target_col)
#     else:
#         train_df = df_drop_price.drop("날짜", axis=1)
#         test_df_X, test_df_y = pd.DataFrame(), pd.DataFrame()  # empty dataframe
#
#     train_df_X, train_df_y = scaling(train_df, target_col)
#
#     return map(torch.tensor, (train_df_X, train_df_y.values, test_df_X, test_df_y.values))