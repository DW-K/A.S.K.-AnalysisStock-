import dart_fss as dart
import datetime

# Open DART API KEY 설정
api_key='a7d190f68ff1aea4f8be10a34331373368efc1f2'
dart.set_api_key(api_key=api_key)

now = datetime.datetime.now()
nowDate = now.strftime('%Y%m%d%H%M')

# DART 에 공시된 회사 리스트 불러오기
corp_list = dart.get_corp_list()

# 삼성전자 검색
corp_name = corp_list.find_by_corp_name('펄어비스', exactly=True)[0]

# 20??년부터 연간 연결재무제표 불러오기
fs = corp_name.extract_fs(bgn_de='20110101')

# 재무제표 검색 결과를 엑셀파일로 저장 ( 기본저장위치: 실행폴더/fsdata )

fs.save('dart_api.xlsx')

# 재무제표에서 제무상태표만 출력하는 코드
# ‘bs’:  재무상태표, ‘is’ : 손익계산서, ‘cis’ : 포괄손익계산서, ‘cf’ : 현금흐름표
#손익계산서-> 기업경영활동의 성과를 나타내는 보고서
#현금흐름표-> 일정기간 기업의 현금흐름을 나타내는표
#재무상태표-> 기업의 자산과 부채 모두 열거-투자자가 한눈에 투자 자산을 검토할 수 있도록 정리한표
#일정기간 기업의 경영 성과를 포괄적으로 한눈에 나타내기 위해 작성하는 재무제표
# df = fs.show('bs')
# print(df)
