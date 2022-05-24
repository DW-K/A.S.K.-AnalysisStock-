from ask_crawling.crawling_news import crawler
from ask_crawling.sentimentNews import sentiment
from datetime import datetime

if __name__ == "__main__":
    print(f'start news crawling : {datetime.now()}')
    path = crawler("3000", "YG엔터", "0", "20140101", "20210810")
    sentiment(path)
    print(f'complete news crawling : {datetime.now()}')