import os
import sys

import Path
from pororo import Pororo
import pandas as pd


def sentiment(filePath, output_file_name, sheetName, target_col):
    output_path = fr'{filePath}\{output_file_name}'
    df = pd.read_excel(output_path, index_col="index", sheet_name=sheetName)

    sa = Pororo(task="sentiment", model="brainbert.base.ko.shopping", lang="ko")
    zsl = Pororo(task="zero-topic", lang="ko")

    categoryList = ["스포츠", "사회", "정치", "경제", "생활/문화", "IT/과학"]

    for i in df.index:
        # print(df.loc[i, target_col])
        sentimentResult = sa(df.loc[i, target_col], show_probs=True)
        categoryResult = zsl(df.loc[i, target_col], categoryList)

        df.loc[i, "positive"] = sentimentResult['positive']
        df.loc[i, "negative"] = sentimentResult['negative']

        for c in categoryResult:
            df.loc[i, c] = categoryResult[c]

    print(filePath)
    sentiment_file_path = "\\".join(filePath.split('\\')[:-1])
    sentiment_file_path += "\\sentiment"
    print(sentiment_file_path)

    sentiment_output = output_file_name.split('.')[0]   # ex) 현대차_20211203_n
    sentiment_output += 's.xlsx'    # ex) 현대차_20211203_ns.xlsx

    sentiment_output_path = fr'{sentiment_file_path}\{sentiment_output}'

    Path.createFolder(sentiment_file_path)
    if os.path.exists(sentiment_output_path):
        with pd.ExcelWriter(sentiment_output_path, mode='a', engine='openpyxl', if_sheet_exists="replace") as writer:
            df.to_excel(writer, index_label=df.index.name, sheet_name=query)
    else:
        with pd.ExcelWriter(sentiment_output_path, mode='w', engine='openpyxl') as writer:
            df.to_excel(writer, index_label=df.index.name, sheet_name=query)

    return sentiment_file_path, sentiment_output


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

    # filePath, output_file_name = crawler(category=category, companyName=companyName, maxpage=maxpage, query=query, sort=sort, s_date=s_date, e_date=e_date)
    # query = "이수만"
    # filePath, output_file_name = sentiment(filePath=filePath, output_file_name=output_file_name, sheetName=query)

