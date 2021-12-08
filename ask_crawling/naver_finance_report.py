import pandas as pd
import requests
import openpyxl

code = input("코드를 입력하세요 : ")
URL = f"https://finance.naver.com/item/main.nhn?code={code}"

samsung_electronic = requests.get(URL)
html = samsung_electronic.text

financial_stmt = pd.read_html(samsung_electronic.text)[3]

financial_stmt.set_index(('주요재무정보', '주요재무정보', '주요재무정보'), inplace=True)
financial_stmt.index.rename('주요재무정보', inplace=True)
financial_stmt.columns = financial_stmt.columns.droplevel(2)
print(financial_stmt)
data = financial_stmt
data.to_excel(f'{code}.xlsx')
