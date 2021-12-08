from datetime import datetime, timedelta

from subProcess_stock import getStockPrice
from subProcess_crawl import crawling_news

from crawlJson import readJson
import Path

jsonCrawlFilePath = Path.RESOURCE_PATH_CRAWLING_KEYWORD
jsonCrawlFileName = Path.RESOURCE_FILE_CRAWLING_KEYWORD
jsonCrawl = Path.RESOURCE_CRAWLING_KEYWORD

jsonStockFilePath = Path.RESOURCE_PATH_STOCK_CODE
jsonStockFileName = Path.RESOURCE_FILE_STOCK_CODE
jsonStock = Path.RESOURCE_STOCK_CODE

dateFormat = "%Y%m%d"
now = datetime.now()
today = now.strftime(dateFormat)

yesterday = now - timedelta(days=1)
yesterday = yesterday.strftime(dateFormat)


def crawlingByKeyword(target_date=today):
    crawlDict = readJson(filePath=jsonCrawlFilePath, fileName=jsonCrawlFileName)
    for category in crawlDict.keys():
        for companyName in crawlDict[category]:
            for crawlKeyword in crawlDict[category][companyName]:
                print(f'company: {companyName}')
                crawling_news(category=category, companyName=companyName, maxpage=str(5), query=crawlKeyword, sort="0",
                              s_date=target_date, e_date=target_date, isSentiment=True)


def crawlingStock():
    stockCodeDict = readJson(filePath=jsonStockFilePath, fileName=jsonStockFileName)

    for category in stockCodeDict.keys():
        for companyName, stockCode in stockCodeDict[category].items():
            print(f'company: {companyName}')
            getStockPrice(category=category, companyName=companyName, stockCode=stockCode)


if __name__ == "__main__":
    crawlingStock()
    # crawlingByKeyword()
    #
    # for i in range(300):
    #     print(f'date : {i}')
    #     target_date_format = now - timedelta(days=i)
    #     target_date = target_date_format.strftime(dateFormat)
    #     crawlingByKeyword(target_date)
