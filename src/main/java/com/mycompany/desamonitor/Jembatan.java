package com.mycompany.desamonitor;

public class Jembatan extends Infrastruktur {

    private String bahan;

    public Jembatan(String id, String nama, String lokasi,
                    double latitude, double longitude, String kondisi, String bahan) {
        super(id, nama, lokasi, latitude, longitude, kondisi);
        setBahan(bahan);
    }

    @Override
    public String getJenis() {
        return "Jembatan";
    }

    @Override
    public char getSimbol() {
        return 'B';
    }

    @Override
    public String[] getOpsiKondisi() {
        return new String[]{"Baik", "Rusak ringan", "Rusak berat"};
    }

    public String getBahan() {
        return bahan;
    }

    public void setBahan(String bahan) {
        if (bahan == null || bahan.trim().isEmpty()) {
            throw new IllegalArgumentException("Bahan jembatan tidak boleh kosong.");
        }
        this.bahan = bahan.trim();
    }

    @Override
    public String getDetailTeks() {
        return super.getDetailTeks() + "\n" + "Bahan     : " + bahan;
    }
}
