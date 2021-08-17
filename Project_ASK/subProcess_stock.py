import subprocess
import json
import os


def subProcess_stock(func, arg=[]):
    path = r'..\resource\environ_stock.json'

    with open(path, 'r') as f:
        json_data = json.load(f)
        env = json_data['env']
        interpreter = json_data['interpreter']

    kwargs = {"stdin": subprocess.PIPE, "stdout": subprocess.PIPE, "env": env}
    with subprocess.Popen([interpreter, fr'..\project_ask_32\{func}.py'] + arg, **kwargs,
                          shell=True) as proc:

        out, err = proc.communicate()
        if err is not None:
            print(err)

        print(out.decode())

    # return out.decode()


def getStockPrice(date=None):
    if date is None:
        subProcess_stock('getStockPrice')
    else:
        subProcess_stock('getStockPrice', arg=date)
    print("complete crawling stock")


if __name__ == "__main__":
    # getStockPrice()
    subProcess_stock('makeUpdown', ['210817'])