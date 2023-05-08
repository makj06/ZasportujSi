package com.example.myapplication.DatoveTypy;

public class Kamarad {
    //Inicializace proměnných
    String email, userID;

    //Prázdný konstruktor pro Firebase
    public Kamarad() {}

    //Konstruktor
    public Kamarad(String email, String userID) {
        this.email = email;
        this.userID = userID;
    }

    //Getry a setry
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
