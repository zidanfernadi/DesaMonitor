package com.mycompany.desamonitor;

import java.util.ArrayList;

/**
 * Menyimpan dan mengelola data infrastruktur dan laporan (ArrayList).
 */
public class SistemDesa {

    private ArrayList<Infrastruktur> daftarInfrastruktur = new ArrayList<>();
    private ArrayList<Laporan> daftarLaporan = new ArrayList<>();
    private ArrayList<Admin> daftarAdmin = new ArrayList<>();

    public SistemDesa() {
        daftarAdmin.add(new Admin("admin", "admin123"));
        daftarAdmin.add(new Admin("petugas", "petugas123"));
        isiDataAwal();
    }

    // Data contoh agar aplikasi langsung punya isi saat dijalankan
    private void isiDataAwal() {
        daftarInfrastruktur.add(new Jalan("INF-001", "Jalan Dusun I", "Dusun I", -5.3702, 105.2411, "Belum diaspal", 850));
        daftarInfrastruktur.add(new Jalan("INF-002", "Jalan Raya Desa", "Dusun II", -5.3715, 105.2438, "Sudah diaspal", 1200));
        daftarInfrastruktur.add(new LampuJalan("INF-003", "Lampu Simpang Tiga", "Simpang Tiga Dusun II", -5.3719, 105.2442, "Mati", 4));
        daftarInfrastruktur.add(new LampuJalan("INF-004", "Lampu Balai Desa", "Depan Balai Desa", -5.3708, 105.2425, "Berfungsi", 6));
        daftarInfrastruktur.add(new Jembatan("INF-005", "Jembatan Sungai Kecil", "Batas Dusun I dan III", -5.3690, 105.2400, "Rusak ringan", "Beton"));
        daftarInfrastruktur.add(new Drainase("INF-006", "Drainase Jalan Mawar", "Dusun III", -5.3725, 105.2450, "Tersumbat", 300));

        daftarInfrastruktur.add(new FasilitasUmum("INF-007", "Masjid Al-Ikhlas", "Dusun II", -5.3712, 105.2430, "Baik", "Masjid"));
        daftarInfrastruktur.add(new FasilitasUmum("INF-008", "Lapangan Olahraga Desa", "Dusun III", -5.3722, 105.2418, "Rusak ringan", "Lapangan olahraga"));

        // Satu laporan contoh agar menu admin bisa langsung dicoba
        daftarLaporan.add(new Laporan("Budi Santoso", "Lampu Jalan", "Simpang Tiga Dusun II",
                "Lampu sudah mati sekitar dua minggu, jalan gelap saat malam.", ""));
    }

    // ---------- Admin ----------
    public boolean loginAdmin(String username, String password) {
        for (Admin admin : daftarAdmin) {
            if (admin.login(username, password)) {
                return true;
            }
        }
        return false;
    }

    // ---------- Infrastruktur ----------
    public ArrayList<Infrastruktur> getDaftarInfrastruktur() {
        return daftarInfrastruktur;
    }

    public String buatIdInfrastruktur() {
        return String.format("INF-%03d", daftarInfrastruktur.size() + 1);
    }

    public void tambahInfrastruktur(Infrastruktur infrastruktur) {
        daftarInfrastruktur.add(infrastruktur);
    }

    public Infrastruktur cariInfrastruktur(String id) {
        for (Infrastruktur inf : daftarInfrastruktur) {
            if (inf.getId().equalsIgnoreCase(id)) {
                return inf;
            }
        }
        return null;
    }

    // ---------- Laporan ----------
    public ArrayList<Laporan> getDaftarLaporan() {
        return daftarLaporan;
    }

    public void tambahLaporan(Laporan laporan) {
        daftarLaporan.add(laporan);
    }

    public Laporan cariLaporan(String kode) {
        for (Laporan lap : daftarLaporan) {
            if (lap.getKode().equalsIgnoreCase(kode)) {
                return lap;
            }
        }
        return null;
    }

    public int hitungLaporanByStatus(String status) {
        int jumlah = 0;
        for (Laporan lap : daftarLaporan) {
            if (lap.getStatus().equals(status)) {
                jumlah++;
            }
        }
        return jumlah;
    }
}
