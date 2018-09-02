package com.github.anoobizzz.youget.exception;

public class UnavailablePlayerException extends RuntimeException {
    public UnavailablePlayerException() {
        super("Player is unavailable");
    }
}