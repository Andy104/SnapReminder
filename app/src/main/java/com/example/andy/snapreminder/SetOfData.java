package com.example.andy.snapreminder;


import android.util.Log;

public class SetOfData {
    private static final String TAG = "SetOfDate";

    public String id;
    public String tytul;
    public String opis;
    public String czas;
    public String data;
    public String sciezka;

    public SetOfData(String id, String tytul, String opis, String czas, String data, String sciezka) {
        this.id = id;
        this.tytul = tytul;
        this.opis = opis;
        this.czas = czas;
        this.data = data;
        this.sciezka = sciezka;

        Log.d(TAG, id + "|" + tytul + "|" + opis + "|" + czas + "|" + data + "|" + sciezka);
    }
}
