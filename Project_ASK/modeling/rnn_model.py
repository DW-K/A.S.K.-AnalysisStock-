import torch
from torch import nn

torch.manual_seed(777)


class lstm_ln_h4_m1(nn.Module):
    def __init__(self, input_size, hidden_size, num_layers, device):
        super().__init__()
        self.input_size = input_size
        self.hidden_size = hidden_size
        self.num_layers = num_layers
        self.device = device

        self.ln = nn.LayerNorm(input_size, device=device)

        # (batch_size, sequence_size, input_size)
        # => (batch_size, hidden_size)
        self.lstm = nn.LSTM(input_size, hidden_size, num_layers, batch_first=True)

        # (batch_size, hidden_size)
        # => (batch_size, num_classes)
        i1 = nn.Linear(hidden_size, hidden_size * 2)
        i2 = nn.Linear(hidden_size * 2, hidden_size * 4)

        m_hidden = hidden_size * 4
        m1 = nn.Linear(m_hidden, m_hidden)

        d3 = nn.Linear(hidden_size * 4, hidden_size * 2)
        d2 = nn.Linear(hidden_size * 2, hidden_size)
        d1 = nn.Linear(hidden_size, 1)

        lRelu = nn.LeakyReLU()
        tanh = nn.Tanh()

        self.S1 = nn.Sequential(i1, lRelu, i2, lRelu
                                )

        self.S2 = nn.Sequential(m1, lRelu)

        self.S3 = nn.Sequential(d3, lRelu,
                                d2, lRelu, d1, tanh)

    def forward(self, x):
        # initialize first hidden state
        h0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)
        c0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)

        x = self.ln(x)
        # drop last hidden state
        out, _ = self.lstm(x, (h0, c0))
        out = self.S1(out)
        out = self.S2(out)
        out = self.S3(out)

        # get last sequence
        out = out[:, -1, :].squeeze()

        return out


class lstm_ln_h4_m2(nn.Module):
    def __init__(self, input_size, hidden_size, num_layers, device):
        super().__init__()
        self.input_size = input_size
        self.hidden_size = hidden_size
        self.num_layers = num_layers
        self.device = device

        self.ln = nn.LayerNorm(input_size, device=device)

        # (batch_size, sequence_size, input_size)
        # => (batch_size, hidden_size)
        self.lstm = nn.LSTM(input_size, hidden_size, num_layers, batch_first=True)

        # (batch_size, hidden_size)
        # => (batch_size, num_classes)
        i1 = nn.Linear(hidden_size, hidden_size * 2)
        i2 = nn.Linear(hidden_size * 2, hidden_size * 4)

        m_hidden = hidden_size * 4
        m1 = nn.Linear(m_hidden, m_hidden)
        m2 = nn.Linear(m_hidden, m_hidden)

        d3 = nn.Linear(hidden_size * 4, hidden_size * 2)
        d2 = nn.Linear(hidden_size * 2, hidden_size)
        d1 = nn.Linear(hidden_size, 1)

        lRelu = nn.LeakyReLU()
        tanh = nn.Tanh()

        self.S1 = nn.Sequential(i1, lRelu, i2, lRelu)

        self.S2 = nn.Sequential(m1, lRelu, m2, lRelu)

        self.S3 = nn.Sequential(d3, lRelu,
                                d2, lRelu, d1, tanh)

    def forward(self, x):
        # initialize first hidden state
        h0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)
        c0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)

        x = self.ln(x)
        # drop last hidden state
        out, _ = self.lstm(x, (h0, c0))
        out = self.S1(out)
        out = self.S2(out)
        out = self.S3(out)

        # get last sequence
        out = out[:, -1, :].squeeze()

        return out


