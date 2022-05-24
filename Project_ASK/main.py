from datetime import datetime, timedelta

from subProcess_stock import getStockPrice
from subProcess_crawl import crawling_news, crawling_tweet, integrate_word_count

from crawlJson import readJson
import Path
import schedule
import time

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


def newsCrawlingByKeyword(target_date=today):
    crawlDict = readJson(filePath=jsonCrawlFilePath, fileName=jsonCrawlFileName)
    for category in crawlDict.keys():
        for companyName in crawlDict[category]:
            for crawlKeyword in crawlDict[category][companyName]:
                crawling_news(category=category, companyName=companyName, maxpage=str(5), query=crawlKeyword, sort="0",
                              s_date=target_date, e_date=target_date)
                # keywordToFirestore(category=category, companyName=companyName, target_date=target_date)
                # pass
            integrate_word_count(category=category, companyName=companyName, target_date=target_date)


def twitterCrawlingByKeyword(target_date=today):
    crawlDict = readJson(filePath=jsonCrawlFilePath, fileName=jsonCrawlFileName)
    for category in crawlDict.keys():
        for companyName in crawlDict[category]:
            for crawlKeyword in crawlDict[category][companyName]:
                # print(f'company: {companyName}')
                crawling_tweet(category=category, companyName=companyName, maxpage=str(5), query=crawlKeyword,
                              s_date=target_date, e_date=target_date)


def crawlingStock():
    stockCodeDict = readJson(filePath=jsonStockFilePath, fileName=jsonStockFileName)

    for category in stockCodeDict.keys():
        for companyName, stockCode in stockCodeDict[category].items():
            # print(f'company: {companyName}')
            getStockPrice(category=category, companyName=companyName, stockCode=stockCode)


def start_crawl():
    newsCrawlingByKeyword()
    twitterCrawlingByKeyword()
    crawlingStock()
    train()


if __name__ == "__main__":
    schedule.every().day.at("23:59").do(start_crawl)

    while True:
        schedule.run_pending()
        time.sleep(60)



    # crawlingStock()
    # newsCrawlingByKeyword()
    # twitterCrawlingByKeyword()

    # date_count = 300
    # for i in range(0, date_count, 1):
    #     print(f'{i}/{date_count}')
    #     target_date_format = now - timedelta(days=i) - timedelta(days=360)
    #     target_date = target_date_format.strftime(dateFormat)
    #     # print(target_date)
    #     newsCrawlingByKeyword(target_date)

    # date_count = 6
    # for i in range(0, date_count, 1):
    #     target_date_format = now - timedelta(days=date_count - i)
    #     target_date = target_date_format.strftime(dateFormat)
    #     twitterCrawlingByKeyword(target_date)
