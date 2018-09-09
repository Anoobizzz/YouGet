package io.github.anoobizzz.youget.exception;

public class AgeRestrictionException extends RuntimeException {
    public AgeRestrictionException() {
        super("Age restriction, account required");
    }
}