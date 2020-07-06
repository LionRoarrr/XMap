package com.liangnie.xmap.login;

public class User {
    private String username;
    private String password;
    public User(String name, String password) {
        this.username = name;
        this.password = password;
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
