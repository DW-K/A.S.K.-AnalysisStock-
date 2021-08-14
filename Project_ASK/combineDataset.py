import Path
import pandas as pd

from Path import RESULT_PATH_STOCK, RESULT_PATH_NEWS


def deleteDotInDate(df_n):
    for i, row in df_n.iterrows():
        df_n.loc[i, 'date'] = row.loc['date'].replace(".", "")
    return df_n


def combDf(df_n, df_s):  # 주식 데이터에 뉴스 데이터 얹기
    df_s.rename(columns={'일자': "date"}, inplace=True)  # column 이름 바꾸기

    df_s['date'] = df_s['date'].astype(str)  # datatype 바꾸기 int -> str

    result = df_s.merge(df_n, how='left', on='date')  # left outer merge

    result = result.drop_duplicates()  # 중복 제거

    result.reset_index(drop=True, inplace=True)  # index 재설정

    result.index.name = 'index'  # index 이름 설정

    result.to_excel(rf'{Path.RESULT_PATH_COMBINE}\result.xlsx')

    return result


def getData(filePath_n=None, fileName_n=None, filePath_s=None, fileName_s=None):
    df_n_col = ['span', 'positive', 'negative', '스포츠', '사회', '정치', '경제', '생활/문화', 'IT/과학']
    df_n = pd.read_excel(rf'{RESULT_PATH_NEWS}\{filePath_n}\{fileName_n}', index_col="index")
    df_n = df_n.loc[:, df_n_col]  # df_n_col 열만 남기기
    df_n.rename(columns={'span': 'date'}, inplace=True)  # column name 바꾸기
    df_n = deleteDotInDate(df_n)  # date의 . 지우기
    df_n.sort_values(by=['date'], axis=0, inplace=True)  # sorting

    df_s = pd.read_excel(rf'{RESULT_PATH_STOCK}\{filePath_s}\{fileName_s}', index_col="index")
    df_s = df_s.iloc[:, 1:]  # 'index' column 제외

    result = combDf(df_n, df_s)

    return result
