package com.mycompany.desamonitor;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * DesaMonitor - Sistem Informasi Geografis untuk pemantauan dan
 * pelaporan kondisi infrastruktur desa (versi konsol).
 */
public class Main {

    private static Scanner input = new Scanner(System.in);
    private static SistemDesa sistem = new SistemDesa();

    public static void main(String[] args) {
        boolean jalan = true;

        while (jalan) {
            System.out.println("\n=== DESAMONITOR ===");
            System.out.println("Pantau dan laporkan kondisi infrastruktur desa");
            System.out.println("1. Masyarakat");
            System.out.println("2. Admin / Petugas Desa");
            System.out.println("0. Keluar");
            int pilihan = bacaAngka("Pilih menu", 0, 2);

            switch (pilihan) {
                case 1:
                    menuMasyarakat();
                    break;
                case 2:
                    if (loginAdmin()) {
                        menuAdmin();
                    }
                    break;
                case 0:
                    jalan = false;
                    System.out.println("Terima kasih telah menggunakan DesaMonitor.");
                    break;
            }
        }
        input.close();
    }

    // =====================================================
    // MENU MASYARAKAT
    // =====================================================
    private static void menuMasyarakat() {
        boolean kembali = false;

        while (!kembali) {
            System.out.println("\n--- MENU MASYARAKAT ---");
            System.out.println("1. Lihat peta infrastruktur desa");
            System.out.println("2. Lihat detail infrastruktur");
            System.out.println("3. Kirim laporan kerusakan");
            System.out.println("4. Cek status laporan");
            System.out.println("0. Kembali");
            int pilihan = bacaAngka("Pilih menu", 0, 4);

            switch (pilihan) {
                case 1:
                    tampilkanPeta();
                    break;
                case 2:
                    lihatDetailInfrastruktur();
                    break;
                case 3:
                    kirimLaporan();
                    break;
                case 4:
                    cekStatusLaporan();
                    break;
                case 0:
                    kembali = true;
                    break;
            }
        }
    }

    // Peta versi konsol: peta teks (posisi titik sesuai koordinat) dan daftar titik lokasi
    private static void tampilkanPeta() {
        ArrayList<Infrastruktur> daftar = sistem.getDaftarInfrastruktur();
        System.out.println("\n=== PETA INFRASTRUKTUR DESA ===");

        if (daftar.isEmpty()) {
            System.out.println("Belum ada data infrastruktur.");
            return;
        }

        int baris = 10;
        int kolom = 44;
        char[][] grid = new char[baris][kolom];
        for (int i = 0; i < baris; i++) {
            for (int j = 0; j < kolom; j++) {
                grid[i][j] = '.';
            }
        }

        // Mencari batas koordinat agar semua titik muat di dalam peta
        double minLat = daftar.get(0).getLatitude();
        double maxLat = minLat;
        double minLon = daftar.get(0).getLongitude();
        double maxLon = minLon;
        for (Infrastruktur inf : daftar) {
            minLat = Math.min(minLat, inf.getLatitude());
            maxLat = Math.max(maxLat, inf.getLatitude());
            minLon = Math.min(minLon, inf.getLongitude());
            maxLon = Math.max(maxLon, inf.getLongitude());
        }

        for (Infrastruktur inf : daftar) {
            int k = 0;
            int b = 0;
            if (maxLon > minLon) {
                k = (int) Math.round((inf.getLongitude() - minLon) / (maxLon - minLon) * (kolom - 1));
            }
            if (maxLat > minLat) {
                // Latitude makin besar = makin ke utara = baris makin atas
                b = (int) Math.round((maxLat - inf.getLatitude()) / (maxLat - minLat) * (baris - 1));
            }
            if (grid[b][k] == '.') {
                grid[b][k] = inf.getSimbol();
            } else {
                grid[b][k] = '*';
            }
        }

        System.out.println("U ^");
        for (int i = 0; i < baris; i++) {
            System.out.println("  " + new String(grid[i]));
        }
        System.out.println("J=Jalan  L=Lampu Jalan  B=Jembatan  D=Drainase  F=Fasilitas Umum  *=titik berdekatan");

        System.out.println();
        System.out.printf("%-8s %-15s %-24s %-22s %-14s%n", "ID", "Jenis", "Nama", "Koordinat", "Kondisi");
        for (Infrastruktur inf : daftar) {
            System.out.printf("%-8s %-15s %-24s %-22s %-14s%n",
                    inf.getId(), inf.getJenis(), inf.getNama(),
                    inf.getLatitude() + ", " + inf.getLongitude(), inf.getKondisi());
        }
    }

