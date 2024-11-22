/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package app;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.io.*;
/**
 *
 * @author DAVID IMAN FURQONI
 */
public class GoMad extends javax.swing.JFrame {
    
    private int userId;
    public static int currentUserId; // Variabel statis untuk menyimpan ID pengguna

    public GoMad(int UserId) throws IOException {
    try {
        initComponents();
        // Kode lainnya
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saat memuat GoMad: " + e.getMessage());
    }
    currentUserId = userId; // Simpan ID pengguna yang baru login
        refreshWishlistTable(); // Panggil untuk menampilkan wishlist pengguna

    this.currentUserId = currentUserId; // Menyimpan ID pengguna saat ini
    
    
    koneksi = Koneksi.getKoneksi();

    // Inisialisasi model tabel
    initTableModels();

    // Memuat data
    loadDataWisata(); 
    wishlist(); 
    refreshWishlistTable();
    }
    
    private void initTableModels() {
    modelRekom = new DefaultTableModel();
    modelRekom.addColumn("ID Wisata");
    modelRekom.addColumn("Nama Wisata");
    modelRekom.addColumn("Lokasi");
    modelRekom.addColumn("Deskripsi");
    modelRekom.addColumn("Kategori");
    modelRekom.addColumn("Gambar");
    tabelRekom.setModel(modelRekom);

    modelCari = new DefaultTableModel();
    modelCari.addColumn("ID Wisata");
    modelCari.addColumn("Nama Wisata");
    modelCari.addColumn("Lokasi");
    modelCari.addColumn("Deskripsi");
    modelCari.addColumn("Kategori");
    modelCari.addColumn("Gambar");
    tabelCari.setModel(modelCari);

    modelWishlist = new DefaultTableModel();
    modelWishlist.addColumn("ID Wisata");
    modelWishlist.addColumn("Nama Wisata");
    modelWishlist.addColumn("Lokasi");
    modelWishlist.addColumn("Catatan");
    tabelWishlist.setModel(modelWishlist);
}

    
    
    Connection koneksi;
    private DefaultTableModel modelRekom;
    private DefaultTableModel modelCari;
    private DefaultTableModel modelWishlist;

    detailFrame d = new detailFrame();

    /**
     * Creates new form GoMad
     */
    public GoMad() throws IOException {
        initComponents();

        koneksi = Koneksi.getKoneksi();
        
        
        modelRekom = new DefaultTableModel();
        modelRekom.addColumn("ID Wisata");
        modelRekom.addColumn("Nama Wisata");
        modelRekom.addColumn("Lokasi");
        modelRekom.addColumn("Deskripsi");
        modelRekom.addColumn("Kategori");
        modelRekom.addColumn("Gambar"); 
        tabelRekom.setModel(modelRekom);

     
        modelCari = new DefaultTableModel();
        modelCari.addColumn("ID Wisata");
        modelCari.addColumn("Nama Wisata");
        modelCari.addColumn("Lokasi");
        modelCari.addColumn("Deskripsi");
        modelCari.addColumn("Kategori");
        modelCari.addColumn("Gambar");
        tabelCari.setModel(modelCari);
        
        modelWishlist = new DefaultTableModel();
        modelWishlist.addColumn("ID Wisata");
        modelWishlist.addColumn("Nama Wisata");
        modelWishlist.addColumn("Lokasi");
        modelWishlist.addColumn("Deskripsi");
        modelWishlist.addColumn("Kategori");
        modelWishlist.addColumn("Gambar");
        tabelWishlist.setModel(modelWishlist);

        loadDataWisata(); 
        wishlist(); 
        
    }

private void configureTableRenderer(JTable table) {
    // Set custom renderer for the image column
    table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                                                       boolean isSelected, boolean hasFocus, 
                                                       int row, int column) {
            // If value is an ImageIcon, create a JLabel to display the image
            if (value instanceof ImageIcon) {
                JLabel label = new JLabel((ImageIcon) value);
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
            
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    });

    table.setRowHeight(100); 
}


