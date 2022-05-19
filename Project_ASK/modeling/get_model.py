import torch
import pandas as pd

from ..db.db import read_table_stock

path = '.'
model = torch.load(path+"/1.pt")

input = read_table_stock

df = input['날짜', 'company']
input = input.drop(['날짜', 'company'])

for i in df.index:
    df.loc[i, :]['result'] = model(input.loc[i, :])