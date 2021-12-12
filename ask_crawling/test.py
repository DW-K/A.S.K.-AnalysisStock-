import pandas as pd
from pororo import Pororo

sa = Pororo(task="sentiment", model="brainbert.base.ko.shopping", lang="ko")

a = ['불법', '의혹', '우려', '강세', '차별화되다', '달성하다', '호조']

df = pd.DataFrame(a)

for i in a:
    sentimentResult = sa(i)
    print(f'{i}: {sentimentResult}')
