pw = 'ask1234!'

db_connection_address = f'mysql+pymysql://root:{pw}@localhost:3306/ASK'
# db_connection_address = f'mysql+pymysql://root:{pw}@13.209.122.152:3306/ASK'

import pandas as pd

if __name__ == "__main__":
    df = pd.DataFrame([[1,2,3], [4,5,6]], columns=['a','b','c'])

    js = df.to_json(orient = 'columns')

    print(js)