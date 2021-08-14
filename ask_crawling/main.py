from ask_crawling.crawling_news import crawler
from ask_crawling.sentimentNews import sentiment
from datetime import datetime
import time
if __name__ == "__main__":
    print(datetime.now())
    path = crawler("3000", "YG엔터", "0", "20140101", "20210810")
    sentiment(path)
    print(datetime.now())