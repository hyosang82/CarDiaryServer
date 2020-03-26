package kr.hyosang.cardiary;

public class Const {
    public static final String APIKEY_DAUM = "5416ff51152399e3df7dfc2a03697acc";	//for ALL
    public static final String NAVER_CLIENT_ID = "FDfBe1cvmUH0co3N3zYR";
    public static final String HTTP_HEADER_AUTH = "Authorization";

    public static class ErrorCode {
        public static final int ERR_NOT_LOGGED_IN = 10001;
        public static final int ERR_NOT_JOINED = 10002;
        public static final int ERR_INVALID_AUTHORIZATION = 10003;
        public static final int ERR_OMITTED_MANDATORY_PARAM = 10004;
        public static final int ERR_INVALID_PARAMETER_VALUE = 10005;
        public static final int ERR_INVALID_OWNERSHIP = 10006;

        public static final int ERR_FROM_DEEP = 19990;
    }
}

