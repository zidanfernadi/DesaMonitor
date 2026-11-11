package com.mycompany.desamonitor;

public class Jalan extends Infrastruktur {

    private double panjangMeter;

    public Jalan(String id, String nama, String lokasi,
                 double latitude, double longitude, String kondisi, double panjangMeter) {
        super(id, nama, lokasi, latitude, longitude, kondisi);
        setPanjangMeter(panjangMeter);
    }

    @Override
    public String getJenis() {
        return "Jalan";
    }

    @Override
    public char getSimbol() {
        return 'J';
    }

    @Override
    public String[] getOpsiKondisi() {
        return new String[]{"Sudah diaspal", "Belum diaspal", "Rusak"};
    }

    public double getPanjangMeter() {
        return panjangMeter;
    }

    public void setPanjangMeter(double panjangMeter) {
        if (panjangMeter <= 0) {
            throw new IllegalArgumentException("Panjang jalan harus lebih dari 0.");
        }
        this.panjangMeter = panjangMeter;
    }

    @Override
    public void tampilkanInfo() {
        super.tampilkanInfo();
        System.out.println("Panjang   : " + panjangMeter + " meter");
    }
}