    private static void lihatDetailInfrastruktur() {
        tampilkanPeta();
        System.out.print("\nMasukkan ID infrastruktur (contoh INF-001): ");
        String id = input.nextLine().trim();

        Infrastruktur inf = sistem.cariInfrastruktur(id);
        if (inf == null) {
            System.out.println("Infrastruktur dengan ID tersebut tidak ditemukan.");
        } else {
            System.out.println();
            inf.tampilkanInfo();
        }
    }

    private static void kirimLaporan() {
        System.out.println("\n=== FORMULIR LAPORAN ===");
        String nama = bacaTeks("Nama pelapor");

        System.out.println("Jenis infrastruktur yang bermasalah:");
        for (int i = 0; i < Laporan.JENIS.length; i++) {
            System.out.println((i + 1) + ". " + Laporan.JENIS[i]);
        }
        int pilihJenis = bacaAngka("Pilih jenis", 1, Laporan.JENIS.length);
        String jenis = Laporan.JENIS[pilihJenis - 1];

        String lokasi = bacaTeks("Lokasi kejadian");
        String deskripsi = bacaTeks("Deskripsi masalah");
        
        Laporan laporan = new Laporan(nama, jenis, lokasi, deskripsi);
        sistem.tambahLaporan(laporan);

        System.out.println("\nLaporan berhasil dikirim dan menunggu pemeriksaan admin.");
        System.out.println("Simpan kode laporan Anda: " + laporan.getKode());
    }

    private static void cekStatusLaporan() {
        System.out.print("\nMasukkan kode laporan (contoh LP-001): ");
        String kode = input.nextLine().trim();

        Laporan laporan = sistem.cariLaporan(kode);
        if (laporan == null) {
            System.out.println("Laporan dengan kode tersebut tidak ditemukan.");
        } else {
            System.out.println();
            laporan.tampilkanInfo();
        }
    }

    // =====================================================
    // MENU ADMIN
    // =====================================================
    private static boolean loginAdmin() {
        System.out.println("\n--- LOGIN ADMIN ---");
        int percobaan = 0;

        while (percobaan < 3) {
            String username = bacaTeks("Username");
            String password = bacaTeks("Password");

            if (sistem.loginAdmin(username, password)) {
                System.out.println("Login berhasil. Selamat datang, " + username + "!");
                return true;
            }
            percobaan++;
            System.out.println("Username atau password salah. Sisa percobaan: " + (3 - percobaan));
        }
        System.out.println("Login gagal 3 kali. Kembali ke menu utama.");
        return false;
    }

    private static void menuAdmin() {
        boolean logout = false;

        while (!logout) {
            System.out.println("\n--- MENU ADMIN ---");
            System.out.println("1. Dashboard");
            System.out.println("2. Daftar infrastruktur");
            System.out.println("3. Tambah data infrastruktur");
            System.out.println("4. Ubah informasi infrastruktur");
            System.out.println("5. Perbarui kondisi infrastruktur");
            System.out.println("6. Daftar laporan masuk");
            System.out.println("7. Proses laporan");
            System.out.println("0. Logout");
            int pilihan = bacaAngka("Pilih menu", 0, 7);

            switch (pilihan) {
                case 1:
                    tampilkanDashboard();
                    break;
                case 2:
                    tampilkanPeta();
                    break;
                case 3:
                    tambahInfrastruktur();
                    break;
                case 4:
                    ubahInformasi();
                    break;
                case 5:
                    perbaruiKondisi();
                    break;
                case 6:
                    tampilkanDaftarLaporan();
                    break;
                case 7:
                    prosesLaporan();
                    break;
                case 0:
                    logout = true;
                    System.out.println("Anda telah logout.");
                    break;
            }
        }
    }

    private static void tampilkanDashboard() {
        System.out.println("\n=== DASHBOARD ADMIN ===");
        System.out.printf("%-26s: %d%n", "Infrastruktur terdata", sistem.getDaftarInfrastruktur().size());
        System.out.printf("%-26s: %d%n", "Total laporan masuk", sistem.getDaftarLaporan().size());
        System.out.printf("  %-24s: %d%n", Laporan.MENUNGGU, sistem.hitungLaporanByStatus(Laporan.MENUNGGU));
        System.out.printf("  %-24s: %d%n", Laporan.DITANGANI, sistem.hitungLaporanByStatus(Laporan.DITANGANI));
        System.out.printf("  %-24s: %d%n", Laporan.SELESAI, sistem.hitungLaporanByStatus(Laporan.SELESAI));
        System.out.printf("  %-24s: %d%n", Laporan.DITOLAK, sistem.hitungLaporanByStatus(Laporan.DITOLAK));
    }

