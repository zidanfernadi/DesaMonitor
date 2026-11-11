package com.mycompany.desamonitor;

public class Drainase extends Infrastruktur {

    private double panjangMeter;

    public Drainase(String id, String nama, String lokasi,
                    double latitude, double longitude, String kondisi, double panjangMeter) {
        super(id, nama, lokasi, latitude, longitude, kondisi);
        setPanjangMeter(panjangMeter);
    }

    @Override
    public String getJenis() {
        return "Drainase";
    }

    @Override
    public char getSimbol() {
        return 'D';
    }

    @Override
    public String[] getOpsiKondisi() {
        return new String[]{"Lancar", "Tersumbat", "Rusak"};
    }

    public double getPanjangMeter() {
        return panjangMeter;
    }

    public void setPanjangMeter(double panjangMeter) {
        if (panjangMeter <= 0) {
            throw new IllegalArgumentException("Panjang drainase harus lebih dari 0.");
        }
        this.panjangMeter = panjangMeter;
    }

    @Override
    public void tampilkanInfo() {
        super.tampilkanInfo();
        System.out.println("Panjang   : " + panjangMeter + " meter");
    }
}
