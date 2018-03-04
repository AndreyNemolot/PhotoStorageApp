package com.example.nemol.googlephotokiller.Model;

/**
 * Created by nemol on 12.11.2017.
 */

public class ActiveUser{

    private static String login;
    private static String password;
    private static int id;
    private static boolean isOnline;

    public static void saveUser(String login, String password, boolean isOnline){
        ActiveUser.login = login;
        ActiveUser.password = password;
        ActiveUser.isOnline = isOnline;
    }
    public static void saveUser(int id, String login, String password, boolean isOnline){
        ActiveUser.id = id;
        ActiveUser.login = login;
        ActiveUser.password = password;
        ActiveUser.isOnline = isOnline;
    }


    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        ActiveUser.id = id;
    }

    public static String getLogin() {
        return login;
    }

    public static String getPassword() {
        return password;
    }

    public static boolean isOnline() {
        return ActiveUser.isOnline;
    }

   /*public static void setPassword(String password) {
        ActiveUser.password = password;
    }*/

    public static void isAuth(boolean auth){
        if(!auth){
            id = -1;
            login = "";
            password = "";
        }
    }


}
