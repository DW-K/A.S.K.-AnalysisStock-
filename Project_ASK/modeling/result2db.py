import pandas as pd
import torch
from torch.utils.data import DataLoader

from getData import get_data
from db.db import create_table_result, insert_table_result
from modeling.rnn_model import *
from modeling.preprocess import myDataset
from datetime import date, timedelta


def result2db(path, seq_size, company, s_date, e_date):
    state = torch.load(path)

    device = 'cuda' if torch.cuda.is_available() else 'cpu'

    model = lstm_ln_h8_m4(3, 6, 3, device)
    model.load_state_dict(state['model'])

    data = get_data(company, s_date - timedelta(days=5), e_date)

    stock_df = data["stock"]
    sentiment_df = data["sentiment"]
    y = data["target"]

    # stock_df = stock_df.set_index("날짜")
    # sentiment_df = sentiment_df.set_index("date")

    eps = 1e-10
    sentiment_df["logit"] = sentiment_df["positive"]/(sentiment_df["negative"] + eps)

    X_df = pd.concat([stock_df, sentiment_df["logit"]], axis=1)

    result_cols = ["date", "company", "result", "ans"]
    df = pd.DataFrame(columns=result_cols)
    indice = list(X_df.index)[seq_size:]
    for i in range(X_df.shape[0]-seq_size):
        X = torch.FloatTensor(X_df.iloc[i:i+seq_size, :].values)
        X = X.unsqueeze(0)
        output = model(X)
        temp = pd.DataFrame([[indice[i], company, output.item(), y.iloc[i+seq_size]]], columns=result_cols)

        df = pd.concat([df, temp], axis=0, ignore_index=True)

    print(df)
    df = df.drop(["ans"], axis=1)
    insert_table_result(df_result=df)


if __name__ == "__main__":
    create_table_result()
    path = r"./models/lstm_ln_h8_m4/sent_seq_5_2.0412148.pt"
    company = "LG전자"
    s_date = date(2022, 4, 20)
    e_date = date(2022, 4, 30)
    seq_size = 5

    result2db(path=path, company=company, seq_size=seq_size, s_date=s_date, e_date=e_date)

