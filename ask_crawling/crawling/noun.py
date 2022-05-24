from konlpy.tag import Okt

if __name__ == "__main__":
    okt = Okt()

    sentence = [
        '쌍방울그룹주, 쌍용차 인수 추진에 일제히 상한가(종합)',
        '하나·농협은행, 적격대출 재개 첫날…"3%대 금리 관심 집중"',
        '“하~ 6만전자·11만닉스”… 역대급 실적에도 부진 왜',
        "삼성·LG전자, 이번주 1분기 잠정 실적…악재 속 '최대실적' 기대감",
        '현대重 울산조선소 사망사고…고용부, 중대재해법 위반 조사'
    ]

    for s in sentence:
        print(s)
        print(okt.nouns(s))
        print()