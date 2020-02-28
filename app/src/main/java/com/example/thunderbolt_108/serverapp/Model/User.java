package com.example.thunderbolt_108.serverapp.Model;

public class User {
    private String name;
    private String password;
    private String Phone;
    private String IsStaff;

    public User() {
    }

    public User(String Pname, String Ppassword) {


        name = Pname;
        password = Ppassword;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPhone() {
        return Phone;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getIsStaff() {
        return IsStaff;
    }
}
