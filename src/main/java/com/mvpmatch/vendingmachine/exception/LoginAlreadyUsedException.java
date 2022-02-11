package com.mvpmatch.vendingmachine.exception;

public class LoginAlreadyUsedException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public LoginAlreadyUsedException() {
        super("This username already exist. Try another one!");
    }
}