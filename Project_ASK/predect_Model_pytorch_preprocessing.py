import torch
import torchvision
import torchvision.transforms as tr
from torch.utils.data import DataLoader
from torch.utils.data import Dataset
import numpy as np
import pandas as pd

fileName = 'result'
df = pd.read_excel(rf'.\dataset\combine\{fileName}.xlsx')


class cbDataset(Dataset):

    def __init__(self, x_data, y_data, transform=None):
        self.x_data = x_data
        self.y_data = y_data
        self.transform = transform
        self.len = len(y_data)

    def __getitem__(self, index):
        sample = self.x_data[index], self.y_data[index]

        if self.transform:
            sample = self.transform(sample)

        return sample

    def __len__(self):
        return self.len


class ToTensor:
    def __call__(self, sample):
        inputs, labels = sample
        inputs = torch.FloatTensor(inputs)
        return inputs, torch.LongTensor(labels)


class LinearTensor:

    def __init__(self, slope=1, bias=0):
        self.slope = slope
        self.bias = bias

    def __call__(self, sample):
        inputs, labels = sample
        inputs = self.slope * inputs + self.bias

        return inputs, labels


if __name__ == "__main__":
    df = df.fillna(method='bfill')  # 결측치 전날 값으로 채우기
    X = df[df.columns.difference(['isUp'])].to_numpy()
    y = df['isUp'].to_numpy()

    trans = tr.Compose([ToTensor(), LinearTensor(2, 5)])
    ds = cbDataset(X, y, transform=trans)
    train_loader = DataLoader(ds, batch_size=10, shuffle=True)

    dataiter = iter(train_loader)
    row, labels = dataiter.next()

    print(row)