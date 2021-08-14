import sys

from ask_crawling.crawling_news import crawler
from pororo import Pororo
import pandas as pd


def sentiment(filePath):
    df = pd.read_excel(filePath, index_col="index")

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

    df.to_excel(filePath, sheet_name='sheet1')

    return filePath


if __name__ == "__main__":
    arg_list = sys.argv[1:]
    maxpage = int(arg_list[0])
    query = arg_list[1]
    sort = arg_list[2]
    s_date = arg_list[3]
    e_date = arg_list[4]
    # crawler(20, "YG엔터", "0", "2021.06.01", "2021.08.00")
    filePath = crawler(arg_list[0], arg_list[1], arg_list[2], arg_list[3], arg_list[4])
    filePath = sentiment(filePath=filePath)

