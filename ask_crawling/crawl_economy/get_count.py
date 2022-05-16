from collections import Counter

import pandas as pd
from konlpy.tag import Kkma

if __name__ == "__main__":
    path = None
    df = pd.read_csv(path)
    han = Kkma()

    countResult = Counter()

    for title in df['D']:
        nounResult = han.nouns(title)
        wordCount = Counter(nounResult)

        del_key_list = []

        for k, v in wordCount.items():
            if len(k) <= 1 or k.isdigit():  # 1 글자 이하 단어나 숫자면
                del_key_list.append(k)

        for k in del_key_list:
            del wordCount[k]

        countResult = Counter(countResult) + Counter(wordCount)

    sorted_wc = countResult.most_common()
    print(sorted_wc)
