package com.example.myapplication.DatoveTypy;

public class OblibeneMisto {
    //Inicializace proměnných
    private String ulice,cisloPopisne, mesto, nazev, IdMista;
    private double zemSirka, zemDelka;

    //Prázdný konstruktor pro Firebase
    public OblibeneMisto(){}

    //Kontruktor


    public OblibeneMisto(String ulice, String cisloPopisne, String mesto, String nazev, String idMista, double zemSirka, double zemDelka) {
        this.ulice = ulice;
        this.cisloPopisne = cisloPopisne;
        this.mesto = mesto;
        this.nazev = nazev;
        this.IdMista = idMista;
        this.zemSirka = zemSirka;
        this.zemDelka = zemDelka;
    }

    //Gettery a settery
    public String getUlice() {
        return ulice;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public double getZemSirka() {
        return zemSirka;
    }

    public void setZemSirka(double zemSirka) {
        this.zemSirka = zemSirka;
    }

    public double getZemDelka() {
        return zemDelka;
    }

    public void setZemDelka(double zemDelka) {
        this.zemDelka = zemDelka;
    }

    public String getCisloPopisne() {
        return cisloPopisne;
    }

    public String getMesto() {
        return mesto;
    }

    public void setMesto(String mesto) {
        this.mesto = mesto;
    }
}
