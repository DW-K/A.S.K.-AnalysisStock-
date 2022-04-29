import numpy as np
import pandas as pd

from collections import Counter
from konlpy.tag import Hannanum
from konlpy.tag import Kkma
from konlpy.tag import Komoran


def make_word_count(df):
    target_col = '기사 내용'

    han = Kkma()

    countResult = {}

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

    return dict(countResult)


if __name__ == "__main__":
    df = pd.read_csv(r'../dataset/crawling.csv')
    make_word_count(df=df)

    # w = '2025'
    # print(w.isdigit())

