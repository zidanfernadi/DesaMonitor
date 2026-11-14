package com.mycompany.desamonitor;

/**
 * Laporan kerusakan infrastruktur dari masyarakat.
 * Status: Menunggu pemeriksaan -> Sedang ditangani -> Selesai
 * atau Menunggu pemeriksaan -> Ditolak (dengan alasan).
 */
public class Laporan {

    // Konstanta status (static final)
    public static final String MENUNGGU = "Menunggu pemeriksaan";
    public static final String DITANGANI = "Sedang ditangani";
    public static final String SELESAI = "Selesai";
    public static final String DITOLAK = "Ditolak";

    // Pilihan jenis infrastruktur pada formulir laporan
    public static final String[] JENIS = {
        "Jalan", "Lampu Jalan", "Jembatan", "Drainase", "Fasilitas Umum"
    };

    // Penghitung untuk membuat kode laporan otomatis
    private static int jumlahLaporan = 0;

    private String kode;
    private String namaPelapor;
    private String jenis;
    private String lokasi;
    private String deskripsi;
    private String status;
    private String keterangan;

    public Laporan(String namaPelapor, String jenis, String lokasi, String deskripsi) {
        jumlahLaporan++;
        this.kode = String.format("LP-%03d", jumlahLaporan);
        setNamaPelapor(namaPelapor);
        this.jenis = jenis;
        setLokasi(lokasi);
        setDeskripsi(deskripsi);
        this.status = MENUNGGU;
        this.keterangan = "";
    }

    public String getKode() {
        return kode;
    }

    public String getNamaPelapor() {
        return namaPelapor;
    }

    public String getJenis() {
        return jenis;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getStatus() {
        return status;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setNamaPelapor(String namaPelapor) {
        if (namaPelapor == null || namaPelapor.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama pelapor tidak boleh kosong.");
        }
        this.namaPelapor = namaPelapor.trim();
    }

    public void setLokasi(String lokasi) {
        if (lokasi == null || lokasi.trim().isEmpty()) {
            throw new IllegalArgumentException("Lokasi tidak boleh kosong.");
        }
        this.lokasi = lokasi.trim();
    }

    public void setDeskripsi(String deskripsi) {
        if (deskripsi == null || deskripsi.trim().isEmpty()) {
            throw new IllegalArgumentException("Deskripsi masalah tidak boleh kosong.");
        }
        this.deskripsi = deskripsi.trim();
    }

    // Perubahan status hanya boleh mengikuti alur yang benar
    public void terima(String keterangan) {
        if (!status.equals(MENUNGGU)) {
            throw new IllegalStateException("Hanya laporan berstatus '" + MENUNGGU + "' yang bisa diterima.");
        }
        status = DITANGANI;
        this.keterangan = (keterangan == null) ? "" : keterangan.trim();
    }

    public void tolak(String alasan) {
        if (!status.equals(MENUNGGU)) {
            throw new IllegalStateException("Hanya laporan berstatus '" + MENUNGGU + "' yang bisa ditolak.");
        }
        if (alasan == null || alasan.trim().isEmpty()) {
            throw new IllegalArgumentException("Alasan penolakan wajib diisi.");
        }
        status = DITOLAK;
        keterangan = alasan.trim();
    }

    public void selesai(String keterangan) {
        if (!status.equals(DITANGANI)) {
            throw new IllegalStateException("Hanya laporan berstatus '" + DITANGANI + "' yang bisa diselesaikan.");
        }
        status = SELESAI;
        if (keterangan != null && !keterangan.trim().isEmpty()) {
            this.keterangan = keterangan.trim();
        }
    }

    public String getDetailTeks() {
        String teks = "Kode      : " + kode
                + "\nPelapor   : " + namaPelapor
                + "\nJenis     : " + jenis
                + "\nLokasi    : " + lokasi
                + "\nMasalah   : " + deskripsi
                + "\nStatus    : " + status;
        if (!keterangan.isEmpty()) {
            teks += "\nKeterangan: " + keterangan;
        }
        return teks;
    }

    public void tampilkanInfo() {
        System.out.println(getDetailTeks());
    }
}