class lstm_ln_h4_m4(nn.Module):
    def __init__(self, input_size, hidden_size, num_layers, device):
        super().__init__()
        self.input_size = input_size
        self.hidden_size = hidden_size
        self.num_layers = num_layers
        self.device = device

        self.ln = nn.LayerNorm(input_size, device=device)

        # (batch_size, sequence_size, input_size)
        # => (batch_size, hidden_size)
        self.lstm = nn.LSTM(input_size, hidden_size, num_layers, batch_first=True)

        # (batch_size, hidden_size)
        # => (batch_size, num_classes)
        i1 = nn.Linear(hidden_size, hidden_size * 2)
        i2 = nn.Linear(hidden_size * 2, hidden_size * 4)

        m_hidden = hidden_size * 4
        m1 = nn.Linear(m_hidden, m_hidden)
        m2 = nn.Linear(m_hidden, m_hidden)
        m3 = nn.Linear(m_hidden, m_hidden)
        m4 = nn.Linear(m_hidden, m_hidden)

        d3 = nn.Linear(hidden_size * 4, hidden_size * 2)
        d2 = nn.Linear(hidden_size * 2, hidden_size)
        d1 = nn.Linear(hidden_size, 1)

        lRelu = nn.LeakyReLU()
        tanh = nn.Tanh()

        self.S1 = nn.Sequential(i1, lRelu, i2, lRelu)

        self.S2 = nn.Sequential(m1, lRelu, m2, lRelu, m3, lRelu, m4, lRelu)

        self.S3 = nn.Sequential(d3, lRelu,
                                d2, lRelu, d1, tanh)

    def forward(self, x):
        # initialize first hidden state
        h0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)
        c0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)

        x = self.ln(x)
        # drop last hidden state
        out, _ = self.lstm(x, (h0, c0))
        out = self.S1(out)
        out = self.S2(out)
        out = self.S3(out)

        # get last sequence
        out = out[:, -1, :].squeeze()

        return out


class lstm_ln_h8_m1(nn.Module):
    def __init__(self, input_size, hidden_size, num_layers, device):
        super().__init__()
        self.input_size = input_size
        self.hidden_size = hidden_size
        self.num_layers = num_layers
        self.device = device

        self.ln = nn.LayerNorm(input_size, device=device)

        # (batch_size, sequence_size, input_size)
        # => (batch_size, hidden_size)
        self.lstm = nn.LSTM(input_size, hidden_size, num_layers, batch_first=True)

        # (batch_size, hidden_size)
        # => (batch_size, num_classes)
        i1 = nn.Linear(hidden_size, hidden_size * 2)
        i2 = nn.Linear(hidden_size * 2, hidden_size * 4)
        i3 = nn.Linear(hidden_size * 4, hidden_size * 8)

        m_hidden = hidden_size * 8
        m1 = nn.Linear(m_hidden, m_hidden)

        d4 = nn.Linear(hidden_size * 8, hidden_size * 4)
        d3 = nn.Linear(hidden_size * 4, hidden_size * 2)
        d2 = nn.Linear(hidden_size * 2, hidden_size)
        d1 = nn.Linear(hidden_size, 1)

        lRelu = nn.LeakyReLU()
        tanh = nn.Tanh()

        self.S1 = nn.Sequential(i1, lRelu, i2, lRelu, i3, lRelu
                                )

        self.S2 = nn.Sequential(m1, lRelu)

        self.S3 = nn.Sequential(d4, lRelu, d3, lRelu,
                                d2, lRelu, d1, tanh)

    def forward(self, x):
        # initialize first hidden state
        h0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)
        c0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)

        x = self.ln(x)
        # drop last hidden state
        out, _ = self.lstm(x, (h0, c0))
        out = self.S1(out)
        out = self.S2(out)
        out = self.S3(out)

        # get last sequence
        out = out[:, -1, :].squeeze()

        return out


