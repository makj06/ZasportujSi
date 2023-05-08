package com.example.myapplication.DatoveTypy;

public class Uzivatel {
    String jmeno, prijmeni, email, telefon, narozen, userID;

    //Prázdný konstruktor pro Firebase
    public Uzivatel(){}

    public Uzivatel(String jmeno, String prijmeni, String email, String telefon, String narozen, String userID) {
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.email = email;
        this.telefon = telefon;
        this.narozen = narozen;
        this.userID = userID;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getNarozen() {
        return narozen;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
