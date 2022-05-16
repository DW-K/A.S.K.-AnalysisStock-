import time

from articlecrawler import ArticleCrawler
from datetime import date, timedelta
from dateutil.relativedelta import relativedelta


if __name__ == "__main__":
    Crawler = ArticleCrawler()
    Crawler.set_category("경제")

    s_date = date(2021, 5, 1)
    e_date = date(2022, 5, 1)
    date_format = "%Y-%m-%d"

    while s_date < e_date:
        # print(f's_date: {s_date.strftime(date_format)}, e_date: {(s_date + relativedelta(months=1)).strftime(date_format)}')
        Crawler.set_date_range(s_date.strftime(date_format), (s_date + relativedelta(months=6)).strftime(date_format))
        # Crawler.set_date_range(s_date.strftime(date_format), s_date.strftime(date_format))
        Crawler.start()
        s_date = s_date + relativedelta(months=6)
        # s_date = s_date + timedelta(days=1)
