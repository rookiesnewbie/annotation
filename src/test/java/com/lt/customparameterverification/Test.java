package com.lt.customparameterverification;

public class Test {
    public static void main(String[] args) {
        String s1 = "abDd1354699875645@";
        System.out.println(s1.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*_])[a-zA-Z\\d~!@#$%^&*_]{8,18}$\n"));

        String s = "aAbc123@!4";
        System.out.println(s.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,18}$"));

    }
}
