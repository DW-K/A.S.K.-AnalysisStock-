import sys

import Path
import pandas as pd
import os
from Path import writeToExcel

def integrate_word_count(category, companyName, target_date):
    output_file_name = f'{companyName}_{target_date}_nsc.xlsx'
    filePath = fr"{Path.RESULT_PATH_NEWS}\{category}\{companyName}\word_count"
    output_path = fr"{filePath}\{output_file_name}"
    # print(output_path)
    counter = {}
    integrate_df = pd.DataFrame()
    if os.path.exists(output_path):
        df_dict = pd.read_excel(output_path, sheet_name=None, index_col="index")
        for query, df in df_dict.items():
            if query != "total":
                for key in df["keyword"].unique():
                    if key not in counter.keys():
                        counter[key] = 0
                    counter[key] += df[df["keyword"] == key]["count"].iloc[0]
                    integrate_df = pd.concat([integrate_df, df[df["keyword"] == key]], axis=0)

        integrate_df.drop("count", axis=1, inplace=True)
        integrate_df.reset_index(drop=True, inplace=True)

        for i in integrate_df.index:
            key = integrate_df.loc[i, "keyword"]
            integrate_df.loc[i, "count"] = counter[key]
        integrate_df.sort_values(ascending=False, by=['count'], axis=0, inplace=True)
        integrate_df.reset_index(drop=True, inplace=True)
        integrate_df.index.name = "index"

        writeToExcel(output_path=output_path, df=integrate_df, sheet_name="total", isWrite=False)
        # print(counter)

    else:
        print("file not exists(integrate_word_count)")


if __name__ == "__main__":
    arg_list = sys.argv[1:]  # argument 받아서 실행
    # arg_list = ['car', '현대차', '5', '정의선', '0', '20211022', '20211022']

    category = arg_list[0]
    companyName = arg_list[1]
    target_date = arg_list[2]

    integrate_word_count(category, companyName, target_date)
