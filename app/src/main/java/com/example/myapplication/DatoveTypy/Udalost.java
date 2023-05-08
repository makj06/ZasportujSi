package com.example.myapplication.DatoveTypy;

import com.google.firebase.Timestamp;

/**
 * Třída, která slouží jako datová struktura pro tahání věcí z db následní dávání je do recycled view
 */

public class Udalost {

    //Inicializace proměnných
    private String nazev;
    private String zacatek;
    private String konec;
    private String misto;
    private String cilovka;
    private String datum;
    private String sport;
    private String kapacita;
    private String vytvoril;
    private Double zemDelka;
    private Double zemSirka;
    private Timestamp zacatekUdalosti;


    //Prázdný konstruktor pro Firebase
    public Udalost(){};

    //Konstruktor


    public Udalost(String nazev, String zacatek, String konec, String misto, String cilovka, String datum, String sport, String kapacita, String vytvoril, Double zemDelka, Double zemSirka, Timestamp zacatekUdalosti) {
        this.nazev = nazev;
        this.zacatek = zacatek;
        this.konec = konec;
        this.misto = misto;
        this.cilovka = cilovka;
        this.datum = datum;
        this.sport = sport;
        this.kapacita = kapacita;
        this.vytvoril = vytvoril;
        this.zemDelka = zemDelka;
        this.zemSirka = zemSirka;
        this.zacatekUdalosti = zacatekUdalosti;
    }

    //Getry a setry
    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public String getZacatek() {
        return zacatek;
    }

    public String getKonec() {
        return konec;
    }

    public String getMisto() {
        return misto;
    }

    public void setMisto(String misto) {
        this.misto = misto;
    }

    public String getCilovka() {
        return cilovka;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getSport() {
        return sport;
    }

    public String getKapacita() {
        return kapacita;
    }

    public String getUživatel() {
        return vytvoril;
    }

    public void setUživatel(String uživatel) {
        vytvoril = uživatel;
    }

    public Double getZemDelka() {
        return zemDelka;
    }

    public void setZemDelka(Double zemDelka) {
        this.zemDelka = zemDelka;
    }

    public Double getZemSirka() {
        return zemSirka;
    }

    public void setZemSirka(Double zemSirka) {
        this.zemSirka = zemSirka;
    }
}
