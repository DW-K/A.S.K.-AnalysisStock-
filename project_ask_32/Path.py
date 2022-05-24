import os

RESULT_PATH_STOCK = r'.\dataset\stockData'

RESOURCE_PATH_STOCK_CODE = r'..\resource\stockCode'  # stock code json 파일 저장 경로
RESOURCE_FILE_STOCK_CODE = "stockCode.json"
RESOURCE_STOCK_CODE = fr'{RESOURCE_PATH_STOCK_CODE}\{RESOURCE_FILE_STOCK_CODE}'


def createFolder(directory):
    dirs = directory.split('\\')
    path = dirs[0]
    dirs = dirs[1:]
    for d in dirs:
        path += '/' + d
        try:
            if not os.path.exists(path):
                os.makedirs(path)
        except OSError:
            print('Error: Creating directory. ' + path)


def getFolderList(directory):
    fileList = os.listdir(directory)
    return fileList
