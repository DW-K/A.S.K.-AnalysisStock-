import os
from datetime import date

import torch
import torch.nn as nn
from torch.utils.data import DataLoader

from modeling.preprocess import myDataset
from modeling.train import validation

from rnn_model import *
from gru_model import *


def test(path, model, test_dl, device):
    loss_func = nn.MSELoss()
    info = torch.load(path)

    try:
        model.load_state_dict(info["model"])
    except:
        os.remove(path)
        return

    print(f'{company}, {model_name}, {file_name}')

    test_loss, r2, f1, acc = validation(model, loss_func, test_dl, device)

    print(f'test_loss: {test_loss}, r2: {r2}, f1: {f1}, acc: {acc}')


if __name__ == "__main__":
    seq_size = 5
    device = "cuda" if torch.cuda.is_available() else "cpu"

    company_list = ["현대차"]

    for company in company_list:
        model_list = [gru_ln_h8_m2(3, 6, 3, device), gru_ln_h8_m4(3, 6, 3, device), lstm_ln_h8_m2(3, 6, 3, device),
                      lstm_ln_h8_m4(3, 6, 3, device),
                      rnn_ln_h8_m2(3, 6, 3, device), rnn_ln_h8_m4(3, 6, 3, device)]

        model_name_list = ["gru_ln_h8_m2", "gru_ln_h8_m4", "lstm_ln_h8_m2", "lstm_ln_h8_m4","rnn_ln_h8_m2",
                           "rnn_ln_h8_m4"]

        for model, model_name in zip(model_list, model_name_list):
            test_ds = myDataset(company, date(2022, 2, 1), date(2022, 4, 30), seq_size=seq_size)
            test_dl = DataLoader(test_ds, batch_size=8, shuffle=False)

            path = f"./models/{company}/{model_name}"

            file_names = os.listdir(path)

            for file_name in file_names:
                test(f"{path}/{file_name}", model, test_dl, device)
