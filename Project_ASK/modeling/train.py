from sklearn.metrics import r2_score
from torch.utils.data import DataLoader

from preprocess import myDataset
from datetime import date
from rnn_model import *
from gru_model import *
import torch
import torch.nn as nn
from torch.optim import Adam, lr_scheduler
import os
from torch.utils.tensorboard import SummaryWriter
from sklearn.metrics import precision_score, recall_score, f1_score


def mk_dir(path):
    if not os.path.exists(path):
        os.mkdir(path)


class info_manager(object):
    def __init__(self, model_name, company, non_sentiment, seq_size, keep_num=5, allow_increase=5):
        self.info_list = []
        self.keep_num = keep_num
        # self.last_train_loss = 1e+10
        self.last_val_loss = 1e+10
        self.train_increase_count = 0
        self.val_increase_count = 0
        self.allow_increase = allow_increase

        path = "./models"
        mk_dir(path)

        path = f"./models/{company}"
        mk_dir(path)

        dir_path = path + f'/{model_name}'
        mk_dir(dir_path)

        self.dir_path = dir_path
        if not non_sentiment:
            self.base_file_name = f"sent_seq_{seq_size}"
        else:
            self.base_file_name = f"seq_{seq_size}"

    def add_info(self, info):
        # if info['train_loss'] > self.last_train_loss:
        #     self.train_increase_count += 1
        # else:
        #     self.last_train_loss = info['train_loss']
        #     self.train_increase_count = 0

        if info['loss'] >= self.last_val_loss and info['epoch'] > 100:
            self.val_increase_count += 1
        else:
            self.val_increase_count = 0
        self.last_val_loss = info['loss']

        # if self.train_increase_count > self.allow_increase:
        #     print(f"train loss increase {self.train_increase_count} times")
        #     return False

        if self.val_increase_count > self.allow_increase:
            print(f"val loss increase {self.val_increase_count} times")
            return False

        info['path'] = f'{self.dir_path}/{self.base_file_name}_{info["loss"]:.7f}.pt'
        self.info_list.append(info)
        self.info_list.sort(key=lambda x: x["f1"])

        if len(self.info_list) > self.keep_num:
            del self.info_list[-1]

        return True

    def get_info(self):
        print("get info: ")
        for l in self.info_list:
            print(l['description'])
        return self.info_list

    def save_model(self):
        for info in self.info_list:
            torch.save(info, info['path'])


def train(model, opt, loss_func, schedular, train_dl, val_dl, test_dl, epochs, allow_increase, device, model_name, seq_size, non_sentiment, company):
    model.to(device)
    im = info_manager(model_name, company, non_sentiment, seq_size, keep_num=5, allow_increase=allow_increase)

    writer = SummaryWriter()

    early_stop_flag = False

    for epoch in range(epochs):
        model.train()
        loss_per_epcoh = 0
        for X, y in train_dl:
            X = X.to(device)
            y = y.to(device)

            opt.zero_grad()

            output = model(X)
            loss = loss_func(output, y)

            loss.backward()
            opt.step()

            loss_per_epcoh += loss.item()

            writer.add_graph(model, X)

        if epoch % 10 == 0:
            val_loss, r2, f1, acc = validation(model, loss_func, val_dl, device)
            train_loss = loss_per_epcoh / train_dl.__len__()

            writer.add_scalar(f"{model_name}_seq {seq_size}: Loss/train", train_loss, epoch)
            writer.add_scalar(f"{model_name}_seq {seq_size}: Loss/val", val_loss, epoch)
            writer.add_scalar(f"{model_name}_seq {seq_size}: r2/val", r2, epoch)
            writer.add_scalar(f"{model_name}_seq {seq_size}: f1/val", f1, epoch)
            writer.add_scalar(f"{model_name}_seq {seq_size}: acc/val", acc, epoch)

            print(f'epoch: {epoch}, train loss: {train_loss}, val loss: {val_loss}, r2: {r2}, f1: {f1}, acc: {acc}')

            description = f"epoch_{epoch}__seq_{seq_size}__{model_name}"

            info = {"loss": val_loss, "train_loss": train_loss, "model": model.state_dict(),
                    "optim": opt.state_dict(), "description": description, "f1": f1, "epoch": epoch}
            if not im.add_info(info):
                print(f'early stopping, epoch: {epoch}, seq: {seq_size}, model_name: {model_name}')
                early_stop_flag = True
                break

        if early_stop_flag is True:
            break

        schedular.step()

    info_list = im.get_info()

    for info in info_list:
        model.load_state_dict(info['model'])
        test_loss, r2, f1, acc = validation(model, loss_func, test_dl, device)
        print(info['path'].split('/')[-1])
        print(f'test_loss: {test_loss}, r2: {r2}, f1: {f1}, acc: {acc}')

    writer.close()
    im.save_model()


