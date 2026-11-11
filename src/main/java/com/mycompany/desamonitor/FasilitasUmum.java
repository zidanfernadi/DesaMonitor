package com.mycompany.desamonitor;

public class FasilitasUmum extends Infrastruktur {

    // Contoh: Masjid, Lapangan olahraga, Balai desa, Posyandu
    private String jenisFasilitas;

    public FasilitasUmum(String id, String nama, String lokasi,
                         double latitude, double longitude, String kondisi, String jenisFasilitas) {
        super(id, nama, lokasi, latitude, longitude, kondisi);
        setJenisFasilitas(jenisFasilitas);
    }

    @Override
    public String getJenis() {
        return "Fasilitas Umum";
    }

    @Override
    public char getSimbol() {
        return 'F';
    }

    @Override
    public String[] getOpsiKondisi() {
        return new String[]{"Baik", "Rusak ringan", "Rusak berat"};
    }

    public String getJenisFasilitas() {
        return jenisFasilitas;
    }

    public void setJenisFasilitas(String jenisFasilitas) {
        if (jenisFasilitas == null || jenisFasilitas.trim().isEmpty()) {
            throw new IllegalArgumentException("Jenis fasilitas tidak boleh kosong.");
        }
        this.jenisFasilitas = jenisFasilitas.trim();
    }

    @Override
    public void tampilkanInfo() {
        super.tampilkanInfo();
        System.out.println("Fasilitas : " + jenisFasilitas);
    }
}
