import os
import time

import numpy as np
from openpyxl import load_workbook

import Path
import pandas as pd
from datetime import datetime
from datetime import timedelta

from Path import RESULT_PATH_STOCK, RESULT_PATH_NEWS, RESOURCE_PATH_STOCK_INFO, RESULT_PATH_TWEET

infoPath = RESOURCE_PATH_STOCK_INFO
filePath_news = Path.RESULT_PATH_NEWS
filePath_stock = Path.RESULT_PATH_STOCK


def deleteDot(df):
    for i, row in df.iterrows():
        df.loc[i, 'date'] = row.loc['date'].replace(".", "")
    return df


def get_sentiment(category, company_name, target_date):
    df_positive = 0
    df_negative = 0
    count = 0

    df_news_dict = pd.read_excel(rf'{RESULT_PATH_NEWS}\{category}\{company_name}\sentiment\{company_name}_{target_date}_ns.xlsx',
                                 index_col="index", sheet_name=None)
    for key, df_news_element in df_news_dict.items():
        df_news_element_positive = df_news_element['positive'].mean()
        df_news_element_negative = df_news_element['negative'].mean()
        df_positive += df_news_element_positive
        df_negative += df_news_element_negative
        count += 1

    df_twitter_dict = pd.read_excel(rf'{RESULT_PATH_TWEET}\{category}\{company_name}\sentiment\{company_name}_{target_date}_ns.xlsx',
                                    index_col="index", sheet_name=None)
    for key, df_twitter_element in df_twitter_dict.items():
        df_twitter_element_positive = df_twitter_element['positive'].mean()
        df_twitter_element_negative = df_twitter_element['negative'].mean()
        df_positive += df_twitter_element_positive
        df_negative += df_twitter_element_negative
        count += 1

    df_positive /= count
    df_negative /= count

    return df_positive, df_negative


def combineData(category, company_name, target_date):
    epsilon = pow(10, -10)
    stock_index = "날짜"
    news_sentiment_logit_name = 'news_sentiment_logit'
    isExist_file = False

    start = time.time()

    combine_filePath = rf'{Path.RESULT_PATH_COMBINE}\{category}\{company_name}'
    combine_outputPath = rf'{combine_filePath}\{company_name}_combine.xlsx'
    Path.createFolder(combine_filePath)

    if os.path.exists(combine_outputPath):
        isExist_file = True
        df_stock = pd.read_excel(combine_outputPath, dtype={stock_index: str})
        df_stock.set_index(stock_index, drop=True, inplace=True)
        df_stock.dropna(how='all', inplace=True)
    else:
        df_stock = pd.read_excel(rf'{RESULT_PATH_STOCK}\{category}\{company_name}\{company_name}_s.xlsx',
                                 dtype={stock_index: str})
        df_stock.set_index(stock_index, drop=True, inplace=True)
        df_stock[news_sentiment_logit_name] = np.NAN    # add empty column

    if target_date in df_stock.index:   # subtract weekend (add to next day later)
        if df_stock.loc[target_date].isnull()[news_sentiment_logit_name]:      # if logit is NAN
            positive_mean, negative_mean = get_sentiment(category, company_name, target_date)    # add sentiment logit column to df_stock
            news_sentiment_logit = positive_mean / (negative_mean + epsilon)
            df_stock.loc[target_date, news_sentiment_logit_name] = news_sentiment_logit

            if isExist_file:
                print("file exist")
                with pd.ExcelWriter(combine_outputPath, mode='a', engine='openpyxl', if_sheet_exists="replace") as writer:
                    df_stock.to_excel(writer, sheet_name=company_name)
            else:
                print("file not exist")
                with pd.ExcelWriter(combine_outputPath, mode='w', engine='openpyxl') as writer:
                    df_stock.to_excel(writer, sheet_name=company_name)
        else:
            print(f'{company_name}: {target_date} has sentiment_logit')
            # print(df_stock[target_date])

    print("time :", time.time() - start)

    return 0


if __name__ == "__main__":
    category = "car"
    company_name = "현대차"
    s_date = "20210611"
    dateFormat = "%Y%m%d"
    today = datetime.now()

    s_date_format = datetime.strptime(s_date, dateFormat)
    # target_date = "20211203"

    target_date_format = s_date_format
    while (today - target_date_format).days > 0:
        target_date = target_date_format.strftime(dateFormat)
        result = combineData(category, company_name, target_date)
        target_date_format += timedelta(days=1)
        print(target_date)
