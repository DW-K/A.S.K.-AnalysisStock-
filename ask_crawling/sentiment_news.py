import sys

from crawling_news import crawler
from pororo import Pororo
import pandas as pd


def sentiment(filePath, sheetName):
    df = pd.read_excel(filePath, index_col="index", sheet_name=sheetName)

    sa = Pororo(task="sentiment", model="brainbert.base.ko.shopping", lang="ko")
    zsl = Pororo(task="zero-topic", lang="ko")

    categoryList = ["스포츠", "사회", "정치", "경제", "생활/문화", "IT/과학"]

    for i in df.index:
        # print(df.loc[i, "title"])
        sentimentResult = sa(df.loc[i, "title"], show_probs=True)
        categoryResult = zsl(df.loc[i, "title"], categoryList)

        df.loc[i, "positive"] = sentimentResult['positive']
        df.loc[i, "negative"] = sentimentResult['negative']

        for c in categoryResult:
            df.loc[i, c] = categoryResult[c]

    df.to_excel(filePath, sheet_name=sheetName)

    return filePath


if __name__ == "__main__":
    arg_list = sys.argv[1:]  # argument 받아서 실행
    category = arg_list[0]
    companyName = arg_list[1]
    maxpage = int(arg_list[2])
    query = arg_list[3]
    sort = arg_list[4]
    s_date = arg_list[5]
    e_date = arg_list[6]
    print(arg_list)
    # filePath = crawler(category="에스엠", maxpage=20, query="이수만", sort="0", s_date="20211024", e_date="20201025")

    filePath = crawler(category=category, companyName=companyName, maxpage=maxpage, query=query, sort=sort, s_date=s_date, e_date=e_date)
    # query = "이수만"
    filePath = sentiment(filePath=filePath, sheetName=query)

