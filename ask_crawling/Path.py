import os

RESULT_PATH_NEWS = r'.\dataset\news'  # 결과 저장할 경로
RESULT_PATH_TWEET = r'.\dataset\tweets'  # 결과 저장할 경로


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
