import random

from getDataset import preprocess, myDataset
import pandas as pd
import torch
from torch import nn, optim
import torch.nn.functional as F
from torch.utils.data import DataLoader
from sklearn.metrics import accuracy_score


# rnn_type='LSTM'
# lstm = getattr(nn, rnn_type)(60, 20, 1, batch_first=True, dropout=0.2, bidirectional=1)

class RNNmodel(nn.Module):
    def __init__(self, input_size, hidden_size, output_size, batch_size=5):
        super(RNNmodel, self).__init__()

        self.input_size = input_size
        self.hidden_size = hidden_size
        self.output_size = output_size
        self.batch_size = batch_size

        self.rnn = nn.RNN(input_size=input_size, hidden_size=hidden_size,
                          num_layers=1, batch_first=True, dtype=torch.float64)
        self.out = nn.Linear(hidden_size, output_size, dtype=torch.float64)

    def step(self, input, hidden=None):
        output, hidden = self.rnn(input, hidden)
        output = self.out(output)
        return output, hidden

    def forward(self, inputs, steps, hidden=None):
        force = random.random() < 0.5

        if force or steps == 0:
            steps = inputs.shape[1]

        outputs = torch.zeros(steps)
        # for i in range(steps):
        #     if force or i == 0:
        #         input = inputs
        #         print(input.shape)
        #     else:
        #         input = output
        # print(inputs.shape)
        output, hidden = self.step(inputs, hidden)
        # outputs[i] = output
        # print(output.shape)
        return output, hidden


def fit(df):
    train_X, train_y, test_X, test_y = preprocess(df, get_test=True, train_bound_e=0, train_bound_s=0)
    train_ds = myDataset(train_X, train_y)
    test_ds = myDataset(test_X, test_y)

    model = RNNmodel(train_ds[0][0].shape[1], 16, 1)
    loss_func = nn.MSELoss()
    optimizer = optim.SGD(model.parameters(), lr=0.01)

    epochs = 200

    for epoch in range(epochs):
        model.train()
        for X, y in train_ds:
            X = X.unsqueeze(0)
            y = y.unsqueeze(0)
            # print(X.shape)
            # print(y.shape)
            outputs, hidden = model(X, X.shape[0], None)

            optimizer.zero_grad()
            loss = loss_func(outputs.squeeze(2), y)
            loss.backward()
            optimizer.step()

        if epoch % 10 == 0:
            model.eval()
            for test_X, test_y in test_ds:
                test_X = test_X.unsqueeze(0)
                test_y = test_y.unsqueeze(0)
                outputs, hidden = model(test_X, test_X.shape[0], None)

                loss = loss_func(outputs.squeeze(2), test_y)
                acc = 0
                print(f'{epoch}/{epochs}: loss: {loss}, acc: {acc}')

        # model.eval()
        # for X, y in test_dl:



if __name__ == "__main__":
    df = pd.read_excel(r"../dataset/stockData/car/기아/기아_s.xlsx")

    fit(df)