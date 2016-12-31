package com.androidlokomedia.crudvolley;

/**
 * Created by ahmad on 28/12/2016.
 */
public class Data {
    private int id;
    private String email;
    private String password;

//    public Data(int id, String email, String password) {
//        this.id = id;
//        this.email = email;
//        this.password = password;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