private ImageIcon scaleImageIcon(ImageIcon icon, int width, int height) {
    if (icon == null) return null;
    
    Image img = icon.getImage();
    Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    return new ImageIcon(scaledImg);
}

    
    private void loadDataWisata() {
    modelRekom.setRowCount(0); // Reset data di tabel

    try {
        String sql = "SELECT * FROM wisata"; // Ambil semua data wisata
        PreparedStatement stmt = koneksi.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            byte[] gambarBytes = rs.getBytes("gambar");
            ImageIcon imageIcon = null;

            if (gambarBytes != null && gambarBytes.length > 0) {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(gambarBytes));
                if (img != null) {
                    imageIcon = new ImageIcon(img);
                    imageIcon = scaleImageIcon(imageIcon, 100, 100); // Sesuaikan ukuran gambar
                }
            }

            modelRekom.addRow(new Object[]{
                rs.getInt("id_wisata"),
                rs.getString("nama_wisata"),
                rs.getString("lokasi"),
                rs.getString("deskripsi"),
                rs.getString("kategori"),
                imageIcon != null ? imageIcon : "Gambar tidak tersedia"
            });
        }

        configureTableRenderer(tabelRekom); // Konfigurasi renderer tabel

        rs.close();
        stmt.close();
    } catch (SQLException | IOException e) {
        JOptionPane.showMessageDialog(this, "Error memuat data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    

    private void CariWisata() {
    modelCari.setRowCount(0); // Reset data di tabel

    try {
        // Membuat query berdasarkan input pengguna
        StringBuilder queryBuilder = new StringBuilder("SELECT id_wisata, nama_wisata, lokasi, deskripsi, kategori, gambar FROM wisata WHERE 1=1");

        // Filter berdasarkan kategori
        String selectedKategori = (String) kategoriCBX.getSelectedItem();
        if (!selectedKategori.equals("Semua")) {
            queryBuilder.append(" AND kategori = '").append(selectedKategori).append("'");
        }

        // Filter berdasarkan lokasi
        if (bangkalan.isSelected()) {
            queryBuilder.append(" AND lokasi = 'Bangkalan'");
        } else if (pamekasan.isSelected()) {
            queryBuilder.append(" AND lokasi = 'Pamekasan'");
        } else if (sampang.isSelected()) {
            queryBuilder.append(" AND lokasi = 'Sampang'");
        } else if (sumenep.isSelected()) {
            queryBuilder.append(" AND lokasi = 'Sumenep'");
        }

        String sql = queryBuilder.toString();
        PreparedStatement stmt = koneksi.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            byte[] gambarBytes = rs.getBytes("gambar");
            ImageIcon imageIcon = null;

            if (gambarBytes != null && gambarBytes.length > 0) {
                try {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(gambarBytes));
                    if (img != null) {
                        imageIcon = new ImageIcon(img);
                        // Optional: scale image
                        imageIcon = scaleImageIcon(imageIcon, 100, 100);
                    }
                } catch (IOException e) {
                    System.err.println("Error loading image: " + e.getMessage());
                }
            }

            // Menambahkan data ke tabel
            modelCari.addRow(new Object[]{
                rs.getInt("id_wisata"),
                rs.getString("nama_wisata"),
                rs.getString("lokasi"),
                rs.getString("deskripsi"),
                rs.getString("kategori"),
                imageIcon != null ? imageIcon : "Gambar tidak tersedia"
            });
        }

        // Konfigurasi renderer untuk menampilkan gambar di tabel
        configureTableRenderer(tabelCari);  // Untuk tabelCari

        rs.close();
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error while connecting to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

// Saat login berhasil, atur nilai currentUserId
public void setCurrentUserId(int userId) {
    this.currentUserId = userId;
}
    
    private void wishlist(){
        modelWishlist.setRowCount(0); // Reset data di tabel

    try {
        String sql = "SELECT w.id_wisata, w.nama_wisata, w.lokasi, w.catatan, u.username " +
                     "FROM wishlist w " +
                     "JOIN user u ON w.id_user = u.id_user " +
                     "WHERE w.id_user = ?";
        PreparedStatement stmt = koneksi.prepareStatement(sql);
        stmt.setInt(1, currentUserId); // Gunakan ID pengguna saat ini
        ResultSet rs = stmt.executeQuery();

        // Atur model tabel hanya dengan kolom yang diperlukan
        modelWishlist.setColumnCount(0);
        modelWishlist.addColumn("ID Wisata");
        modelWishlist.addColumn("Nama Wisata");
        modelWishlist.addColumn("Lokasi");
        modelWishlist.addColumn("Catatan");
        modelWishlist.addColumn("Penambah");

        while (rs.next()) {
            modelWishlist.addRow(new Object[] {
                rs.getInt("id_wisata"),
                rs.getString("nama_wisata"),
                rs.getString("lokasi"),
                rs.getString("catatan"),
                rs.getString("username")
            });
        }
        rs.close();
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error memuat data wishlist: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    }
    
    
    
        private void addToWishlist(int idWisata, String namaWisata, String lokasi, String deskripsi, String kategori, byte[] gambar, String catatan) {
    try {
        String sql = "INSERT INTO wishlist (id_wisata, nama_wisata, lokasi, deskripsi, kategori, gambar, catatan, id_user) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = koneksi.prepareStatement(sql);
        stmt.setInt(1, idWisata);
        stmt.setString(2, namaWisata);
        stmt.setString(3, lokasi);
        stmt.setString(4, deskripsi);
        stmt.setString(5, kategori);
        stmt.setBytes(6, gambar);
        stmt.setString(7, catatan);
        stmt.setInt(8, currentUserId);
        stmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "Berhasil menambahkan ke wishlist!");

        // Panggil refreshWishlistTable untuk memperbarui tabel
        refreshWishlistTable();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error menambahkan data wishlist: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void updateCatatan(int idWisata, String newCatatan) {
    try {
        String sql = "UPDATE wishlist SET catatan = ? WHERE id_wisata = ?";
        PreparedStatement stmt = koneksi.prepareStatement(sql);
        stmt.setString(1, newCatatan);
        stmt.setInt(2, idWisata);
        
        int affectedRows = stmt.executeUpdate();
        if (affectedRows > 0) {
            // Jika berhasil, tampilkan pesan sukses
            JOptionPane.showMessageDialog(this, "Catatan berhasil diperbarui.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            
            // Memperbarui tabel wishlist
            wishlist(); // Pastikan untuk memuat ulang data wishlist di tabel
        } else {
            // Jika tidak ada baris yang terpengaruh, tampilkan pesan error
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memperbarui catatan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error saat mengupdate catatan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        Home = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelRekom = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        btnWishlistHome = new javax.swing.JButton();
        btnDetailHome = new javax.swing.JButton();
        cariWisata = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        kategoriCBX = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        bangkalan = new javax.swing.JRadioButton();
        sampang = new javax.swing.JRadioButton();
        pamekasan = new javax.swing.JRadioButton();
        sumenep = new javax.swing.JRadioButton();
        btnCari = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        btnWishlistCari = new javax.swing.JButton();
        btnDetailCari = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabelCari = new javax.swing.JTable();
        Wishlist = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        btnUpdate = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelWishlist = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        jLabel3.setFont(new java.awt.Font("Sitka Text", 1, 18)); // NOI18N
        jLabel3.setText("Rekomendasi Wisata di MADURA");

        jLabel8.setFont(new java.awt.Font("Rockwell Extra Bold", 1, 18)); // NOI18N
        jLabel8.setText("GoMad (Go Madura)");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        tabelRekom.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelRekom.setRowHeight(50);
        jScrollPane2.setViewportView(tabelRekom);
        if (tabelRekom.getColumnModel().getColumnCount() > 0) {
            tabelRekom.getColumnModel().getColumn(0).setResizable(false);
            tabelRekom.getColumnModel().getColumn(1).setResizable(false);
            tabelRekom.getColumnModel().getColumn(2).setResizable(false);
            tabelRekom.getColumnModel().getColumn(3).setResizable(false);
        }

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));

        btnWishlistHome.setText("Tambah ke Wishlist");
        btnWishlistHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWishlistHomeActionPerformed(evt);
            }
        });

        btnDetailHome.setText("Lihat Detail");
        btnDetailHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailHomeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnDetailHome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnWishlistHome)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDetailHome)
                    .addComponent(btnWishlistHome))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout HomeLayout = new javax.swing.GroupLayout(Home);
        Home.setLayout(HomeLayout);
        HomeLayout.setHorizontalGroup(
            HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomeLayout.createSequentialGroup()
                .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(HomeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(HomeLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE))))
                .addContainerGap())
        );
        HomeLayout.setVerticalGroup(
            HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomeLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(93, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Home", Home);

        cariWisata.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jLabel4.setText("kategori");

        kategoriCBX.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pantai", "Bukit", "Sejarah ", "Goa" }));

        jLabel5.setText("lokasi");

        buttonGroup1.add(bangkalan);
        bangkalan.setText("Bangkalan");

        buttonGroup1.add(sampang);
        sampang.setText("Sampang");

        buttonGroup1.add(pamekasan);
        pamekasan.setText("Pamekasan");
        pamekasan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pamekasanActionPerformed(evt);
            }
        });

        buttonGroup1.add(sumenep);
        sumenep.setText("Sumenep");

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Stencil", 1, 24)); // NOI18N
        jLabel2.setText("Cari Wisata");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(bangkalan)
                                .addGap(60, 60, 60)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(sumenep)
                                        .addGap(92, 92, 92)
                                        .addComponent(btnCari))
                                    .addComponent(pamekasan)))
                            .addComponent(sampang))
                        .addContainerGap(122, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(kategoriCBX, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kategoriCBX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bangkalan)
                        .addComponent(pamekasan)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sumenep)
                    .addComponent(sampang)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCari)
                .addContainerGap())
        );

        cariWisata.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 560, -1));

        jPanel6.setBackground(new java.awt.Color(204, 204, 204));

        btnWishlistCari.setText("Tambah ke Wishlist");
        btnWishlistCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWishlistCariActionPerformed(evt);
            }
        });

        btnDetailCari.setText("Lihat Detail");
        btnDetailCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailCariActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnDetailCari, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 324, Short.MAX_VALUE)
                .addComponent(btnWishlistCari)
                .addGap(18, 18, 18))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnWishlistCari)
                    .addComponent(btnDetailCari))
                .addContainerGap(52, Short.MAX_VALUE))
        );

        cariWisata.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, -1, 80));

        tabelCari.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(tabelCari);

        cariWisata.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 560, 230));

        jTabbedPane1.addTab("Cari Wisata", cariWisata);

        Wishlist.setLayout(new java.awt.BorderLayout());

        jPanel7.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("Perpetua Titling MT", 1, 24)); // NOI18N
        jLabel1.setText("Wishlist Anda");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(342, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        Wishlist.add(jPanel7, java.awt.BorderLayout.PAGE_START);

        jPanel9.setBackground(new java.awt.Color(204, 204, 204));

        btnUpdate.setText("Update Keterangan");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus Wishlist");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnHapus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 314, Short.MAX_VALUE)
                .addComponent(btnUpdate)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdate)
                    .addComponent(btnHapus))
                .addContainerGap(63, Short.MAX_VALUE))
        );

        Wishlist.add(jPanel9, java.awt.BorderLayout.PAGE_END);

        tabelWishlist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabelWishlist);

        Wishlist.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Wishlist", Wishlist);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 653, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void pamekasanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pamekasanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pamekasanActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
         // Ambil ID Wisata yang dipilih dari tabel
        int selectedRow = tabelWishlist.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih wisata yang ingin diperbarui.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil ID Wisata dari kolom pertama (ID Wisata)
        int idWisata = (int) modelWishlist.getValueAt(selectedRow, 0);

        // Tampilkan JOptionPane untuk meminta input catatan baru
        String newCatatan = JOptionPane.showInputDialog(this, "Masukkan catatan baru:", "Update Catatan", JOptionPane.PLAIN_MESSAGE);

        if (newCatatan != null && !newCatatan.trim().isEmpty()) {
            // Panggil method untuk mengupdate catatan di database
            updateCatatan(idWisata, newCatatan);
        } else {
            JOptionPane.showMessageDialog(this, "Catatan tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        // TODO add your handling code here:
        CariWisata();
    }//GEN-LAST:event_btnCariActionPerformed

    private void btnWishlistHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWishlistHomeActionPerformed
    // Ambil baris yang dipilih dari tabel
    int selectedRow = tabelRekom.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Silakan pilih wisata yang ingin ditambahkan ke wishlist.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Ambil ID Wisata dan data lainnya dari baris yang dipilih
    int idWisata = (int) modelRekom.getValueAt(selectedRow, 0);
    String namaWisata = (String) modelRekom.getValueAt(selectedRow, 1);
    String lokasi = (String) modelRekom.getValueAt(selectedRow, 2);
    String deskripsi = (String) modelRekom.getValueAt(selectedRow, 3);
    String kategori = (String) modelRekom.getValueAt(selectedRow, 4);
    ImageIcon imageIcon = (ImageIcon) modelRekom.getValueAt(selectedRow, 5);

    // Konversi ImageIcon menjadi byte[]
    byte[] gambar = convertImageIconToByteArray(imageIcon);

    // Tampilkan dialog untuk meminta catatan dari pengguna
    String catatan = JOptionPane.showInputDialog(this, "Masukkan catatan untuk wisata " + namaWisata + ":", "Tambah ke Wishlist", JOptionPane.PLAIN_MESSAGE);

    if (catatan != null && !catatan.trim().isEmpty()) {
        // Panggil method untuk menambahkan ke wishlist
        addToWishlist(idWisata, namaWisata, lokasi, deskripsi, kategori, gambar, catatan);
    } else {
        JOptionPane.showMessageDialog(this, "Catatan tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_btnWishlistHomeActionPerformed
private byte[] convertImageIconToByteArray(ImageIcon imageIcon) {
    BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = bufferedImage.createGraphics();
    imageIcon.paintIcon(null, g2d, 0, 0);
    g2d.dispose();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
        ImageIO.write(bufferedImage, "jpg", baos);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return baos.toByteArray();
}
    private void btnDetailHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetailHomeActionPerformed
            // Ambil baris yang dipilih dari tabel
    int selectedRow = tabelRekom.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Silakan pilih wisata yang ingin dilihat detailnya.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Ambil ID Wisata dari kolom pertama (ID Wisata) pada baris yang dipilih
    int idWisata = (int) modelRekom.getValueAt(selectedRow, 0);

    try {
        // Ambil data detail wisata dari database berdasarkan ID Wisata yang dipilih
        String sql = "SELECT nama_wisata, deskripsi, gambar FROM wisata WHERE id_wisata = ?";
        PreparedStatement ps = koneksi.prepareStatement(sql);
        ps.setInt(1, idWisata); // Gunakan ID Wisata yang dipilih
        ResultSet rs = ps.executeQuery();

        // Jika data ditemukan
        if (rs.next()) {
            String judul = rs.getString("nama_wisata");
            String deskripsi = rs.getString("deskripsi");
            byte[] gambarBytes = rs.getBytes("gambar");

            if (gambarBytes != null && gambarBytes.length > 0) {
                try {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(gambarBytes));
                    ImageIcon imageIcon = new ImageIcon(img);
                    // Optional: scale image
                    imageIcon = scaleImageIcon(imageIcon, 150, 110);
                    d.gambarDetail.setIcon(imageIcon);
                } catch (IOException e) {
                    System.err.println("Error loading image: " + e.getMessage());
                }
            } else {
                d.gambarDetail.setIcon(null); // Jika tidak ada gambar, set ikon kosong
            }

            // Menampilkan detail lainnya di frame
            d.detailArea.setText(deskripsi);
            d.judul.setText("DETAIL: " + judul);
        } else {
            JOptionPane.showMessageDialog(this, "Detail wisata tidak ditemukan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }

        // Menutup statement dan resultSet setelah selesai
        rs.close();
        ps.close();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error memuat data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Tampilkan detail frame
    d.setDefaultCloseOperation(detailFrame.DISPOSE_ON_CLOSE);
    d.setVisible(true);
    }//GEN-LAST:event_btnDetailHomeActionPerformed

    private void btnDetailCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetailCariActionPerformed
        // TODO add your handling code here:
        // Ambil baris yang dipilih dari tabel
    int selectedRow = tabelCari.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Silakan pilih wisata yang ingin dilihat detailnya.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Ambil ID Wisata dari kolom pertama (ID Wisata) pada baris yang dipilih
    int idWisata = (int) modelRekom.getValueAt(selectedRow, 0);

    try {
        // Ambil data detail wisata dari database berdasarkan ID Wisata yang dipilih
        String sql = "SELECT nama_wisata, deskripsi, gambar FROM wisata WHERE id_wisata = ?";
        PreparedStatement ps = koneksi.prepareStatement(sql);
        ps.setInt(1, idWisata); // Gunakan ID Wisata yang dipilih
        ResultSet rs = ps.executeQuery();

        // Jika data ditemukan
        if (rs.next()) {
            String judul = rs.getString("nama_wisata");
            String deskripsi = rs.getString("deskripsi");
            byte[] gambarBytes = rs.getBytes("gambar");

            if (gambarBytes != null && gambarBytes.length > 0) {
                try {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(gambarBytes));
                    ImageIcon imageIcon = new ImageIcon(img);
                    // Optional: scale image
                    imageIcon = scaleImageIcon(imageIcon, 150, 110);
                    d.gambarDetail.setIcon(imageIcon);
                } catch (IOException e) {
                    System.err.println("Error loading image: " + e.getMessage());
                }
            } else {
                d.gambarDetail.setIcon(null); // Jika tidak ada gambar, set ikon kosong
            }

            // Menampilkan detail lainnya di frame
            d.detailArea.setText(deskripsi);
            d.judul.setText("DETAIL: " + judul);
        } else {
            JOptionPane.showMessageDialog(this, "Detail wisata tidak ditemukan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }

        // Menutup statement dan resultSet setelah selesai
        rs.close();
        ps.close();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error memuat data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Tampilkan detail frame
    d.setDefaultCloseOperation(detailFrame.DISPOSE_ON_CLOSE);
    d.setVisible(true);
    }//GEN-LAST:event_btnDetailCariActionPerformed

    private void btnWishlistCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWishlistCariActionPerformed
    // Ambil baris yang dipilih dari tabel
    int selectedRow = tabelCari.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Silakan pilih wisata yang ingin ditambahkan ke wishlist.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Ambil ID Wisata dan data lainnya dari baris yang dipilih
    int idWisata = (int) modelCari.getValueAt(selectedRow, 0);
    String namaWisata = (String) modelCari.getValueAt(selectedRow, 1);
    String lokasi = (String) modelCari.getValueAt(selectedRow, 2);
    String deskripsi = (String) modelCari.getValueAt(selectedRow, 3);
    String kategori = (String) modelCari.getValueAt(selectedRow, 4);
    ImageIcon imageIcon = (ImageIcon) modelCari.getValueAt(selectedRow, 5);

    // Konversi ImageIcon menjadi byte[]
    byte[] gambar = convertImageIconToByteArray(imageIcon);

    // Tampilkan dialog untuk meminta catatan dari pengguna
    String catatan = JOptionPane.showInputDialog(this, "Masukkan catatan untuk wisata " + namaWisata + ":", "Tambah ke Wishlist", JOptionPane.PLAIN_MESSAGE);

    if (catatan != null && !catatan.trim().isEmpty()) {
        // Panggil method untuk menambahkan ke wishlist
        addToWishlist(idWisata, namaWisata, lokasi, deskripsi, kategori, gambar, catatan);
    } else {
        JOptionPane.showMessageDialog(this, "Catatan tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_btnWishlistCariActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        // Ambil baris yang dipilih dari tabel wishlist
    int selectedRow = tabelWishlist.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Silakan pilih item yang ingin dihapus dari wishlist.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Ambil ID Wisata dari kolom pertama (ID Wisata) pada baris yang dipilih
    int idWisata = (int) modelWishlist.getValueAt(selectedRow, 0);

    try {
        // Hapus data dari database berdasarkan ID Wisata
        String sql = "DELETE FROM wishlist WHERE id_wisata = ?";
        PreparedStatement ps = koneksi.prepareStatement(sql);
        ps.setInt(1, idWisata);
        int rowsDeleted = ps.executeUpdate();

        if (rowsDeleted > 0) {
            JOptionPane.showMessageDialog(this, "Item berhasil dihapus dari wishlist.", "Informasi", JOptionPane.INFORMATION_MESSAGE);

            // Refresh tabel wishlist
            refreshWishlistTable();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus item dari wishlist.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }

        // Menutup statement setelah selesai
        ps.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error menghapus item dari wishlist: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void refreshWishlistTable() {
    try {
            modelWishlist.setRowCount(0); // Bersihkan model tabel sebelum memperbarui

            // Ambil data wishlist berdasarkan ID pengguna yang aktif
            String sql = "SELECT id_wisata, nama_wisata FROM wishlist WHERE id_user = ?";
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setInt(1, currentUserId); // Gunakan ID pengguna yang baru login
            ResultSet rs = ps.executeQuery();

            // Tambahkan data baru ke model tabel
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_wisata"), // ID Wisata
                    rs.getString("nama_wisata") // Nama Wisata
                };
                modelWishlist.addRow(row);
            }

            // Menutup statement dan resultSet setelah selesai
            rs.close();
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error memuat data wishlist: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    
    }//GEN-LAST:event_btnHapusActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GoMad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GoMad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GoMad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GoMad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new GoMad().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(GoMad.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Home;
    private javax.swing.JPanel Wishlist;
    private javax.swing.JRadioButton bangkalan;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnDetailCari;
    private javax.swing.JButton btnDetailHome;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnWishlistCari;
    private javax.swing.JButton btnWishlistHome;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel cariWisata;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox<String> kategoriCBX;
    private javax.swing.JRadioButton pamekasan;
    private javax.swing.JRadioButton sampang;
    private javax.swing.JRadioButton sumenep;
    private javax.swing.JTable tabelCari;
    private javax.swing.JTable tabelRekom;
    private javax.swing.JTable tabelWishlist;
    // End of variables declaration//GEN-END:variables
}
