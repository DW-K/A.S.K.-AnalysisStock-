import os

import pandas as pd

RESULT_PATH_NEWS = r'.\dataset\news'  # 결과 저장할 경로
RESULT_PATH_TWEET = r'.\dataset\tweets'  # 결과 저장할 경로


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


def writeToExcel(output_path, df, sheet_name, isWrite=True):
    if isWrite:
        with pd.ExcelWriter(output_path, mode='w', engine='openpyxl') as writer:
            df.to_excel(writer, index=True, index_label=df.index.name, sheet_name=sheet_name)
    else:
        with pd.ExcelWriter(output_path, mode='a', engine='openpyxl', if_sheet_exists="replace") as writer:
            df.to_excel(writer, index=True, index_label=df.index.name, sheet_name=sheet_name)
