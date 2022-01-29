import os

import numpy as np
import pandas as pd

import Path
from Path import writeToExcel

from collections import Counter
from konlpy.tag import Hannanum
from konlpy.tag import Kkma
from konlpy.tag import Komoran


def make_word_count(filePath, output_file_name, sheetName, target_col):
    output_path = fr'{filePath}\{output_file_name}'

    df = pd.read_excel(output_path, index_col="index", sheet_name=sheetName)

    han = Kkma()
    counter = {}
    count_df = pd.DataFrame()
    count_df_col = ["keyword", "title", "link", "sentiment_logit"]

    Path.createFolder(filePath)

    row_count = 0
    for i in df.index:
        # print(df.loc[i, target_col])
        if df.loc[i, target_col] is not np.NAN:
            nounResult = han.nouns(df.loc[i, target_col])
            countResult = Counter(nounResult)

            for k, v in countResult.items():
                # print(f'{k}, {sheetName}   {k in sheetName}')
                if len(k) > 1 and k not in sheetName and not k.isdigit():
                    if k not in counter.keys():
                        counter[k] = 0
                    # list_row = []
                    counter[k] += v

                    count_df.loc[row_count, count_df_col[0]] = k
                    for j in range(1, len(count_df_col)):
                        count_df.loc[row_count, count_df_col[j]] = df.loc[i, count_df_col[j]]
                    row_count += 1

    for k, v in counter.items():
        for i in count_df[count_df["keyword"] == k].index:
            count_df.loc[i, "count"] = v
        # count_df.loc[k, "count"] = v

    if count_df.shape[0] > 0:
        count_df = count_df[count_df["count"] > 4]
        # count_df.sort_values(ascending=False, by=['count'], axis=0, inplace=True)
        count_df.reset_index(drop=True, inplace=True)

    if count_df.shape[0] > 0:
        count_df.index.name = "index"
        count_file_path = "\\".join(filePath.split('\\')[:-1])
        count_file_path += "\\word_count"

        count_output = output_file_name.split('.')[0]  # ex) 현대차_20211203_n
        count_output += 'c.xlsx'  # ex) 현대차_20211203_ns.xlsx

        count_output_path = fr'{count_file_path}\{count_output}'

        Path.createFolder(count_file_path)

        if os.path.exists(count_output_path):
            writeToExcel(output_path=count_output_path, df=count_df, sheet_name=sheetName, isWrite=False)
        else:
            writeToExcel(output_path=count_output_path, df=count_df, sheet_name=sheetName, isWrite=True)

        return count_file_path, count_output
    else:
        return False, False


if __name__ == "__main__":
    # arg_list = sys.argv[1:]  # argument 받아서 실행
    arg_list = ['car', '기아', '5', '정의선', '0', '20211024', '20211024']

    category = arg_list[0]
    companyName = arg_list[1]
    maxpage = int(arg_list[2])
    query = arg_list[3]
    sort = arg_list[4]
    s_date = arg_list[5]
    e_date = arg_list[6]

    target_col = "contents"
    output_file_name = f'{companyName}_{e_date}_n.xlsx'
    filePath = fr"{Path.RESULT_PATH_NEWS}\{category}\{companyName}\news"

    # filePath, output_file_name = crawler(category=category, companyName=companyName, query=query, sort=sort,
    #                                      s_date=s_date, e_date=e_date)
    #
    # sentiment(filePath=filePath, output_file_name=output_file_name, sheetName=query, target_col=target_col)

    make_word_count(filePath=filePath, output_file_name=output_file_name, sheetName=query, target_col=target_col)