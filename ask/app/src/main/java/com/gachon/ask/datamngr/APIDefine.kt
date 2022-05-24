package com.gachon.ask.datamngr

// api에서 정의된 리턴값들 입니다. 참조용으로 사용하시기 바랍니다.
class API_DEFINE {
    companion object {
        const val RECEIVE_TIMEOUTERROR = -7              // TIMEOUT 에러
        const val RECEIVE_INITECHERROR = -6              // initech 핸드세이킹 에러
        const val RECEIVE_PERMISSIONERROR = -5              // 퍼미션취소
        const val RECEIVE_ERROR = -4              // 일반적인 에러 (로그인 실패도 여기로 옴)
        const val RECEIVE_DISCONNECT = -3              // SOCKET이 연결종료된 경우
        const val RECEIVE_SYSTEMERROR = -2              // 서버에서 내려주는 시스템에러
        const val RECEIVE_CONNECTERROR = -1              // SOCKET 연결에러
        const val RECEIVE_CONNECT = 0              // SOCKET 연결완료
        const val RECEIVE_DATA = 1              // TR데이타 수신
        const val RECEIVE_REALDATA = 2              // 실시간데이타 수신
        const val RECEIVE_MSG = 3              // TR메세지 수신
        const val RECEIVE_LOGINCOMPLETE = 4              // 로그인완료
        const val RECEIVE_RECONNECT = 5              // SOCKET종료후 재연결 완료
        const val RECEIVE_SIGN = 6              // 선택한 공인인증서 관련 정보
        const val RECEIVE_INITECHOK = 7              // 이니텍 핸드세이킹 OK
        const val RECEIVE_RELEASE = 8              // RELEASE
        const val RECEIVE_REALDATA_SEARCH = 9              // 종목검색 실시간데이타 수신

        const val API_SUCCESS = 0
        const val API_ERROR_SOCKET_CREATE_FAIL = -1             // 소켓생성 실패
        const val API_ERROR_CONNECT_FAIL = -2             // 서버연결 실패
        const val API_ERROR_WRONG_ADDRESS = -3             // 서버주소가 틀렸음
        const val API_ERROR_CONNECT_TIMEOUT = -4             // 연결시간 초과
        const val API_ERROR_ALREADY_CONNECT = -5             // 이미 서버에 연결중입니다.
        const val API_ERROR_CANT_USE_TR = -6             // 해당 TR은 사용할 수 없습니다.
        const val API_ERROR_NEED_LOGIN = -7             // 로그인이 필요합니다.
        const val API_ERROR_CANT_USE_SISEONLY = -8             // 시세전용에서는 사용이 불가능합니다.
        const val API_ERROR_HAVE_NOT_ACCOUNT = -9             // 계좌리스트에서는 사용이 불가능합니다.
        const val API_ERROR_FAULT_PACKET_SIZE = -10            // Packet의 크기가 잘못되었습니다.
        const val API_ERROR_DIFF_DATA_SIZE = -11            // Data 크기가 다릅니다.
        const val API_ERROR_NOT_EXIST_ACCOUNT = -12            // 계좌가 존재하지 않습니다.
        const val API_ERROR_NOT_ENOUGH_RQID = -13            // Request ID 부족
        const val API_ERROR_NOT_CREATE_SOCKET = -14            // 소켓이 생성되지 않았습니다.
        const val API_ERROR_ENCRYPT_CREATE_FAIL = -15            // 암호화 생성에 실패했습니다.
        const val API_ERROR_DATA_SEND_FAIL = -16            // 데이터 전송에 실패했습니다.
        const val API_ERROR_FAIL_ENCRYPT_RTN = -17            // 암호화(RTN)처리에 실패했습니다.
        const val API_ERROR_CERT_NOT_EXIST_FILE = -18            // 공인인증 파일이 없습니다.
        const val API_ERROR_CERT_NOT_EXIST_FUNC = -19            // 공인인증 Function이 없습니다.
        const val API_ERROR_NOT_ENOUGH_MEMORY = -20            // 메모리가 충분하지 않다.
        const val API_ERROR_LIMIT_TR = -21            // TR의 사용제한.
        const val API_ERROR_NOT_USE_FUNCTION = -22            // 해당 TR은 해당 함수를 이용할 수 없습니다.
        const val API_ERROR_NOT_FIND_TR_INFO = -23            // TR에 대한 정보를 찾을 수 없습니다.
        const val API_ERROR_ACCOUNT_POS = -24            // 계좌위치가 지정되지 않았습니다.
        const val API_ERROR_HAVE_NOT_ACCOUNTLIST = -25            // 계좌를 가지고 있지 않습니다.
        const val API_ERROR_FILE_READ = -26            // 파일읽기에 실패했습니다.
        const val API_ERROR_REALKEY_REG = -27            // 실시간 종목검색 조건 등록 건수가 최대 갯수를 초과하였습니다.
        const val API_ERROR_REALKEY = -28            // 등록키에대한 정보를 찾을 수 없습니다.
        const val API_ERROR_MULTILOGIN = -29            // 다중접속제한
        const val API_ERROR_LIMIT_MAX_TR = -34            // TR의 1시간내 최대 전송 가능횟수를 초과
        const val API_ERROR_MULTILOGIN_OVER = -35            // 다중접속을 초과하여 접속을 요청하였습니다.
        const val API_ERROR_NOT_INPUT_SEARCHREAL = -37            // 실시간 종목검색 등록 데이타 오류
        const val API_ERROR_NOT_INPUT_ORD_QTY = -38            // 주문TR 입력필드 수량 문제있는 경우
        const val API_ERROR_NOT_INPUT_ORD_PRICE = -39            // 주문TR 입력필드 단가
        const val API_ERROR_NOT_INPUT_ORD_CODE = -40            // 주문TR 입력필드 종목코드
        const val API_ERROR_NOT_INPUT_ORD_PWD = -41            // 주문TR 입력필드 비밀번호
        const val API_ERROR_NOT_INPUT_ORD_ORIGNO = -42            // 주문TR 입력필드 원주문번호 문제있는 경우
        const val API_ERROR_ETC = -99            // 기타에러

        //==========================================================================================
        // Field Data Type
        const val TYPE_INT = 1                                 // 정수
        const val TYPE_REAL = 2                                 // 실수
        const val TYPE_STR = 3                                 // 문자열

        //==========================================================================================
        // Field Block In/Out
        const val BLOCK_IN = 1
        const val BLOCK_OUT = 2

        //==========================================================================================
        // TR / Block Info
        const val ATTR = true                              // Attribute
        const val OCCURS = true                              // Block Occurs

        //==========================================================================================
        // Header Type ... 추후 추가할것
        const val HEADER_A = 'A'
        const val HEADER_D = 'D'
    }
}
