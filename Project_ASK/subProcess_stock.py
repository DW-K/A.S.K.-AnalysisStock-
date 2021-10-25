import subprocess
import json


def subProcess_stock(func, arg=[]):
    path = r'..\resource\environ_stock.json'

    with open(path, 'r') as f:
        json_data = json.load(f)
        env = json_data['env']
        interpreter = json_data['interpreter']

    kwargs = {"stdin": subprocess.PIPE, "stdout": subprocess.PIPE, "env": env}
    with subprocess.Popen([interpreter, fr'..\project_ask_32\{func}.py'] + arg, **kwargs, bufsize=-1,
                          shell=True) as proc:
        out, err = proc.communicate()
        if err is not None:
            print(err)

    # arg_string = json.dumps(arg[0]).encode('utf-8')
    # arg_string += b"!" + bytes(arg[1], encoding='utf-8')
    # arg_string += b"!" + bytes(arg[2], encoding='utf-8')
    #
    # kwargs = {"stdin": subprocess.PIPE, "stdout": subprocess.PIPE, "env": env}
    # with subprocess.Popen(fr'{interpreter} ..\project_ask_32\{func}.py', **kwargs, bufsize=-1,
    #                       shell=True) as proc:
    #
    #     out, err = proc.communicate(input=arg_string, timeout=None)
    #     if err is not None:
    #         print(err)

    print(out.decode())

    # return out.decode()


def getStockPrice(date=None):
    if date is None:
        subProcess_stock('getStockPrice')
    else:
        subProcess_stock('getStockPrice', arg=date)
    print("complete crawling stock")


# def getStockCode(args):
#     subProcess_stock('getStockCode', arg=args)


def getStockCode(stockDict, filePath, fileName):
    subProcess_stock('getStockCode', arg=[stockDict, filePath, fileName])


def getStockCode_temp(filePath, fileName):
    subProcess_stock('getStockCode', arg=[filePath, fileName])


if __name__ == "__main__":
    pass
