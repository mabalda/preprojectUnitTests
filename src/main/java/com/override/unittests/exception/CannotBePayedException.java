package com.override.unittests.exception;

public class CannotBePayedException extends RuntimeException{
    public CannotBePayedException(double amount, double monthlyAmount) {
        super("Credit " + amount + " can not be payed with monthly pay " + monthlyAmount);
    }

}
