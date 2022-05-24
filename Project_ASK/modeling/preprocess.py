import numpy as np
import torch

from getData import get_data

from sklearn.preprocessing import StandardScaler
from datetime import date
import torch.utils.data as data

import joblib


def preprocessing(company, s_date, e_date, path, is_train=False):
    data = get_data(company, s_date, e_date)

    sentiment_df = data["sentiment"]

    eps = 1e-8

    sentiment_df["logit"] = sentiment_df["positive"] / (sentiment_df["negative"] + eps)
    stock_df = data["stock"]

    if is_train:
        ss_sentiment = StandardScaler()
        ss_stock = StandardScaler()
        ss_sentiment.fit(sentiment_df["logit"].values.reshape(-1, 1))
        ss_stock.fit(stock_df)

        joblib.dump(ss_sentiment, f'{path}_se.pkl')
        joblib.dump(ss_stock, f'{path}_st.pkl')
    else:
        ss_sentiment =joblib.load(f'{path}_se.pkl')
        ss_stock = joblib.load(f'{path}_st.pkl')

    sentiment_np = ss_sentiment.transform(sentiment_df["logit"].values.reshape(-1, 1))
    stock_np = ss_stock.transform(stock_df)
    y = data["target"].values

    return stock_np, sentiment_np, y


class myDataset(data.Dataset):
    def __init__(self, company, s_date, e_date, seq_size=5, is_train=False, split_sentiment=False):
        super(myDataset, self).__init__()

        self.seq_size = seq_size
        self.split_sentiment = split_sentiment

        path = r'./models/scaler/1'

        X_stock, X_sentiment, y = preprocessing(company, s_date, e_date, path, is_train)

        X_stock_tensor = torch.FloatTensor(X_stock)
        X_sentiment_tensor = torch.FloatTensor(X_sentiment)
        self.y = torch.FloatTensor(y)[self.seq_size:]

        if split_sentiment is False:
            self.X = torch.cat([X_stock_tensor, X_sentiment_tensor], axis=1)
        else:
            self.X = (X_stock_tensor, X_sentiment_tensor)

    def __getitem__(self, index):
        if self.split_sentiment is True:
            return self.X[0][index:index+self.seq_size], self.X[1][index:index+self.seq_size], \
                   self.y[index]

        return self.X[index:index+self.seq_size], self.y[index]

    def __len__(self):
        return self.y.shape[0] - self.seq_size


if __name__ == "__main__":
    ds = myDataset("현대차", date(2021, 1, 1), date(2022, 1, 1))