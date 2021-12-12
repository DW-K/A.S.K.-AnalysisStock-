import pandas as pd
import requests
import openpyxl

# 한글깨짐 방지
import sys



code = input("코드를 입력하세요 : ")
companyName = input("기업이름을 입력하세요 : ")
URL = f"https://finance.naver.com/item/main.nhn?code={code}"


company_report= requests.get(URL)
html = company_report.text

financial_stmt = pd.read_html(company_report.text)[3]

financial_stmt.set_index(('주요재무정보', '주요재무정보', '주요재무정보'), inplace=True)
financial_stmt.index.rename('주요재무정보', inplace=True)
financial_stmt.columns = financial_stmt.columns.droplevel(2)
annual_date = pd.DataFrame(financial_stmt).xs('최근 연간 실적', axis = 1)
quarter_date = pd.DataFrame(financial_stmt).xs('최근 연간 실적', axis = 1)

annual = annual_date
annual.to_excel(f'{companyName}_{code}_annual.xlsx')

quarter = quarter_date
quarter.to_excel(f'{companyName}_{code}_quarter.xlsx')




#
# if __name__ == "__main__":
#     arg_list = sys.argv[1:]
#     category = arg_list[0]
#     companyName = arg_list[1]
#     query = arg_list[3]
#
#




