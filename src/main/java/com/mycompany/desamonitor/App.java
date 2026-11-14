package com.mycompany.desamonitor;

import java.util.Optional;
import java.util.function.Function;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * DesaMonitor versi GUI (JavaFX, semua tampilan dibuat lewat kode).
 * Memakai class model yang sama dengan versi konsol.
 */
public class App extends Application {

    private static final double LEBAR_PETA = 360;
    private static final double TINGGI_PETA = 300;

    private SistemDesa sistem = new SistemDesa();
    private ObservableList<Infrastruktur> dataInfra;
    private ObservableList<Laporan> dataLaporan;

    // Komponen yang dipakai di lebih dari satu method
    private TableView<Infrastruktur> tabelInfra = new TableView<>();
    private TableView<Laporan> tabelLaporan = new TableView<>();
    private TextArea areaDetailInfra = new TextArea();
    private TextArea areaDetailLaporan = new TextArea();
    private Pane petaPane = new Pane();
    private Label lblDashboard = new Label();
    private Button btnTerima = new Button("Terima");
    private Button btnTolak = new Button("Tolak");
    private Button btnSelesai = new Button("Tandai Selesai");

    private Tab tabAdmin = new Tab("Admin");
    private Node formLogin;
    private Node panelAdmin;

    @Override
    public void start(Stage stage) {
        dataInfra = FXCollections.observableArrayList(sistem.getDaftarInfrastruktur());
        dataLaporan = FXCollections.observableArrayList(sistem.getDaftarLaporan());

        formLogin = buatFormLogin();
        tabAdmin.setClosable(false);
        tabAdmin.setContent(formLogin);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(buatTabInfrastruktur(), buatTabLapor(), buatTabStatus(), tabAdmin);

        stage.setTitle("DesaMonitor - Pemantauan Infrastruktur Desa");
        stage.setScene(new Scene(tabPane, 840, 580));
        stage.show();
    }

    // TAB 1: PETA DAN DAFTAR INFRASTRUKTUR (masyarakat)
    private Tab buatTabInfrastruktur() {
        tabelInfra.getColumns().add(kolomInfra("ID", 70, inf -> inf.getId()));
        tabelInfra.getColumns().add(kolomInfra("Jenis", 100, inf -> inf.getJenis()));
        tabelInfra.getColumns().add(kolomInfra("Nama", 150, inf -> inf.getNama()));
        tabelInfra.getColumns().add(kolomInfra("Kondisi", 100, inf -> inf.getKondisi()));
        tabelInfra.setItems(dataInfra);

        areaDetailInfra.setEditable(false);
        areaDetailInfra.setPrefRowCount(8);

        // Saat satu baris dipilih, tampilkan detailnya
        tabelInfra.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> {
            if (baru != null) {
                areaDetailInfra.setText(baru.getDetailTeks());
            }
        });

        petaPane.setPrefSize(LEBAR_PETA, TINGGI_PETA);
        petaPane.setMinSize(LEBAR_PETA, TINGGI_PETA);
        petaPane.setStyle("-fx-background-color: #eef3e6; -fx-border-color: #888888;");
        gambarPeta();

        VBox kiri = new VBox(8);
        kiri.getChildren().addAll(
                new Label("Peta Desa"),
                petaPane,
                new Label("Hijau = baik, Oranye = perlu perhatian, Merah = rusak/mati"));

        VBox kanan = new VBox(8);
        kanan.getChildren().addAll(
                new Label("Daftar Infrastruktur (klik baris atau titik di peta)"),
                tabelInfra,
                new Label("Detail"),
                areaDetailInfra);

        HBox isi = new HBox(15);
        isi.setPadding(new Insets(15));
        isi.getChildren().addAll(kiri, kanan);

