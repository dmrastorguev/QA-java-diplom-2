package ru.yandex.praktikum.model;

public class UserLogout {
    String token;

    public UserLogout(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
