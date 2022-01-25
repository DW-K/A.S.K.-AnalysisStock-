import torch
from torch.utils.data import Dataset, DataLoader

class customDataset(Dataset):
    def __init__(self, X, y):
        super(customDataset, self).__init__()
        self.X = X
        self.y = y

    def __len__(self):
        return len(self.X) - 1

    def __getitem__(self, idx):
        X_row = self.X.iloc[idx, :]
        y_row = self.y.iloc[idx, :]

        return X_row, y_row