    private static void tambahInfrastruktur() {
        System.out.println("\n=== TAMBAH DATA INFRASTRUKTUR ===");
        System.out.println("Jenis infrastruktur:");
        System.out.println("1. Jalan");
        System.out.println("2. Lampu Jalan");
        System.out.println("3. Jembatan");
        System.out.println("4. Drainase");
        System.out.println("5. Fasilitas Umum");
        int jenis = bacaAngka("Pilih jenis", 1, 5);

        String id = sistem.buatIdInfrastruktur();
        String nama = bacaTeks("Nama");
        String lokasi = bacaTeks("Lokasi");
        double lat = bacaDesimal("Latitude (contoh -5.3702)");
        double lon = bacaDesimal("Longitude (contoh 105.2411)");

        try {
            Infrastruktur baru = null;

            // Opsi kondisi dan data tambahan menyesuaikan jenis yang dipilih
            switch (jenis) {
                case 1:
                    Jalan jalanBaru = new Jalan(id, nama, lokasi, lat, lon, "Sudah diaspal",
                            bacaDesimal("Panjang jalan (meter)"));
                    jalanBaru.setKondisi(pilihKondisi(jalanBaru));
                    baru = jalanBaru;
                    break;
                case 2:
                    LampuJalan lampuBaru = new LampuJalan(id, nama, lokasi, lat, lon, "Berfungsi",
                            bacaAngka("Jumlah titik lampu", 0, 1000));
                    lampuBaru.setKondisi(pilihKondisi(lampuBaru));
                    baru = lampuBaru;
                    break;
                case 3:
                    Jembatan jembatanBaru = new Jembatan(id, nama, lokasi, lat, lon, "Baik",
                            bacaTeks("Bahan jembatan (contoh Beton/Kayu/Besi)"));
                    jembatanBaru.setKondisi(pilihKondisi(jembatanBaru));
                    baru = jembatanBaru;
                    break;
                case 4:
                    Drainase drainaseBaru = new Drainase(id, nama, lokasi, lat, lon, "Lancar",
                            bacaDesimal("Panjang drainase (meter)"));
                    drainaseBaru.setKondisi(pilihKondisi(drainaseBaru));
                    baru = drainaseBaru;
                    break;
                case 5:
                    FasilitasUmum fasilitasBaru = new FasilitasUmum(id, nama, lokasi, lat, lon, "Baik",
                            bacaTeks("Jenis fasilitas (contoh Masjid/Lapangan olahraga/Balai desa)"));
                    fasilitasBaru.setKondisi(pilihKondisi(fasilitasBaru));
                    baru = fasilitasBaru;
                    break;
            }


            sistem.tambahInfrastruktur(baru);
            System.out.println("\nData infrastruktur berhasil ditambahkan dengan ID " + id + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("Data tidak valid: " + e.getMessage());
        }
    }

    // Menampilkan opsi kondisi sesuai jenis infrastruktur, lalu mengembalikan pilihan
    private static String pilihKondisi(Infrastruktur inf) {
        String[] opsi = inf.getOpsiKondisi();
        System.out.println("Kondisi " + inf.getJenis() + ":");
        for (int i = 0; i < opsi.length; i++) {
            System.out.println((i + 1) + ". " + opsi[i]);
        }
        int pilihan = bacaAngka("Pilih kondisi", 1, opsi.length);
        return opsi[pilihan - 1];
    }

    private static void ubahInformasi() {
        tampilkanPeta();
        System.out.print("\nMasukkan ID infrastruktur yang akan diubah: ");
        String id = input.nextLine().trim();

        Infrastruktur inf = sistem.cariInfrastruktur(id);
        if (inf == null) {
            System.out.println("Infrastruktur dengan ID tersebut tidak ditemukan.");
            return;
        }

        System.out.println();
        inf.tampilkanInfo();
        System.out.println("\nBagian yang diubah:");
        System.out.println("1. Nama");
        System.out.println("2. Lokasi");
        System.out.println("0. Batal");
        int pilihan = bacaAngka("Pilih bagian", 0, 3);

        try {
            switch (pilihan) {
                case 1:
                    inf.setNama(bacaTeks("Nama baru"));
                    System.out.println("Nama berhasil diubah.");
                    break;
                case 2:
                    inf.setLokasi(bacaTeks("Lokasi baru"));
                    System.out.println("Lokasi berhasil diubah.");
                    break;
               
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Data tidak valid: " + e.getMessage());
        }
    }

    private static void perbaruiKondisi() {
        tampilkanPeta();
        System.out.print("\nMasukkan ID infrastruktur yang akan diperbarui: ");
        String id = input.nextLine().trim();

        Infrastruktur inf = sistem.cariInfrastruktur(id);
        if (inf == null) {
            System.out.println("Infrastruktur dengan ID tersebut tidak ditemukan.");
            return;
        }

        System.out.println("\n" + inf.getNama() + " (" + inf.getJenis() + ")");
        System.out.println("Kondisi saat ini: " + inf.getKondisi());
        String kondisiBaru = pilihKondisi(inf);

        if (kondisiBaru.equals(inf.getKondisi())) {
            System.out.println("Kondisi tidak berubah.");
        } else {
            inf.setKondisi(kondisiBaru);
            System.out.println("Kondisi berhasil diperbarui menjadi: " + inf.getKondisi());
        }
    }

    private static void tampilkanDaftarLaporan() {
        System.out.println("\n=== DAFTAR LAPORAN MASUK ===");
        if (sistem.getDaftarLaporan().isEmpty()) {
            System.out.println("Belum ada laporan masuk.");
            return;
        }
        System.out.printf("%-8s %-16s %-14s %-22s%n", "Kode", "Pelapor", "Jenis", "Status");
        for (Laporan lap : sistem.getDaftarLaporan()) {
            System.out.printf("%-8s %-16s %-14s %-22s%n",
                    lap.getKode(), lap.getNamaPelapor(), lap.getJenis(), lap.getStatus());
        }
    }

    private static void prosesLaporan() {
        tampilkanDaftarLaporan();
        if (sistem.getDaftarLaporan().isEmpty()) {
            return;
        }
        System.out.print("\nMasukkan kode laporan yang akan diproses: ");
        String kode = input.nextLine().trim();

        Laporan laporan = sistem.cariLaporan(kode);
        if (laporan == null) {
            System.out.println("Laporan dengan kode tersebut tidak ditemukan.");
            return;
        }

        System.out.println();
        laporan.tampilkanInfo();

        // Opsi yang tampil menyesuaikan status laporan saat ini
        String status = laporan.getStatus();
        if (status.equals(Laporan.MENUNGGU)) {
            System.out.println("\n1. Terima laporan (ubah ke '" + Laporan.DITANGANI + "')");
            System.out.println("2. Tolak laporan");
            System.out.println("0. Batal");
            int pilihan = bacaAngka("Pilih tindakan", 0, 2);

            if (pilihan == 1) {
                System.out.print("Keterangan untuk masyarakat (Enter untuk lewati): ");
                laporan.terima(input.nextLine());
                System.out.println("Laporan diterima. Status: " + laporan.getStatus());
            } else if (pilihan == 2) {
                laporan.tolak(bacaTeks("Alasan penolakan"));
                System.out.println("Laporan ditolak. Status: " + laporan.getStatus());
            }
        } else if (status.equals(Laporan.DITANGANI)) {
            System.out.println("\n1. Tandai laporan selesai");
            System.out.println("0. Batal");
            int pilihan = bacaAngka("Pilih tindakan", 0, 1);

            if (pilihan == 1) {
                System.out.print("Keterangan penyelesaian (Enter untuk lewati): ");
                laporan.selesai(input.nextLine());
                System.out.println("Laporan selesai ditangani. Status: " + laporan.getStatus());
                System.out.println("Jangan lupa perbarui kondisi infrastruktur lewat menu 5.");
            }
        } else {
            System.out.println("\nLaporan berstatus '" + status + "' sudah final dan tidak dapat diubah.");
        }
    }

    // =====================================================
    // METHOD BANTU INPUT (Scanner + try-catch)
    // =====================================================
    private static int bacaAngka(String pesan, int min, int max) {
        while (true) {
            System.out.print(pesan + " [" + min + "-" + max + "]: ");
            try {
                int angka = Integer.parseInt(input.nextLine().trim());
                if (angka >= min && angka <= max) {
                    return angka;
                }
                System.out.println("Pilihan di luar rentang, coba lagi.");
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa angka, coba lagi.");
            }
        }
    }

    private static double bacaDesimal(String pesan) {
        while (true) {
            System.out.print(pesan + ": ");
            try {
                return Double.parseDouble(input.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa angka, coba lagi.");
            }
        }
    }

    private static String bacaTeks(String pesan) {
        while (true) {
            System.out.print(pesan + ": ");
            String teks = input.nextLine().trim();
            if (!teks.isEmpty()) {
                return teks;
            }
            System.out.println("Input tidak boleh kosong, coba lagi.");
        }
    }
}
