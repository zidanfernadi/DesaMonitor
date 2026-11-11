package com.mycompany.desamonitor;

public class LampuJalan extends Infrastruktur {

    private int jumlahTitik;

    public LampuJalan(String id, String nama, String lokasi,
                      double latitude, double longitude, String kondisi, int jumlahTitik) {
        super(id, nama, lokasi, latitude, longitude, kondisi);
        setJumlahTitik(jumlahTitik);
    }

    @Override
    public String getJenis() {
        return "Lampu Jalan";
    }

    @Override
    public char getSimbol() {
        return 'L';
    }

    @Override
    public String[] getOpsiKondisi() {
        return new String[]{"Berfungsi", "Mati", "Belum tersedia"};
    }

    public int getJumlahTitik() {
        return jumlahTitik;
    }

    public void setJumlahTitik(int jumlahTitik) {
        if (jumlahTitik < 0) {
            throw new IllegalArgumentException("Jumlah titik lampu tidak boleh negatif.");
        }
        this.jumlahTitik = jumlahTitik;
    }

    @Override
    public void tampilkanInfo() {
        super.tampilkanInfo();
        System.out.println("Jml Titik : " + jumlahTitik + " lampu");
    }
}
