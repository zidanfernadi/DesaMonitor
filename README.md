# DesaMonitor

Sistem Informasi Geografis untuk pemantauan dan pelaporan kondisi infrastruktur desa.
Proyek akhir mata kuliah Pemrograman Berorientasi Objek (PBO) - Ilmu Komputer, Universitas Lampung.

## Cara menjalankan
Jalankan class `com.mycompany.desamonitor.Main` (aplikasi konsol, input memakai Scanner).

Akun admin contoh: `admin` / `admin123` dan `petugas` / `petugas123`.

## Fitur
- Masyarakat: lihat peta/daftar infrastruktur, lihat detail, kirim laporan, cek status laporan.
- Admin: login, dashboard, tambah data infrastruktur (jalan, lampu jalan, jembatan, drainase, fasilitas umum), ubah informasi, perbarui kondisi, proses laporan (terima / tolak dengan alasan / selesai).

## Materi PBO yang diterapkan
| Materi | Penerapan |
|---|---|
| Scanner & Control Flow (Pert. 2) | `Main` memakai Scanner, `switch`, `while`, `if/else`, `for` |
| Class & Object (Pert. 3) | `Infrastruktur`, `Laporan`, `Admin`, `SistemDesa`, objek dibuat di `Main` dan `SistemDesa` |
| Encapsulation (Pert. 4) | Field `private`, getter/setter dengan validasi, `static` pada `Laporan` |
| Inheritance (Pert. 6) | `Jalan`, `LampuJalan`, `Jembatan`, `Drainase` extends `Infrastruktur`, memakai `super` dan `@Override` |
| Polymorphism (Pert. 7) | `getOpsiKondisi()` dan `tampilkanInfo()` di-override tiap subclass |
| Exception & ArrayList (Pert. 10) | `try-catch` pada input, `throw` pada setter, `ArrayList` di `SistemDesa` |
