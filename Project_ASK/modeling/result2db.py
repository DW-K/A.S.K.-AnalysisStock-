import os

import pandas as pd
import torch
from torch.utils.data import DataLoader

from getData import get_data
from db.db import create_table_result, insert_table_result
from modeling.rnn_model import *
from modeling.preprocess import myDataset
from datetime import date, timedelta


class predictor:
    def __init__(self, is_test=False):
        self.result_cols = ["date", "company", "result", "answer"]
        self.x_cols = ["price", "trade", "logit"]
        self.is_test = is_test

    def result2db(self, path, model_func, seq_size, company, s_date, e_date):
        device = 'cuda' if torch.cuda.is_available() else 'cpu'

        if self.is_test:
            df = pd.DataFrame(columns=self.result_cols + self.x_cols)
        else:
            df = pd.DataFrame(columns=self.result_cols)

        state = torch.load(path)
        model = model_func(3, 6, 3, device)
        model.load_state_dict(state['model'])

        while e_date >= s_date:
            temp = self.get_result(model, seq_size, company, s_date)
            df = pd.concat([df, temp], axis=0)
            s_date += timedelta(days=1)

        # self.df = self.df.drop['answer']
        # insert_table_result(df_result=df)
        print(df)
        del model
        return df

    def get_result(self, model, seq_size, company, e_date):
        s_date = e_date - timedelta(days=seq_size * 3)
        ds = myDataset(company, s_date, e_date, seq_size, is_train=False, get_dates=True)

        if ds.__len__() < seq_size:
            del ds
            s_date = e_date - timedelta(days=seq_size * 6)
            ds = myDataset(company, s_date, e_date, seq_size, is_train=False, get_dates=True)

        X, y, day = ds.__getitem__(ds.__len__()-1)
        X = X.unsqueeze(0)
        output = model(X)
        temp = pd.DataFrame([[day, company, output.item(), y.item()]], columns=self.result_cols)
        print(temp)
        if self.is_test:
            x_df = pd.DataFrame([[X.squeeze()[-1][0].item(), X.squeeze()[-1][1].item(), X.squeeze()[-1][2].item()]], columns=self.x_cols)
            temp = pd.concat([temp, x_df], axis=1)
        # print(temp)

        del ds
        return temp


if __name__ == "__main__":
    create_table_result()

    s_date = date(2022, 4, 1)
    e_date = date(2022, 5, 25)

    seq_size = 5

    company_list = ["현대차", "카카오", "하이브", "LG전자"]

    pdt = predictor()

    model_func = lstm_ln_h8_m4

    for company in company_list:
        path = fr"./models/{company}/lstm_ln_h8_m4"

        file_names = os.listdir(path)

        pdt.result2db(f'{path}/{file_names[0]}', model_func, seq_size, company, s_date, e_date)
