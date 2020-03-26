package kr.hyosang.cardiary.exception;

public class UnknownAuthorizationHeaderException extends BaseException {
    public UnknownAuthorizationHeaderException(String authHeader) {
        super("Unknown authorization header: " + authHeader);
    }
}