def validation(model, loss_func, val_dl, device):
    with torch.no_grad():
        model.eval()
        val_loss = 0

        output_list = []
        y_list = []

        for X, y in val_dl:
            X = X.to(device)
            y = y.to(device)

            output = model(X)

            loss = loss_func(output, y)

            # output_pm = [1 if i >= 0 else 0 for i in output.cpu()]
            # y_pm = [1 if i >= 0 else 0 for i in y.cpu()]
            #
            # for a, b in zip(output_pm, y_pm):
            #     acc += 1 if a == b else 0
            #
            # r2 += r2_score(y.cpu(), output.cpu())
            for out in output:
                output_list.append(out)

            for t in y:
                y_list.append(t)

            val_loss += loss.item()

        val_loss /= val_dl.__len__()
        # r2 /= val_dl.__len__()
        # acc /= val_dl.__len__() * X.shape[0]
        out_pm_list = [1 if pm >= 0 else 0 for pm in output_list]
        y_pm_list = [1 if pm >= 0 else 0 for pm in y_list]
        r2 = r2_score(y_list, output_list)
        f1 = f1_score(y_pm_list, out_pm_list)
        acc = sum([1 if a == b else 0 for a, b in zip(out_pm_list, y_pm_list)])/len(out_pm_list)

    return val_loss, r2, f1, acc


def main1(model, model_name, company):
    seq_size = 5
    train_ds = myDataset(company, date(2021, 1, 1), date(2021, 10, 31), seq_size=seq_size, is_train=True)
    val_ds = myDataset(company, date(2021, 11, 1), date(2022, 1, 31), seq_size=seq_size)
    test_ds = myDataset(company, date(2022, 2, 1), date(2022, 4, 30), seq_size=seq_size)

    train_dl = DataLoader(train_ds, batch_size=8, shuffle=False)
    val_dl = DataLoader(val_ds, batch_size=8, shuffle=False)
    test_dl = DataLoader(test_ds, batch_size=8, shuffle=False)

    device = "cuda" if torch.cuda.is_available() else 'cpu'

    # model = rnn_ln_h8_m4(3, 6, 3, device)

    lr = 1e-3

    opt = Adam(model.parameters(), lr=lr)

    loss_func = nn.MSELoss()

    epochs = 10000

    scheduler = lr_scheduler.LambdaLR(optimizer=opt,
                                      lr_lambda=lambda epoch: max(0.999 ** epoch, lr/50),
                                      last_epoch=-1)

    train(model=model, opt=opt, loss_func=loss_func, schedular=scheduler, train_dl=train_dl,
          val_dl=val_dl, test_dl=test_dl, epochs=epochs, allow_increase=3, device=device, model_name=model_name,
          seq_size=seq_size, non_sentiment=False, company=company)


def get_h8():
    model_list = [gru_ln_h8_m2(3, 6, 3, device), gru_ln_h8_m4(3, 6, 3, device), lstm_ln_h8_m2(3, 6, 3, device),
                  lstm_ln_h8_m4(3, 6, 3, device),
                  rnn_ln_h8_m2(3, 6, 3, device), rnn_ln_h8_m4(3, 6, 3, device)]

    model_name_list = ["gru_ln_h8_m2", "gru_ln_h8_m4", "lstm_ln_h8_m2", "lstm_ln_h8_m4", "rnn_ln_h8_m2", "rnn_ln_h8_m4"]

    return model_list, model_name_list


if __name__ == "__main__":
    device = "cuda" if torch.cuda.is_available() else "cpu"

    company_list = ["현대차", "하이브", "카카오", "LG전자"]

    for company in company_list:
        model_list, model_name_list = get_h8()

        for model, model_name in zip(model_list, model_name_list):
            main1(model, model_name, company)
