import os

RESULT_PATH_STOCK = r'.\dataset\stockData'


def createFolder(directory):
    dirs = directory.split('\\')[1:]
    path = r"."
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
