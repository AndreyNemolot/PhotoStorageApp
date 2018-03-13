package com.example.nemol.googlephotokiller.Model;

import java.io.Serializable;

/**
 * Created by nemol on 01.11.2017.
 */

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String login;
    private String password;
    private short enabled;
    private String role;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.role = "ROLE_USER";
        this.enabled = 1;
    }

    public short getEnabled() {
        return enabled;
    }
    public String getRole() {
        return role;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}