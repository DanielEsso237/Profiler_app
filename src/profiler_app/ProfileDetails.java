package profiler_app;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ProfileDetails extends JFrame {

    public ProfileDetails(int profilId) {
        setTitle("Détails du Profil");
        setSize(650, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel container = new JPanel();
        container.setLayout(new BorderLayout(10, 10));
        container.setBackground(new Color(240, 248, 255)); // Bleu clair doux

        // Panneau supérieur pour la photo
        JPanel photoPanel = new JPanel();
        photoPanel.setBackground(new Color(255, 255, 255));
        photoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        photoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Panneau des détails
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(new Color(255, 255, 255));
        detailsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panneau des boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        // Charger les données et remplir les panneaux
        loadProfileDetails(profilId, photoPanel, detailsPanel, buttonPanel);

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        container.add(photoPanel, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(container);
        setVisible(true);
    }

    private void loadProfileDetails(int profilId, JPanel photoPanel, JPanel detailsPanel, JPanel buttonPanel) {
        try (Connection conn = Utils.connectToDatabase()) {
            if (conn == null) {
                detailsPanel.add(new JLabel("Erreur : Connexion à la base de données échouée."));
                return;
            }

            // 1. Informations de base depuis "profils"
            String profilSQL = "SELECT * FROM profils WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(profilSQL)) {
                ps.setInt(1, profilId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Afficher la photo
                        String photoPath = rs.getString("photo");
                        JLabel photoLabel;
                        // Chemin de l'image par défaut (remplacez par le chemin réel)
                        String defaultPhotoPath = "image/user.png"; // Exemple de chemin
                        try {
                            // Vérifiez si photoPath est null ou vide
                            if (photoPath == null || photoPath.trim().isEmpty()) {
                                ImageIcon icon = new ImageIcon(defaultPhotoPath);
                                Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                                photoLabel = new JLabel(new ImageIcon(scaledImage));
                            } else {
                                ImageIcon icon = new ImageIcon(photoPath);
                                Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                                photoLabel = new JLabel(new ImageIcon(scaledImage));
                            }
                        } catch (Exception e) {
                            photoLabel = new JLabel("Pas d’image disponible");
                            photoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                            photoLabel.setForeground(Color.GRAY);
                        }
                        photoPanel.add(photoLabel);

                        // Informations personnelles
                        detailsPanel.add(createSectionTitle("Informations personnelles"));
                        detailsPanel.add(createLabel("Nom : " + rs.getString("nom")));
                        detailsPanel.add(createLabel("Date de naissance : " + rs.getString("date_naissance")));
                        detailsPanel.add(createLabel("Prénom : " + rs.getString("prenom")));
                        detailsPanel.add(createLabel("Âge : " + rs.getInt("age")));
                        detailsPanel.add(createLabel("Sexe : " + rs.getString("sexe")));
                        detailsPanel.add(createLabel("Email : " + rs.getString("email")));
                        detailsPanel.add(createLabel("Téléphone : " + rs.getString("telephone")));
                        detailsPanel.add(createLabel("Adresse : " + rs.getString("adresse")));
                        detailsPanel.add(createLabel("Nationalité : " + rs.getString("nationalite")));
                    } else {
                        detailsPanel.add(new JLabel("Profil non trouvé."));
                        return;
                    }
                }
            }

            // 2. Compétences
            detailsPanel.add(createSectionTitle("Compétences"));
            String competenceSQL = "SELECT competence, niveau FROM competences WHERE profil_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(competenceSQL)) {
                ps.setInt(1, profilId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        detailsPanel.add(createLabel(" - " + rs.getString("competence") + " (" + rs.getString("niveau") + ")"));
                    }
                }
            }

            // 3. Diplômes
            detailsPanel.add(createSectionTitle("Diplômes"));
            String diplomeSQL = "SELECT niveau_etudes, domaine, etablissement, annee_obtention FROM diplomes WHERE profil_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(diplomeSQL)) {
                ps.setInt(1, profilId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        detailsPanel.add(createLabel(" - " + rs.getString("niveau_etudes") + " en " + rs.getString("domaine") + 
                            ", " + rs.getString("etablissement") + " (" + rs.getInt("annee_obtention") + ")"));
                    }
                }
            }

            // 4. Expériences
            detailsPanel.add(createSectionTitle("Expériences"));
            String experienceSQL = "SELECT poste, entreprise, annee_debut, annee_fin, description FROM experiences WHERE profil_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(experienceSQL)) {
                ps.setInt(1, profilId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String anneeFin = rs.getString("annee_fin") == null ? "En cours" : rs.getString("annee_fin");
                        detailsPanel.add(createLabel(" - " + rs.getString("poste") + " chez " + rs.getString("entreprise") + 
                            " (" + rs.getInt("annee_debut") + " - " + anneeFin + "): " + rs.getString("description")));
                    }
                }
            }

            // 5. Langues
            detailsPanel.add(createSectionTitle("Langues"));
            String langueSQL = "SELECT langue, niveau FROM langues WHERE profil_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(langueSQL)) {
                ps.setInt(1, profilId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        detailsPanel.add(createLabel(" - " + rs.getString("langue") + " (" + rs.getString("niveau") + ")"));
                    }
                }
            }

            // 6. Visites médicales
            detailsPanel.add(createSectionTitle("Visites médicales"));
            String visiteSQL = "SELECT date_visite, notes FROM visites_medicales WHERE profil_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(visiteSQL)) {
                ps.setInt(1, profilId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        detailsPanel.add(createLabel(" - " + rs.getString("date_visite") + ": " + rs.getString("notes")));
                    }
                }
            }

            // Boutons
            JButton deleteBtn = new JButton("Effacer");
            deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            deleteBtn.setBackground(new Color(220, 53, 69)); // Rouge Bootstrap
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce profil ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteProfile(profilId);
                    dispose();
                }
            });

            JButton modifyBtn = new JButton("Modifier");
            modifyBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            modifyBtn.setBackground(new Color(40, 167, 69)); // Vert Bootstrap
            modifyBtn.setForeground(Color.WHITE);
            modifyBtn.setFocusPainted(false);
            modifyBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            modifyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            modifyBtn.addActionListener(e -> new ModifyProfile(profilId)); // Correction ici

            buttonPanel.add(modifyBtn);
            buttonPanel.add(deleteBtn);

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des détails : " + e.getMessage());
            detailsPanel.add(new JLabel("Erreur lors du chargement des données : " + e.getMessage()));
        }
    }

    private void deleteProfile(int profilId) {
        try (Connection conn = Utils.connectToDatabase()) {
            if (conn == null) return;
            String deleteSQL = "DELETE FROM profils WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSQL)) {
                ps.setInt(1, profilId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Profil supprimé avec succès !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(33, 37, 41)); // Gris foncé pour contraste
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 0));
        return label;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(0, 123, 255)); // Bleu vif pour les titres
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        return label;
    }
}