package ru.yandex.praktikum.data;

import org.apache.commons.lang3.RandomStringUtils;

public class UserGeneration {

    public String userEmail() {
        String userEmail = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".ru";
        return userEmail;
    }

    public String userPassword() {
        String userPassword = RandomStringUtils.randomNumeric(7);
        return userPassword;
    }

    public String userName() {
        String userName = RandomStringUtils.randomAlphabetic(7);
        return userName;
    }
}
