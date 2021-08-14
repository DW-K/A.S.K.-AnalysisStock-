import datetime

from subProcess_stock import getStockPrice
from subProcess_crawl import crawling_news

if __name__ == "__main__":
    now = datetime.datetime.now()
    today = now.strftime("%Y%m%d")
    # crawling_news("3000", "YG엔터", "0", "20140101", today, True)    # 1시간 20분
    getStockPrice()     #4분
    pass
