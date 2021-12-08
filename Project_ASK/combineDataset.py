import os
import time

from openpyxl import load_workbook

import Path
import pandas as pd
from datetime import datetime
from datetime import timedelta

from Path import RESULT_PATH_STOCK, RESULT_PATH_NEWS, RESOURCE_PATH_STOCK_INFO

infoPath = RESOURCE_PATH_STOCK_INFO
filePath_news = Path.RESULT_PATH_NEWS
filePath_stock = Path.RESULT_PATH_STOCK


def deleteDot(df):
    for i, row in df.iterrows():
        df.loc[i, 'date'] = row.loc['date'].replace(".", "")
    return df


def get_news_sentiment(category, company_name, target_date):
    df_news_dict = pd.read_excel(rf'{RESULT_PATH_NEWS}\{category}\{company_name}\sentiment\{company_name}_{target_date}_ns.xlsx',
                                 index_col="index", sheet_name=None)

    df_news_positive = 0
    df_news_negative = 0
    for key, df_news_element in df_news_dict.items():
        df_news_element_positive = df_news_element['positive'].mean()
        df_news_element_negative = df_news_element['negative'].mean()
        df_news_positive += df_news_element_positive
        df_news_negative += df_news_element_negative

    return df_news_positive, df_news_negative


def combineData(category, company_name, target_date):
    epsilon = pow(10, -10)
    stock_index = "날짜"
    news_sentiment_logit_name = 'news_sentiment_logit'

    start = time.time()

    combine_filePath = rf'{Path.RESULT_PATH_COMBINE}\{category}\{company_name}'
    combine_fileName = rf'{Path.RESULT_PATH_COMBINE}\{category}\{company_name}\{company_name}_combine.xlsx'
    Path.createFolder(combine_filePath)

    if os.path.exists(combine_fileName):
        df_stock = pd.read_excel(combine_fileName, dtype={stock_index: str})
        df_stock.set_index(stock_index, drop=True, inplace=True)
        df_stock.dropna(how='all', inplace=True)
    else:
        df_stock = pd.read_excel(rf'{RESULT_PATH_STOCK}\{category}\{company_name}\{company_name}_s.xlsx',
                                 dtype={stock_index: str})
        df_stock.set_index(stock_index, drop=True, inplace=True)
        df_stock[news_sentiment_logit_name] = ""    # add empty column

    if target_date in df_stock.index:
        print(df_stock.index[-120:-100])
        print(target_date in df_stock.index)
        if df_stock.loc[target_date].isnull()[news_sentiment_logit_name]:      # if logit is NAN
            news_positive_mean, news_negative_mean = get_news_sentiment(category, company_name, target_date)    # add sentiment logit column to df_stock
            news_sentiment_logit = news_positive_mean / (news_negative_mean + epsilon)
            df_stock.loc[target_date, news_sentiment_logit_name] = news_sentiment_logit

            with pd.ExcelWriter(combine_fileName, mode='w', engine='openpyxl') as writer:
                df_stock.to_excel(writer, sheet_name=target_date)
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
