import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import axes3d

text = pd.read_csv('height_weight.txt', sep=' \n', engine='python')

height = text.iloc[:int(text.shape[0]/2)]
weight = text.iloc[int(text.shape[0]/2)+1:]

height = np.array(height).astype(float) / 1000
weight = np.array(weight).astype(float)
ones = np.ones(shape=(height.shape[0], 1), dtype=np.float_)

A = np.append(height.copy(), ones, axis=1)
# a = 1
# b = 1
y = weight.copy().T


def loss(a,b):
    x = np.array([a,b])
    mse = y - np.dot(A, x)
    loss = (mse ** 2).sum(axis=1)
    return loss


# print(loss(1000,1000))


a_array = np.arange(-50, 50, 0.1)
b_array = np.arange(-50, 50, 0.1)

x_v = []
loss_list = []

for a in a_array:
    for b in b_array:
        x_v.append((a**2 + b**2)**0.5)
        loss_list.append(loss(a,b))

x_v = np.array(x_v)
loss_v = np.array(loss_list)

print(loss_v.mean())

plt.plot(x_v, loss_v)
plt.show()

