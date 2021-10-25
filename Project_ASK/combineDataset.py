import Path
import pandas as pd
import datetime

from Path import RESULT_PATH_STOCK, RESULT_PATH_NEWS


def deleteDot(df):
    for i, row in df.iterrows():
        df.loc[i, 'date'] = row.loc['date'].replace(".", "")
    return df


def addQuarter(df):
    now = datetime.datetime.now()
    cur_year = int(now.year)
    cur_month = int(now.month)
    cur_quarter = int((cur_month-1) / 3) + 1

    count = 0
    for i, row in df.iterrows():
        count = (cur_year - int(row.loc['date'][:4])) * 4
        temp_quarter = int( (int(row.loc['date'][4:6])-1) / 3) + 1
        count = count + (cur_quarter - temp_quarter)
        df.loc[i, 'quarter'] = count
    df.loc[:, 'quarter'] = (df.loc[:, 'quarter'] - count) * -1
    return df


def combDf(df_n, df_s):  # 주식 데이터에 뉴스 데이터 얹기
    result = df_s.merge(df_n, how='left', on='date')  # left outer merge

    result.fillna(method='bfill', limit=5, inplace=True)
    result.fillna(0, inplace=True)

    result = addQuarter(result)
    result.index = result['date']
    result.drop('date', axis=1, inplace=True)

    return result


def getData(filePath_n=None, fileName_n=None, filePath_s=None, fileName_s=None):
    df_n_col = ['span', 'positive', 'negative'] #, '스포츠', '사회', '정치', '경제', '생활/문화', 'IT/과학']
    df_n = pd.read_excel(rf'{RESULT_PATH_NEWS}\{filePath_n}\{fileName_n}', index_col="index")
    df_n = df_n.loc[:, df_n_col]  # df_n_col 열만 남기기
    df_n.rename(columns={'span': 'date'}, inplace=True)  # column name 바꾸기
    df_n = deleteDot(df_n)  # date의 . 지우기

    df_n.sort_values(by=['date'], axis=0, inplace=True)  # sorting
    df_n = df_n.groupby('date').mean()

    df_n['sentiment_logit'] = df_n['positive'] / (df_n['negative'] + pow(10, -10))
    df_n = df_n['sentiment_logit']

    df_s = pd.read_excel(rf'{RESULT_PATH_STOCK}\{filePath_s}\{fileName_s}', index_col="index")
    df_s = df_s.iloc[:, 1:]  # 'index' column 제외
    df_s.rename(columns={'일자': "date"}, inplace=True)  # column 이름 바꾸기
    df_s['date'] = df_s['date'].astype(str)  # datatype 바꾸기 int -> str

    result = combDf(df_n, df_s)

    result.to_excel(rf'{Path.RESULT_PATH_COMBINE}\result.xlsx')

    return result


if __name__ == "__main__":
    filePath_n = "210817"
    filePath_s = "210817"
    fileName_s = rf"와이지엔터테인먼트_210817_s.xlsx"
    fileName_n = rf"YG엔터_210817_n.xlsx"

    result = getData(filePath_n, fileName_n, filePath_s, fileName_s)