        Tab tab = new Tab("Peta Infrastruktur");
        tab.setClosable(false);
        tab.setContent(isi);
        return tab;
    }

    // Setiap infrastruktur digambar sebagai titik sesuai koordinatnya
    private void gambarPeta() {
        petaPane.getChildren().clear();
        if (dataInfra.isEmpty()) {
            return;
        }

        double minLat = dataInfra.get(0).getLatitude();
        double maxLat = minLat;
        double minLon = dataInfra.get(0).getLongitude();
        double maxLon = minLon;
        for (Infrastruktur inf : dataInfra) {
            minLat = Math.min(minLat, inf.getLatitude());
            maxLat = Math.max(maxLat, inf.getLatitude());
            minLon = Math.min(minLon, inf.getLongitude());
            maxLon = Math.max(maxLon, inf.getLongitude());
        }

        for (Infrastruktur inf : dataInfra) {
            double x = LEBAR_PETA / 2;
            double y = TINGGI_PETA / 2;
            if (maxLon > minLon) {
                x = 20 + (inf.getLongitude() - minLon) / (maxLon - minLon) * (LEBAR_PETA - 40);
            }
            if (maxLat > minLat) {
                // Latitude makin besar = makin ke utara = posisi y makin kecil
                y = 20 + (maxLat - inf.getLatitude()) / (maxLat - minLat) * (TINGGI_PETA - 40);
            }

            Circle titik = new Circle(x, y, 8, warnaKondisi(inf));
            titik.setStroke(Color.BLACK);
            titik.setOnMouseClicked(e -> tabelInfra.getSelectionModel().select(inf));
            petaPane.getChildren().add(titik);
        }
    }

    private Color warnaKondisi(Infrastruktur inf) {
        String k = inf.getKondisi();
        if (k.equals("Baik") || k.equals("Sudah diaspal") || k.equals("Berfungsi") || k.equals("Lancar")) {
            return Color.LIMEGREEN;
        } else if (k.equals("Rusak ringan") || k.equals("Belum diaspal")
                || k.equals("Tersumbat") || k.equals("Belum tersedia")) {
            return Color.ORANGE;
        }
        return Color.RED;
    }

    // TAB 2: KIRIM LAPORAN (masyarakat)
    private Tab buatTabLapor() {
        TextField txtNama = new TextField();
        ComboBox<String> cmbJenis = new ComboBox<>();
        cmbJenis.getItems().addAll(Laporan.JENIS);
        cmbJenis.setPromptText("Pilih jenis");
        TextField txtLokasi = new TextField();
        TextArea txtDeskripsi = new TextArea();
        txtDeskripsi.setPrefRowCount(4);
        Button btnKirim = new Button("Kirim Laporan");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.add(new Label("Nama pelapor"), 0, 0);
        form.add(txtNama, 1, 0);
        form.add(new Label("Jenis infrastruktur"), 0, 1);
        form.add(cmbJenis, 1, 1);
        form.add(new Label("Lokasi kejadian"), 0, 2);
        form.add(txtLokasi, 1, 2);
        form.add(new Label("Deskripsi masalah"), 0, 3);
        form.add(txtDeskripsi, 1, 3);
        form.add(btnKirim, 1, 5);

        btnKirim.setOnAction(e -> {
            try {
                if (cmbJenis.getValue() == null) {
                    throw new IllegalArgumentException("Pilih jenis infrastruktur terlebih dahulu.");
                }
                Laporan laporan = new Laporan(txtNama.getText(), cmbJenis.getValue(),
                        txtLokasi.getText(), txtDeskripsi.getText());
                sistem.tambahLaporan(laporan);
                dataLaporan.add(laporan);
                perbaruiDashboard();

                tampilAlert(Alert.AlertType.INFORMATION, "Laporan Terkirim",
                        "Laporan berhasil dikirim.\nSimpan kode laporan Anda: " + laporan.getKode());
                txtNama.clear();
                cmbJenis.setValue(null);
                txtLokasi.clear();
                txtDeskripsi.clear();
            } catch (IllegalArgumentException ex) {
                tampilAlert(Alert.AlertType.WARNING, "Data Belum Lengkap", ex.getMessage());
            }
        });

        Tab tab = new Tab("Kirim Laporan");
        tab.setClosable(false);
        tab.setContent(form);
        return tab;
    }

    // TAB 3: CEK STATUS LAPORAN (masyarakat)
    private Tab buatTabStatus() {
        TextField txtKode = new TextField();
        txtKode.setPromptText("Contoh: LP-001");
        Button btnCek = new Button("Cek Status");
        TextArea areaHasil = new TextArea();
        areaHasil.setEditable(false);
        areaHasil.setPrefRowCount(10);

        btnCek.setOnAction(e -> {
            String kode = txtKode.getText().trim();
            if (kode.isEmpty()) {
                tampilAlert(Alert.AlertType.WARNING, "Kode Kosong", "Masukkan kode laporan terlebih dahulu.");
                return;
            }
            Laporan laporan = sistem.cariLaporan(kode);
            if (laporan == null) {
                areaHasil.clear();
                tampilAlert(Alert.AlertType.ERROR, "Tidak Ditemukan", "Laporan dengan kode tersebut tidak ditemukan.");
            } else {
                areaHasil.setText(laporan.getDetailTeks());
            }
        });

        HBox baris = new HBox(10);
        baris.getChildren().addAll(new Label("Kode laporan"), txtKode, btnCek);

        VBox isi = new VBox(12);
        isi.setPadding(new Insets(20));
        isi.getChildren().addAll(baris, areaHasil);

        Tab tab = new Tab("Status Laporan");
        tab.setClosable(false);
        tab.setContent(isi);
        return tab;
    }

    // TAB 4: ADMIN
    private Node buatFormLogin() {
        TextField txtUser = new TextField();
        PasswordField txtPass = new PasswordField();
        Button btnLogin = new Button("Masuk");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(30));
        form.add(new Label("Login Admin / Petugas Desa"), 0, 0, 2, 1);
        form.add(new Label("Username"), 0, 1);
        form.add(txtUser, 1, 1);
        form.add(new Label("Password"), 0, 2);
        form.add(txtPass, 1, 2);
        form.add(btnLogin, 1, 3);

        btnLogin.setOnAction(e -> {
            if (txtUser.getText().trim().isEmpty() || txtPass.getText().isEmpty()) {
                tampilAlert(Alert.AlertType.WARNING, "Login", "Username dan password harus diisi.");
            } else if (sistem.loginAdmin(txtUser.getText().trim(), txtPass.getText())) {
                txtUser.clear();
                txtPass.clear();
                if (panelAdmin == null) {
                    panelAdmin = buatPanelAdmin();
                }
                perbaruiDashboard();
                tabAdmin.setContent(panelAdmin);
            } else {
                tampilAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau password salah.");
            }
        });
        return form;
    }

    private Node buatPanelAdmin() {
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> tabAdmin.setContent(formLogin));

        TabPane tabDalam = new TabPane();
        tabDalam.getTabs().addAll(buatTabProsesLaporan(), buatTabTambahInfrastruktur());
        for (Tab t : tabDalam.getTabs()) {
            t.setClosable(false);
        }

        HBox atas = new HBox(15);
        atas.getChildren().addAll(new Label("Panel Admin"), btnLogout);

        VBox isi = new VBox(10);
        isi.setPadding(new Insets(10));
        isi.getChildren().addAll(atas, lblDashboard, tabDalam);
        return isi;
    }

    private void perbaruiDashboard() {
        lblDashboard.setText("Infrastruktur terdata: " + dataInfra.size()
                + "  |  Laporan masuk: " + dataLaporan.size()
                + "  |  Menunggu: " + sistem.hitungLaporanByStatus(Laporan.MENUNGGU)
                + "  |  Ditangani: " + sistem.hitungLaporanByStatus(Laporan.DITANGANI)
                + "  |  Selesai: " + sistem.hitungLaporanByStatus(Laporan.SELESAI)
                + "  |  Ditolak: " + sistem.hitungLaporanByStatus(Laporan.DITOLAK));
    }

    // Proses laporan
    private Tab buatTabProsesLaporan() {
        tabelLaporan.getColumns().add(kolomLaporan("Kode", 70, lap -> lap.getKode()));
        tabelLaporan.getColumns().add(kolomLaporan("Pelapor", 120, lap -> lap.getNamaPelapor()));
        tabelLaporan.getColumns().add(kolomLaporan("Jenis", 100, lap -> lap.getJenis()));
        tabelLaporan.getColumns().add(kolomLaporan("Lokasi", 160, lap -> lap.getLokasi()));
        tabelLaporan.getColumns().add(kolomLaporan("Status", 140, lap -> lap.getStatus()));
        tabelLaporan.setItems(dataLaporan);

        areaDetailLaporan.setEditable(false);
        areaDetailLaporan.setPrefRowCount(8);

        // Tombol aktif sesuai status laporan yang dipilih
        btnTerima.setDisable(true);
        btnTolak.setDisable(true);
        btnSelesai.setDisable(true);
        tabelLaporan.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> {
            if (baru != null) {
                areaDetailLaporan.setText(baru.getDetailTeks());
            }
            aturTombolLaporan(baru);
        });

        btnTerima.setOnAction(e -> {
            Laporan laporan = tabelLaporan.getSelectionModel().getSelectedItem();
            Optional<String> hasil = mintaTeks("Terima Laporan", "Keterangan untuk masyarakat (boleh kosong):");
            if (laporan != null && hasil.isPresent()) {
                laporan.terima(hasil.get());
                segarkanLaporan(laporan);
            }
        });

        btnTolak.setOnAction(e -> {
            Laporan laporan = tabelLaporan.getSelectionModel().getSelectedItem();
            Optional<String> hasil = mintaTeks("Tolak Laporan", "Alasan penolakan:");
            if (laporan != null && hasil.isPresent()) {
                try {
                    laporan.tolak(hasil.get());
                    segarkanLaporan(laporan);
                } catch (IllegalArgumentException ex) {
                    tampilAlert(Alert.AlertType.WARNING, "Alasan Kosong", ex.getMessage());
                }
            }
        });

        btnSelesai.setOnAction(e -> {
            Laporan laporan = tabelLaporan.getSelectionModel().getSelectedItem();
            Optional<String> hasil = mintaTeks("Selesaikan Laporan", "Keterangan penyelesaian (boleh kosong):");
            if (laporan != null && hasil.isPresent()) {
                laporan.selesai(hasil.get());
                segarkanLaporan(laporan);
            }
        });

        HBox tombol = new HBox(10);
        tombol.getChildren().addAll(btnTerima, btnTolak, btnSelesai);

        VBox isi = new VBox(10);
        isi.setPadding(new Insets(10));
        isi.getChildren().addAll(tabelLaporan, areaDetailLaporan, tombol);

        Tab tab = new Tab("Proses Laporan");
        tab.setContent(isi);
        return tab;
    }

    // Terima/Tolak hanya untuk status Menunggu, Selesai hanya untuk status Sedang ditangani
    private void aturTombolLaporan(Laporan laporan) {
        boolean menunggu = laporan != null && laporan.getStatus().equals(Laporan.MENUNGGU);
        boolean ditangani = laporan != null && laporan.getStatus().equals(Laporan.DITANGANI);
        btnTerima.setDisable(!menunggu);
        btnTolak.setDisable(!menunggu);
        btnSelesai.setDisable(!ditangani);
    }

    private void segarkanLaporan(Laporan laporan) {
        tabelLaporan.refresh();
        areaDetailLaporan.setText(laporan.getDetailTeks());
        aturTombolLaporan(laporan);
        perbaruiDashboard();
    }

    // Tambah infrastruktur
    private Tab buatTabTambahInfrastruktur() {
        String[] jenis = {"Jalan", "Lampu Jalan", "Jembatan", "Drainase", "Fasilitas Umum"};
        String[] labelEkstra = {"Panjang jalan (meter)", "Jumlah titik lampu", "Bahan jembatan",
            "Panjang drainase (meter)", "Jenis fasilitas (Masjid, dll.)"};

        ComboBox<String> cmbJenis = new ComboBox<>();
        cmbJenis.getItems().addAll(jenis);
        cmbJenis.setPromptText("Pilih jenis");
        TextField txtNama = new TextField();
        TextField txtLokasi = new TextField();
        TextField txtLat = new TextField();
        txtLat.setPromptText("Contoh: -5.3702");
        TextField txtLon = new TextField();
        txtLon.setPromptText("Contoh: 105.2411");
        Label lblEkstra = new Label("Data tambahan");
        TextField txtEkstra = new TextField();
        ComboBox<String> cmbKondisi = new ComboBox<>();
        cmbKondisi.setPromptText("Pilih jenis dahulu");
        Button btnSimpan = new Button("Simpan");

        // Label data tambahan dan pilihan kondisi menyesuaikan jenis yang dipilih
        cmbJenis.setOnAction(e -> {
            int idx = cmbJenis.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                lblEkstra.setText(labelEkstra[idx]);
                cmbKondisi.getItems().clear();
                cmbKondisi.getItems().addAll(contohObjek(idx).getOpsiKondisi());
                cmbKondisi.setValue(null);
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(15));
        form.add(new Label("Jenis"), 0, 0);
        form.add(cmbJenis, 1, 0);
        form.add(new Label("Nama"), 0, 1);
        form.add(txtNama, 1, 1);
        form.add(new Label("Lokasi"), 0, 2);
        form.add(txtLokasi, 1, 2);
        form.add(new Label("Latitude"), 0, 3);
        form.add(txtLat, 1, 3);
        form.add(new Label("Longitude"), 0, 4);
        form.add(txtLon, 1, 4);
        form.add(lblEkstra, 0, 5);
        form.add(txtEkstra, 1, 5);
        form.add(new Label("Kondisi"), 0, 6);
        form.add(cmbKondisi, 1, 6);
        form.add(btnSimpan, 1, 8);

        btnSimpan.setOnAction(e -> {
            try {
                int idx = cmbJenis.getSelectionModel().getSelectedIndex();
                if (idx < 0) {
                    throw new IllegalArgumentException("Pilih jenis infrastruktur terlebih dahulu.");
                }
                if (cmbKondisi.getValue() == null) {
                    throw new IllegalArgumentException("Pilih kondisi infrastruktur.");
                }

                String id = sistem.buatIdInfrastruktur();
                String nama = txtNama.getText();
                String lokasi = txtLokasi.getText();
                String kondisi = cmbKondisi.getValue();
                String ekstra = txtEkstra.getText();
                double lat = Double.parseDouble(txtLat.getText().trim().replace(",", "."));
                double lon = Double.parseDouble(txtLon.getText().trim().replace(",", "."));

                Infrastruktur baru;
                switch (idx) {
                    case 0:
                        baru = new Jalan(id, nama, lokasi, lat, lon, kondisi,
                                Double.parseDouble(ekstra.trim().replace(",", ".")));
                        break;
                    case 1:
                        baru = new LampuJalan(id, nama, lokasi, lat, lon, kondisi,
                                Integer.parseInt(ekstra.trim()));
                        break;
                    case 2:
                        baru = new Jembatan(id, nama, lokasi, lat, lon, kondisi, ekstra);
                        break;
                    case 3:
                        baru = new Drainase(id, nama, lokasi, lat, lon, kondisi,
                                Double.parseDouble(ekstra.trim().replace(",", ".")));
                        break;
                    default:
                        baru = new FasilitasUmum(id, nama, lokasi, lat, lon, kondisi, ekstra);
                        break;
                }

                sistem.tambahInfrastruktur(baru);
                dataInfra.add(baru);
                gambarPeta();
                perbaruiDashboard();

                tampilAlert(Alert.AlertType.INFORMATION, "Berhasil",
                        "Data infrastruktur ditambahkan dengan ID " + id + ".");
                txtNama.clear();
                txtLokasi.clear();
                txtLat.clear();
                txtLon.clear();
                txtEkstra.clear();
                cmbJenis.setValue(null);
                cmbKondisi.getItems().clear();
            } catch (NumberFormatException ex) {
                // NumberFormatException harus ditangkap sebelum IllegalArgumentException
                tampilAlert(Alert.AlertType.ERROR, "Format Angka Salah",
                        "Latitude, longitude, dan data ukuran harus berupa angka.");
            } catch (IllegalArgumentException ex) {
                tampilAlert(Alert.AlertType.WARNING, "Data Tidak Valid", ex.getMessage());
            }
        });

        Tab tab = new Tab("Tambah Infrastruktur");
        tab.setContent(form);
        return tab;
    }

    // Objek contoh hanya untuk mengambil daftar opsi kondisi setiap jenis
    private Infrastruktur contohObjek(int idx) {
        switch (idx) {
            case 0:
                return new Jalan("-", "-", "-", 0, 0, "Rusak", 1);
            case 1:
                return new LampuJalan("-", "-", "-", 0, 0, "Mati", 0);
            case 2:
                return new Jembatan("-", "-", "-", 0, 0, "Baik", "-");
            case 3:
                return new Drainase("-", "-", "-", 0, 0, "Lancar", 1);
            default:
                return new FasilitasUmum("-", "-", "-", 0, 0, "Baik", "-");
        }
    }

    // METHOD BANTU
    private TableColumn<Infrastruktur, String> kolomInfra(String judul, double lebar,
            Function<Infrastruktur, String> isi) {
        TableColumn<Infrastruktur, String> kolom = new TableColumn<>(judul);
        kolom.setPrefWidth(lebar);
        kolom.setCellValueFactory(data -> new SimpleStringProperty(isi.apply(data.getValue())));
        return kolom;
    }

    private TableColumn<Laporan, String> kolomLaporan(String judul, double lebar,
            Function<Laporan, String> isi) {
        TableColumn<Laporan, String> kolom = new TableColumn<>(judul);
        kolom.setPrefWidth(lebar);
        kolom.setCellValueFactory(data -> new SimpleStringProperty(isi.apply(data.getValue())));
        return kolom;
    }

    private void tampilAlert(Alert.AlertType tipe, String judul, String pesan) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private Optional<String> mintaTeks(String judul, String pesan) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(judul);
        dialog.setHeaderText(null);
        dialog.setContentText(pesan);
        return dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}