package com.mycompany.desamonitor;

/**
 * Superclass untuk semua jenis infrastruktur desa.
 * Field dibuat private (Encapsulation), diakses lewat getter dan setter.
 */
public class Infrastruktur {

    private String id;
    private String nama;
    private String lokasi;
    private double latitude;
    private double longitude;
    private String kondisi;
    private String foto = "-";

    public Infrastruktur(String id, String nama, String lokasi,
                         double latitude, double longitude, String kondisi) {
        this.id = id;
        setNama(nama);
        setLokasi(lokasi);
        setLatitude(latitude);
        setLongitude(longitude);
        setKondisi(kondisi);
    }

    // Jenis infrastruktur, akan di-override oleh subclass
    public String getJenis() {
        return "Infrastruktur";
    }

    // Simbol untuk peta teks, akan di-override oleh subclass
    public char getSimbol() {
        return '?';
    }

    // Pilihan kondisi yang valid, akan di-override oleh subclass
    public String[] getOpsiKondisi() {
        return new String[]{"Baik", "Rusak"};
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getLokasi() {
        return lokasi;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getKondisi() {
        return kondisi;
    }

    public String getFoto() {
        return foto;
    }

    // Setter dengan validasi
    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama infrastruktur tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public void setLokasi(String lokasi) {
        if (lokasi == null || lokasi.trim().isEmpty()) {
            throw new IllegalArgumentException("Lokasi tidak boleh kosong.");
        }
        this.lokasi = lokasi.trim();
    }

    public void setLatitude(double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude harus antara -90 dan 90.");
        }
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude harus antara -180 dan 180.");
        }
        this.longitude = longitude;
    }

    public void setKondisi(String kondisi) {
        // Kondisi hanya boleh salah satu dari opsi milik jenis infrastrukturnya
        for (String opsi : getOpsiKondisi()) {
            if (opsi.equals(kondisi)) {
                this.kondisi = kondisi;
                return;
            }
        }
        throw new IllegalArgumentException("Kondisi '" + kondisi + "' tidak valid untuk " + getJenis() + ".");
    }

    // Foto bersifat opsional, kosong berarti tidak ada foto
    public void setFoto(String foto) {
        if (foto == null || foto.trim().isEmpty()) {
            this.foto = "-";
        } else {
            this.foto = foto.trim();
        }
    }

    public void tampilkanInfo() {
        System.out.println("ID        : " + id);
        System.out.println("Jenis     : " + getJenis());
        System.out.println("Nama      : " + nama);
        System.out.println("Lokasi    : " + lokasi);
        System.out.println("Koordinat : " + latitude + ", " + longitude);
        System.out.println("Kondisi   : " + kondisi);
        System.out.println("Foto      : " + foto);
    }
}
