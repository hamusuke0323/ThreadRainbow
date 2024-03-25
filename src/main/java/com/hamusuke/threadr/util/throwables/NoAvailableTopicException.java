package com.hamusuke.threadr.util.throwables;

public class NoAvailableTopicException extends RuntimeException {
    public NoAvailableTopicException(String msg) {
        super(msg);
    }

    public NoAvailableTopicException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