class lstm_ln_h8_m2(nn.Module):
    def __init__(self, input_size, hidden_size, num_layers, device):
        super().__init__()
        self.input_size = input_size
        self.hidden_size = hidden_size
        self.num_layers = num_layers
        self.device = device

        self.ln = nn.LayerNorm(input_size, device=device)

        # (batch_size, sequence_size, input_size)
        # => (batch_size, hidden_size)
        self.lstm = nn.LSTM(input_size, hidden_size, num_layers, batch_first=True)

        # (batch_size, hidden_size)
        # => (batch_size, num_classes)
        i1 = nn.Linear(hidden_size, hidden_size * 2)
        i2 = nn.Linear(hidden_size * 2, hidden_size * 4)
        i3 = nn.Linear(hidden_size * 4, hidden_size * 8)

        m_hidden = hidden_size * 8
        m1 = nn.Linear(m_hidden, m_hidden)
        m2 = nn.Linear(m_hidden, m_hidden)

        d4 = nn.Linear(hidden_size * 8, hidden_size * 4)
        d3 = nn.Linear(hidden_size * 4, hidden_size * 2)
        d2 = nn.Linear(hidden_size * 2, hidden_size)
        d1 = nn.Linear(hidden_size, 1)

        lRelu = nn.LeakyReLU()
        tanh = nn.Tanh()

        self.S1 = nn.Sequential(i1, lRelu, i2, lRelu, i3, lRelu
                                )

        self.S2 = nn.Sequential(m1, lRelu, m2, lRelu)

        self.S3 = nn.Sequential(d4, lRelu, d3, lRelu,
                                d2, lRelu, d1, tanh)

    def forward(self, x):
        # initialize first hidden state
        h0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)
        c0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)

        x = self.ln(x)
        # drop last hidden state
        out, _ = self.lstm(x, (h0, c0))
        out = self.S1(out)
        out = self.S2(out)
        out = self.S3(out)

        # get last sequence
        out = out[:, -1, :].squeeze()

        return out


class lstm_ln_h8_m4(nn.Module):
    def __init__(self, input_size, hidden_size, num_layers, device):
        super().__init__()
        self.input_size = input_size
        self.hidden_size = hidden_size
        self.num_layers = num_layers
        self.device = device

        self.ln = nn.LayerNorm(input_size, device=device)

        # (batch_size, sequence_size, input_size)
        # => (batch_size, hidden_size)
        self.lstm = nn.LSTM(input_size, hidden_size, num_layers, batch_first=True)

        # (batch_size, hidden_size)
        # => (batch_size, num_classes)
        i1 = nn.Linear(hidden_size, hidden_size * 2)
        i2 = nn.Linear(hidden_size * 2, hidden_size * 4)
        i3 = nn.Linear(hidden_size * 4, hidden_size * 8)

        m_hidden = hidden_size * 8
        m1 = nn.Linear(m_hidden, m_hidden)
        m2 = nn.Linear(m_hidden, m_hidden)
        m3 = nn.Linear(m_hidden, m_hidden)
        m4 = nn.Linear(m_hidden, m_hidden)

        d4 = nn.Linear(hidden_size * 8, hidden_size * 4)
        d3 = nn.Linear(hidden_size * 4, hidden_size * 2)
        d2 = nn.Linear(hidden_size * 2, hidden_size)
        d1 = nn.Linear(hidden_size, 1)

        lRelu = nn.LeakyReLU()
        tanh = nn.Tanh()

        self.S1 = nn.Sequential(i1, lRelu, i2, lRelu, i3, lRelu
                                )

        self.S2 = nn.Sequential(m1, lRelu, m2, lRelu, m3, lRelu, m4, lRelu)

        self.S3 = nn.Sequential(d4, lRelu, d3, lRelu,
                                d2, lRelu, d1, tanh)

    def forward(self, x):
        # initialize first hidden state
        h0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)
        c0 = torch.zeros(self.num_layers, x.size()[0], self.hidden_size).to(self.device)

        x = self.ln(x)
        # drop last hidden state
        out, _ = self.lstm(x, (h0, c0))
        out = self.S1(out)
        out = self.S2(out)
        out = self.S3(out)

        # get last sequence
        out = out[:, -1, :].squeeze()

        return out