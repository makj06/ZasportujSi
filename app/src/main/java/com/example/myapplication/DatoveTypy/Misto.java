package com.example.myapplication.DatoveTypy;

public class Misto {
    private String nazev, adresa, mistoID;

    public Misto(String nazev, String adresa, String mistoID) {
        this.nazev = nazev;
        this.adresa = adresa;
        this.mistoID = mistoID;
    }

    //Gettery a settery
    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public String getMistoID() {
        return mistoID;
    }

}
