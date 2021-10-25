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

if __name__ == "__main__":
    dateFormat = "%Y%m%d"
    now = datetime.now()
    today = now.strftime(dateFormat)

    yesterday = now - timedelta(days=1)
    yesterday = yesterday.strftime(dateFormat)

    # crawling_news("30", "YG엔터", "0", "20140101", today, True)    # 1시간 20분
    # getStockPrice()     #4분

    crawlDict = readJson(filePath=jsonCrawlFilePath, fileName=jsonCrawlFileName)
    count = 0
    for category in crawlDict.keys():
        for companyName in crawlDict[category]:
            for crawlKeyword in crawlDict[category][companyName]:
                if count > 10:
                    break
                crawling_news("5", crawlKeyword, "0", yesterday, today, True)    # 1시간 20분
                count += 1


    # crawling_news("30", "YG엔터", "0", "20140101", today, True)    # 1시간 20분
    # getStockPrice()     #4분
