package com.thema3statistik;

public class TabelleEintrag {

    private Reaktionszeit typ;
    private double reaktionszeit;
    private int rang;

    public TabelleEintrag(int rang, double reaktionszeit, Reaktionszeit typ) {
        this.typ = typ;
        this.rang = rang;
        this.reaktionszeit = reaktionszeit;
    }

    public Reaktionszeit getReaktionszeitTyp() {
        return this.typ;
    }

    public int getRang() {
        return this.rang;
    }

    public void setRang(int neuerRang) {
        this.rang = neuerRang;
    }

    public double getReaktionszeit() {
        return this.reaktionszeit;
    }
}

enum Reaktionszeit {
    RENNEN, 
    TRAINING
}
