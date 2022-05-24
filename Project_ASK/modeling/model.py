import torch
import torch.nn as nn


class RNNBaseModel(nn.Module):
    def __init__(self, rnn_type, input_size, hidden_size, output_size):
        super(RNNBaseModel, self).__init__()

        self.input_size = input_size
        self.hidden_size = hidden_size
        self.output_size = output_size

        self.layerNorm = nn.LayerNorm(input_size)

        self.rnn = getattr(nn, rnn_type)(input_size, hidden_size, num_layers=2,
                                         batch_first=True, dtype=torch.float32)

        self.out = nn.Linear(hidden_size*2, output_size, dtype=torch.float32)

    def forward(self, inputs, hidden=None):
        inputs = self.layerNorm(inputs)
        output, hidden = self.rnn(inputs, hidden)
        output = self.out(output)
        return output, hidden