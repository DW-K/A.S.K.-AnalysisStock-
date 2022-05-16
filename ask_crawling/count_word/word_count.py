from datetime import date

import numpy as np
import pandas as pd

from collections import Counter
from konlpy.tag import Hannanum
from konlpy.tag import Kkma
from konlpy.tag import Komoran

from database.models import create_tables
from database.word_db_sql import read_table_news, read_table_news_for_count, insert_table_news_count


def make_word_count(company, day):
    df_news = read_table_news(company, day)
    wc_counter = make_word_count_counter(df_news)
    sorted_wc = wc_counter.most_common()

    cols = ['date', 'word', 'count', 'company', 'positive', 'negative']
    df_result = pd.DataFrame(columns=cols)

    for i in range(len(sorted_wc)):
        word = sorted_wc[i][0]
        count = sorted_wc[i][1]
        df_count = read_table_news_for_count(company, day, word)

        if count > 1:
            new_row = {'date': day, 'word': word, 'count': count, 'company': company, 'positive':df_count['positive'].mean(), 'negative': df_count['negative'].mean()}
            df_result = df_result.append(new_row, ignore_index=True)

    print('make df')

    insert_table_news_count(df_result.iloc[:5, :])


def make_word_count_counter(df):
    target_col = 'article_content'

    han = Kkma()

    countResult = Counter()

    for i in df.index:
        if df.loc[i, target_col] is not np.NAN:
            nounResult = han.nouns(df.loc[i, target_col])
            wordCount = Counter(nounResult)

            del_key_list = []

            for k, v in wordCount.items():
                if len(k) <= 1 or k.isdigit():  # 1 글자 이하 단어나 숫자면
                    del_key_list.append(k)

            for k in del_key_list:
                del wordCount[k]

            countResult = Counter(countResult) + Counter(wordCount)

    return countResult


if __name__ == "__main__":
    create_tables()
    make_word_count('현대차', date(2021, 1, 1))

