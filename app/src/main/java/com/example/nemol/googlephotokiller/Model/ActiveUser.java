package com.example.nemol.googlephotokiller.Model;

/**
 * Created by nemol on 12.11.2017.
 */

public class ActiveUser{

    private static String login;
    private static String password;
    private static int id;

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        ActiveUser.id = id;
    }

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        ActiveUser.login = login;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        ActiveUser.password = password;
    }

    public static void isAuth(boolean auth){
        if(!auth){
            id = -1;
            login = "";
            password = "";
        }
    }


}
