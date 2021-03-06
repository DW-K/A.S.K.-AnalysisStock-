import torch
import torch.nn as nn
import torch.nn.functional as F
from torch.utils.data import DataLoader
from torch.utils.data import Dataset
import numpy as np
from sklearn.metrics import r2_score
from sklearn.metrics import accuracy_score
from sklearn.metrics import r2_score

from preprocess import preprocessing

device = 'cuda' if torch.cuda.is_available() else 'cpu'

# for reproducibility
torch.manual_seed(777)
if device == 'cuda':
    torch.cuda.manual_seed_all(777)


def make_binary(out):
    if out > 0:
        out = 1
    else:
        out = 0

    return out


class stock_LSTM(nn.Module):
    def __init__(self, num_classes, input_size, hidden_size, num_layers, seq_length):
        super(stock_LSTM, self).__init__()
        self.num_classes = num_classes  # number of classes
        self.num_layers = num_layers  # number of layers
        self.input_size = input_size  # input size
        self.hidden_size = hidden_size  # hidden state
        self.seq_length = seq_length  # sequence length
        self.lstm = nn.LSTM(input_size=input_size, hidden_size=hidden_size, num_layers=num_layers,
                            batch_first=True)  # lstm
        self.fc_1 = nn.Linear(hidden_size, 256)  # fully connected 1
        self.fc = nn.Linear(256, num_classes)  # fully connected last layer
        self.relu = nn.ReLU()
        self.sigmoid = nn.Sigmoid()

    def forward(self, x):
        h_0 = torch.zeros(self.num_layers, x.size(0), self.hidden_size).to(device)  # hidden state
        c_0 = torch.zeros(self.num_layers, x.size(0), self.hidden_size).to(device)  # internal state
        # Propagate input through LSTM
        output, (hn, cn) = self.lstm(x, (h_0, c_0))  # lstm with input, hidden, and internal state
        hn = hn.view(-1, self.hidden_size)  # reshaping the data for Dense layer next
        out = self.relu(hn)
        out = self.fc_1(out)  # first Dense
        out = self.relu(out)  # relu
        out = self.fc(out)  # Final Output

        return out


def train(num_epochs=None, lr=None, input_size=None, hidden_size=None, num_layers=None, num_classes=None):
    X_train_scaled, X_test_scaled, y_train, y_test = preprocessing("car", "??????")

    X_train_tensors = torch.Tensor(X_train_scaled)
    y_train_tensors = torch.Tensor(y_train)

    X_test_tensors = torch.Tensor(X_test_scaled)
    y_test_tensors = torch.Tensor(y_test)

    X_train_tensors_final = torch.reshape(X_train_tensors, (X_train_tensors.shape[0], 1, X_train_tensors.shape[1]))
    X_test_tensors_final = torch.reshape(X_test_tensors, (X_test_tensors.shape[0], 1, X_test_tensors.shape[1]))

    lstm1 = stock_LSTM(num_classes, input_size, hidden_size, num_layers, X_train_tensors_final.shape[1]).to(device)

    loss_function = torch.nn.MSELoss()  # mean-squared error for regression
    optimizer = torch.optim.Adam(lstm1.parameters(), lr=lr)  # adam optimizer

    for epoch in range(num_epochs):
        outputs = lstm1.forward(X_train_tensors_final.to(device))
        optimizer.zero_grad()

        loss = loss_function(outputs, y_train_tensors.to(device))
        loss.backward()

        optimizer.step()

        if epoch % (1000 - 1) == 0:
            print("Epoch: %d / %d, loss: %1.5f" % (epoch, num_epochs, loss.item()))

    predict = lstm1(X_test_tensors_final.to(device))

    # print(predict)

    r2 = r2_score(y_test_tensors.cpu().detach().numpy(), predict.cpu().detach().numpy())

    for i in range(len(y_test_tensors)):
        y_test_tensors[i] = make_binary(y_test_tensors[i])
        predict[i] = make_binary(predict[i])

    acc = accuracy_score(y_test_tensors.cpu().detach().numpy(), predict.cpu().detach().numpy())

    print()
    print(f'r2 {r2}')
    print(f'acc {acc}')


if __name__ == "__main__":
    train(num_epochs=20000, lr=0.000008, input_size=21, hidden_size=128, num_layers=1, num_classes=1)
