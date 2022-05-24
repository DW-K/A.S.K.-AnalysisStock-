import torch
import pandas as pd

path = '.'
model = torch.load(path+"/1.pt")

df = input['날짜', 'company']
input = input.drop(['날짜', 'company'])

for i in df.index:
    df.loc[i, :]['result'] = model(input.loc[i, :])
