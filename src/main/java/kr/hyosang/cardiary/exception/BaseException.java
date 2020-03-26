package kr.hyosang.cardiary.exception;

public class BaseException extends Exception {
    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(String msg, Throwable th) {
        super(msg, th);
    }
}
