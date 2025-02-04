package ru.job4j.api.telegram;

public class SendContentException extends RuntimeException {

    public SendContentException(Throwable throwable) {
        super(throwable);
    }

    public SendContentException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
