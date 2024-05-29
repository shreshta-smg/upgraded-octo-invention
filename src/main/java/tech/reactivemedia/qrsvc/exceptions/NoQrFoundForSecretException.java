package tech.reactivemedia.qrsvc.exceptions;

public class NoQrFoundForSecretException extends RuntimeException{
    public NoQrFoundForSecretException() {
    }

    public NoQrFoundForSecretException(String message) {
        super(message);
    }
}
