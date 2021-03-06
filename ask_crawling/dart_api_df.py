import dart_fss as dart
import pandas as pd
from datetime import datetime
api_key='a7d190f68ff1aea4f8be10a34331373368efc1f2'
dart.set_api_key(api_key=api_key)

#현재날짜
now = datetime.now()
nowDate = now.strftime('%Y%m%d%H%M')

corp_list=dart.get_corp_list()
corp_name=corp_list.find_by_corp_name('기아', exactly=True)[0]
# fs = dart.fs.extract(corp_code=corp_code, bgn_de=bgn_de, end_de=end_de, report_tp='quarter', lang='ko', separator=False)
fs=corp_name.extract_fs(bgn_de='20200101', end_de='20220125', report_tp='quarter', lang='ko', separator=False)
df_fs = fs['bs']
df_is = fs['is']
df_cis = fs['cis']
df_cf = fs['cf']
# print(df_fs)
# print(df_is)
# print(df_cis)
# print(df_cf)
# df_all = [df_fs, df_is, df_cis, df_cf]
df_fs.to_excel('fs.xlsx')
df_is.to_excel('if.xlsx')
df_cis.to_excel('cis.xlsx')
df_cf.to_excel('cf.xlsx')

# ‘bs’:  재무상태표, ‘is’ 손익계산서, ‘cis’ : 포괄손익계산서, ‘cf’ : 현금흐름표
# 손익계산서 빼도 될 것 같다.
# fs.save()