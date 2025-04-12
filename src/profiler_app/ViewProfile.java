package profiler_app;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ViewProfile {

    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(50, 150, 100);
    private static final Color HOVER_COLOR = new Color(230, 245, 235);

    private JPanel cardsPanel;
    private JLabel profileCountLabel;
    private List<Object[]> allProfiles;

    public ViewProfile(JPanel contentPanel) {
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);

        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_COLOR, 0, getHeight(), new Color(200, 230, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new BorderLayout());
        contentPanel.add(gradientPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        topPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Rechercher un profil");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        topPanel.add(titleLabel, gbc);

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(400, 35));
        searchField.setMinimumSize(new Dimension(300, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(searchField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton refreshBtn = new JButton("Rafraîchir");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(100, 100, 100));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                refreshBtn.setBackground(new Color(120, 120, 120));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                refreshBtn.setBackground(new Color(100, 100, 100));
            }
        });
        refreshBtn.addActionListener(e -> {
            refreshProfiles();
            searchField.setText("");
        });
        buttonPanel.add(refreshBtn);

        JButton createBtn = new JButton("Créer un nouveau profil");
        createBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createBtn.setBackground(ACCENT_COLOR);
        createBtn.setForeground(Color.WHITE);
        createBtn.setFocusPainted(false);
        createBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        createBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                createBtn.setBackground(new Color(60, 180, 120));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                createBtn.setBackground(ACCENT_COLOR);
            }
        });
        createBtn.addActionListener(e -> new AddProfile());
        buttonPanel.add(createBtn);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        topPanel.add(buttonPanel, gbc);

        gradientPanel.add(topPanel, BorderLayout.NORTH);

        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setOpaque(false);

        allProfiles = loadProfilesFromDatabase();
        updateCards(allProfiles);

        profileCountLabel = new JLabel("Profils affichés : " + allProfiles.size());
        profileCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        profileCountLabel.setForeground(new Color(100, 100, 100));
        profileCountLabel.setBorder(new EmptyBorder(5, 15, 10, 0));
        gradientPanel.add(profileCountLabel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        gradientPanel.add(scrollPane, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterProfiles(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterProfiles(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterProfiles(searchField.getText());
            }
        });
    }

    private void refreshProfiles() {
        allProfiles = loadProfilesFromDatabase();
        updateCards(allProfiles);
        profileCountLabel.setText("Profils affichés : " + allProfiles.size());
    }

    private void filterProfiles(String searchText) {
        List<Object[]> filteredProfiles = new ArrayList<>();
        String searchLower = searchText.toLowerCase();

        for (Object[] profile : allProfiles) {
            String fullName = (profile[1] + " " + profile[2]).toLowerCase();
            if (fullName.contains(searchLower)) {
                filteredProfiles.add(profile);
            }
        }

        updateCards(filteredProfiles);
        profileCountLabel.setText("Profils affichés : " + filteredProfiles.size());
    }

    private void updateCards(List<Object[]> profiles) {
        cardsPanel.removeAll();
        for (Object[] profile : profiles) {
            int profilId = (int) profile[0];
            String name = profile[1] + " " + profile[2];
            String imagePath = (String) profile[9];
            JPanel card = createProfileCard(name, imagePath, profilId);
            cardsPanel.add(card);
            cardsPanel.add(Box.createVerticalStrut(15));
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createProfileCard(String name, String imagePath, int profilId) {
        JPanel card = new JPanel();
        card.setLayout(new GridBagLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(500, 100));
        card.setPreferredSize(new Dimension(500, 100));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.setBorder(new CompoundBorder(
            new DropShadowBorder(new Color(0, 0, 0, 50), 5, 0.3f, 10, true, true, true, true),
            card.getBorder()
        ));

        JLabel lblImage;
        // Chemin de l'image par défaut 
        String defaultImagePath = "image/user.png"; 

        try {
            // Vérifiez si imagePath est null ou vide
            if (imagePath == null || imagePath.trim().isEmpty()) {
                ImageIcon icon = new ImageIcon(defaultImagePath);
                Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                lblImage = new JLabel(new ImageIcon(scaledImage));
            } else {
                ImageIcon icon = new ImageIcon(imagePath);
                Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                lblImage = new JLabel(new ImageIcon(scaledImage));
            }
        } catch (Exception e) {
            // En cas d'erreur avec l'image par défaut ou l'image fournie
            lblImage = new JLabel("Pas d'image");
            lblImage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblImage.setForeground(new Color(150, 150, 150));
            lblImage.setPreferredSize(new Dimension(80, 80));
            lblImage.setHorizontalAlignment(JLabel.CENTER);
            lblImage.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        }

        GridBagConstraints gbc_lblImage = new GridBagConstraints();
        gbc_lblImage.gridx = 0;
        gbc_lblImage.gridy = 0;
        gbc_lblImage.insets = new Insets(0, 10, 0, 15);
        card.add(lblImage, gbc_lblImage);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblName.setForeground(new Color(0, 123, 255));

        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.gridx = 1;
        gbc_lblName.gridy = 0;
        gbc_lblName.weightx = 1.0;
        gbc_lblName.anchor = GridBagConstraints.WEST;
        card.add(lblName, gbc_lblName);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ProfileDetails(profilId);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_COLOR);
            }
        });

        return card;
    }

    private List<Object[]> loadProfilesFromDatabase() {
        List<Object[]> profiles = new ArrayList<>();
        try (Connection conn = Utils.connectToDatabase()) {
            if (conn == null) {
                System.err.println("Erreur : Connexion à la base de données échouée.");
                JOptionPane.showMessageDialog(null, "Impossible de se connecter à la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return profiles;
            }

            String query = "SELECT id, nom, prenom, age, sexe, email, telephone, adresse, nationalite, photo FROM profils";
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] profile = new Object[10];
                    profile[0] = rs.getInt("id");
                    profile[1] = rs.getString("nom");
                    profile[2] = rs.getString("prenom");
                    profile[3] = rs.getInt("age");
                    profile[4] = rs.getString("sexe");
                    profile[5] = rs.getString("email");
                    profile[6] = rs.getString("telephone");
                    profile[7] = rs.getString("adresse");
                    profile[8] = rs.getString("nationalite");
                    profile[9] = rs.getString("photo");
                    profiles.add(profile);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des profils : " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Erreur lors de la récupération des profils : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return profiles;
    }
}