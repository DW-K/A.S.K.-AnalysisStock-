import pandas as pd
import requests
import openpyxl

# 한글깨짐 방지
import sys
import io

import Path
from Path import writeToExcel

from sentiment import sentiment


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



if __name__ == "__main__":
    arg_list = sys.argv[1:]  # argument 받아서 실행
    category = arg_list[0]
    companyName = arg_list[1]
    query = arg_list[3]
    sort = arg_list[4]


    # filePath = crawler(category="에스엠", maxpage=20, query="이수만", sort="0", s_date="20211024", e_date="20201025")

    filePath, output_file_name = crawler(category=category, companyName=companyName, maxpage=query, sort=sort)

    sentiment(filePath=filePath, output_file_name=output_file_name, sheetName=query)


