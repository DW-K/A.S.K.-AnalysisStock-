import os

RESULT_PATH_NEWS = r'.\dataset\news'  # 결과 저장할 경로
RESULT_PATH_TWEET = r'.\dataset\tweets'
RESULT_PATH_STOCK = r'.\dataset\stockData'
RESULT_PATH_COMBINE = r'.\dataset\combine'
RESULT_PATH_FINANCE = r'.\dataset\finance'
RESULT_PATH_MODEL = r'.\dataset\models'

RESOURCE_PATH_STOCK_INFO = r'..\resource\stockInfo'  # stock 키워드 엑셀 파일 저장 경로
RESOURCE_PATH_CRAWLING_KEYWORD = r'..\resource\crawlKeyword'  # crawling keyword json 파일 저장 경로
RESOURCE_PATH_STOCK_CODE = r'..\resource\stockCode'  # stock code json 파일 저장 경로

RESOURCE_FILE_CRAWLING_KEYWORD = "crawlKeyword.json"
RESOURCE_FILE_STOCK_CODE = "stockCode.json"

RESOURCE_CRAWLING_KEYWORD = fr'{RESOURCE_PATH_CRAWLING_KEYWORD}\{RESOURCE_FILE_CRAWLING_KEYWORD}'
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
