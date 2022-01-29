import random
import time

from getDataset import preprocess, myDataset, myDatasetSameSize
import pandas as pd
import torch
from torch import nn, optim
import torch.nn.functional as F
from torch.utils.data import DataLoader
from sklearn.metrics import accuracy_score


# rnn_type='LSTM'
# lstm = getattr(nn, rnn_type)(60, 20, 1, batch_first=True, dropout=0.2, bidirectional=1)

class GRUmodel(nn.Module):
    def __init__(self, input_size, hidden_size, output_size):
        super(GRUmodel, self).__init__()

        self.input_size = input_size
        self.hidden_size = hidden_size
        self.output_size = output_size

        self.rnn = nn.GRU(input_size=input_size, hidden_size=hidden_size,
                          num_layers=1, batch_first=True, dtype=torch.float64)
        self.out = nn.Linear(hidden_size, output_size, dtype=torch.float64)

    def forward(self, inputs, hidden=None):
        output, hidden = self.rnn(inputs, hidden)
        output = self.out(output)
        return output, hidden


class RNNBaseModel(nn.Module):
    def __init__(self, rnn_type, input_size, hidden_size, output_size):
        super(RNNBaseModel, self).__init__()

        self.input_size = input_size
        self.hidden_size = hidden_size
        self.output_size = output_size

        self.rnn = getattr(nn, rnn_type)(input_size, hidden_size, num_layers=1,
                                         batch_first=True, dtype=torch.float64, bidirectional=True)

        self.out = nn.Linear(hidden_size*2, output_size, dtype=torch.float64)

    def forward(self, inputs, hidden=None):
        output, hidden = self.rnn(inputs, hidden)
        output = self.out(output)
        return output, hidden


def fit(df, rnn_type):
    start = time.time()
    train_X, train_y, test_X, test_y = preprocess(df, get_test=True, train_bound_e=0, train_bound_s=0)

    train_ds = myDatasetSameSize(train_X, train_y)
    test_ds = myDatasetSameSize(test_X, test_y)

    # train_ds = myDataset(train_X, train_y)
    # test_ds = myDataset(test_X, test_y)

    bs = 5

    train_dl = DataLoader(train_ds, batch_size=bs, shuffle=False)
    test_dl = DataLoader(test_ds, batch_size=bs, shuffle=False)

    # model = RNNBaseModel(train_ds[0][0].shape[1], 16, 1)
    model = RNNBaseModel(rnn_type, train_ds[0][0].shape[1], 16, 1)
    loss_func = nn.MSELoss()
    optimizer = optim.SGD(model.parameters(), lr=0.01)

    epochs = 200

    for epoch in range(epochs):
        model.train()
        for X, y in train_dl:
            # print(X.shape)
            # X = X.unsqueeze(0)
            # print(X.shape)
            # print(y.shape)
            outputs, hidden = model(X, None)
            outputs = outputs.squeeze(2)
            # print(outputs.shape)
            # print(hidden.shape)
            # print(y.shape)

            optimizer.zero_grad()
            loss = loss_func(outputs, y)
            loss.backward()
            optimizer.step()

        if (epoch + 1) % 10 == 0:
            model.eval()
            with torch.no_grad():
                acc = torch.zeros(1)
                for test_X, test_y in test_dl:
                    # test_X = test_X.unsqueeze(0)
                    # print(test_X.shape)
                    outputs, hidden = model(test_X, None)
                    outputs = outputs.squeeze(2)

                    # print(outputs.shape)
                    # print(test_y.shape)

                    loss = loss_func(outputs, test_y)

                    acc += (outputs * test_y > 0).sum() / test_y.shape[1]

                    test_size = len(test_ds)
                    acc = acc / test_size

                    print(f'{epoch + 1}/{epochs}: loss: {loss}, acc: {acc.item()}, test size: {test_size}')

    print(f'Run time: {time.time() - start}')


if __name__ == "__main__":
    df = pd.read_excel(r"../dataset/stockData/car/기아/기아_s.xlsx")

    rnn_type_list = ["RNN", "LSTM", "GRU"]

    for rnn_type in rnn_type_list:
        fit(df, rnn_type)
