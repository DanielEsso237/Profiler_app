package profiler_app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddProfile extends JFrame {

    private final HashMap<String, JTextField> fieldMap = new HashMap<>();
    private JTextField photoField;
    private List<JPanel> competencePanels = new ArrayList<>();
    private List<JPanel> diplomePanels = new ArrayList<>();
    private List<JPanel> experiencePanels = new ArrayList<>();
    private List<JPanel> languePanels = new ArrayList<>();
    private List<JPanel> visitePanels = new ArrayList<>();

    public AddProfile() {
        setTitle("Ajouter un Profil");
        setSize(700, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel container = new JPanel();
        container.setLayout(new BorderLayout(10, 10));
        container.setBackground(new Color(240, 248, 255));

        // Titre
        JLabel title = new JLabel("Ajouter un Profil", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 123, 255));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        container.add(title, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Champs pour "profils"
        String[] profilFields = {"Nom", "Prenom", "Age", "Sexe", "Email", "Téléphone", "Adresse", "Nationalité", "Photo (chemin)"};
        JPanel profilSection = createSectionPanel("Informations personnelles", profilFields, false);
        formPanel.add(profilSection);

        // Sections dynamiques
        formPanel.add(createDynamicSection("Compétences", new String[]{"Compétence", "Niveau compétence"}, competencePanels));
        formPanel.add(createDynamicSection("Diplômes", new String[]{"Niveau d’études", "Domaine", "Établissement", "Année d’obtention"}, diplomePanels));
        formPanel.add(createDynamicSection("Expériences", new String[]{"Poste", "Entreprise", "Année début", "Année fin", "Description expérience"}, experiencePanels));
        formPanel.add(createDynamicSection("Langues", new String[]{"Langue", "Niveau langue"}, languePanels));
        formPanel.add(createDynamicSection("Visites médicales", new String[]{"Date visite (YYYY-MM-DD)", "Notes visite"}, visitePanels));

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        container.add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(new Color(255, 255, 255));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        JButton saveBtn = new JButton("Enregistrer");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setBackground(new Color(40, 167, 69));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> {
            if (saveProfileToDatabase()) {
                JOptionPane.showMessageDialog(this, "Profil ajouté avec succès !");
                dispose();
            }
        });

        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setBackground(new Color(108, 117, 125));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        container.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(container);
        setVisible(true);
    }

    private JPanel createSectionPanel(String title, String[] fields, boolean isDynamic) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new GridLayout(0, 2, 10, 10));
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 123, 255)), title,
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 16), new Color(0, 123, 255)));

        for (String label : fields) {
            JLabel lbl = new JLabel(label + " :");
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JTextField field = new JTextField();
            if (label.equals("Photo (chemin)")) {
                photoField = field;
                JButton choosePhotoButton = new JButton("Choisir");
                choosePhotoButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                choosePhotoButton.setBackground(new Color(0, 123, 255));
                choosePhotoButton.setForeground(Color.WHITE);
                choosePhotoButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "png", "gif"));
                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        photoField.setText(selectedFile.getAbsolutePath());
                    }
                });
                JPanel photoPanel = new JPanel(new BorderLayout(5, 0));
                photoPanel.setOpaque(false);
                photoPanel.add(field, BorderLayout.CENTER);
                photoPanel.add(choosePhotoButton, BorderLayout.EAST);
                sectionPanel.add(lbl);
                sectionPanel.add(photoPanel);
            } else {
                fieldMap.put(label, field);
                sectionPanel.add(lbl);
                sectionPanel.add(field);
            }
        }
        return sectionPanel;
    }

    private JPanel createDynamicSection(String title, String[] fields, List<JPanel> panelList) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 123, 255)), title,
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 16), new Color(0, 123, 255)));

        JButton addButton = new JButton("Ajouter " + title.substring(0, title.length() - 1));
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addButton.setBackground(new Color(0, 123, 255));
        addButton.setForeground(Color.WHITE);
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.addActionListener(e -> {
            JPanel entryPanel = createEntryPanel(fields, panelList);
            sectionPanel.add(entryPanel);
            sectionPanel.revalidate();
            sectionPanel.repaint();
        });

        sectionPanel.add(addButton);
        panelList.add(sectionPanel);
        return sectionPanel;
    }

    private JPanel createEntryPanel(String[] fields, List<JPanel> panelList) {
        JPanel entryPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        entryPanel.setOpaque(false);
        entryPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        HashMap<String, JTextField> entryFields = new HashMap<>();

        for (String label : fields) {
            JLabel lbl = new JLabel(label + " :");
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JTextField field = new JTextField();
            entryFields.put(label, field);
            entryPanel.add(lbl);
            entryPanel.add(field);
        }

        JButton removeButton = new JButton("Supprimer");
        removeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        removeButton.setBackground(new Color(220, 53, 69));
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(e -> {
            panelList.get(0).remove(entryPanel);
            panelList.get(0).revalidate();
            panelList.get(0).repaint();
        });

        entryPanel.add(new JLabel(""));
        entryPanel.add(removeButton);
        panelList.add(entryPanel);
        return entryPanel;
    }

    private boolean saveProfileToDatabase() {
        try (Connection conn = Utils.connectToDatabase()) {
            if (conn == null) return false;

            conn.setAutoCommit(false);

            // Vérification des champs obligatoires
            if (fieldMap.get("Nom").getText().isEmpty() || fieldMap.get("Prenom").getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le nom et le prénom sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Insérer dans "profils"
            String insertProfilSQL = "INSERT INTO profils (nom, prenom, age, sexe, email, telephone, adresse, nationalite, photo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int newProfilId;
            try (PreparedStatement ps = conn.prepareStatement(insertProfilSQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, fieldMap.get("Nom").getText());
                ps.setString(2, fieldMap.get("Prenom").getText());
                String ageText = fieldMap.get("Age").getText();
                ps.setInt(3, ageText.isEmpty() ? 0 : Integer.parseInt(ageText));
                ps.setString(4, fieldMap.get("Sexe").getText());
                ps.setString(5, fieldMap.get("Email").getText());
                ps.setString(6, fieldMap.get("Téléphone").getText());
                ps.setString(7, fieldMap.get("Adresse").getText());
                ps.setString(8, fieldMap.get("Nationalité").getText());
                ps.setString(9, photoField.getText());
                ps.executeUpdate();

                // Récupérer l’ID généré
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        newProfilId = rs.getInt(1);
                    } else {
                        throw new SQLException("Échec de la récupération de l’ID du profil.");
                    }
                }
            }

            // Insérer les compétences
            for (JPanel panel : competencePanels.subList(1, competencePanels.size())) {
                JTextField competenceField = (JTextField) panel.getComponent(1);
                JTextField niveauField = (JTextField) panel.getComponent(3);
                if (!competenceField.getText().isEmpty()) {
                    String insertSQL = "INSERT INTO competences (profil_id, competence, niveau) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                        ps.setInt(1, newProfilId);
                        ps.setString(2, competenceField.getText());
                        ps.setString(3, niveauField.getText());
                        ps.executeUpdate();
                    }
                }
            }

            // Insérer les diplômes
            for (JPanel panel : diplomePanels.subList(1, diplomePanels.size())) {
                JTextField niveauField = (JTextField) panel.getComponent(1);
                if (!niveauField.getText().isEmpty()) {
                    String insertSQL = "INSERT INTO diplomes (profil_id, niveau_etudes, domaine, etablissement, annee_obtention) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                        ps.setInt(1, newProfilId);
                        ps.setString(2, niveauField.getText());
                        ps.setString(3, ((JTextField) panel.getComponent(3)).getText());
                        ps.setString(4, ((JTextField) panel.getComponent(5)).getText());
                        String annee = ((JTextField) panel.getComponent(7)).getText();
                        ps.setInt(5, annee.isEmpty() ? 0 : Integer.parseInt(annee));
                        ps.executeUpdate();
                    }
                }
            }

            // Insérer les expériences
            for (JPanel panel : experiencePanels.subList(1, experiencePanels.size())) {
                JTextField posteField = (JTextField) panel.getComponent(1);
                if (!posteField.getText().isEmpty()) {
                    String insertSQL = "INSERT INTO experiences (profil_id, poste, entreprise, annee_debut, annee_fin, description) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                        ps.setInt(1, newProfilId);
                        ps.setString(2, posteField.getText());
                        ps.setString(3, ((JTextField) panel.getComponent(3)).getText());
                        String debut = ((JTextField) panel.getComponent(5)).getText();
                        ps.setInt(4, debut.isEmpty() ? 0 : Integer.parseInt(debut));
                        String fin = ((JTextField) panel.getComponent(7)).getText();
                        if (fin.isEmpty()) ps.setNull(5, java.sql.Types.INTEGER);
                        else ps.setInt(5, Integer.parseInt(fin));
                        ps.setString(6, ((JTextField) panel.getComponent(9)).getText());
                        ps.executeUpdate();
                    }
                }
            }

            // Insérer les langues
            for (JPanel panel : languePanels.subList(1, languePanels.size())) {
                JTextField langueField = (JTextField) panel.getComponent(1);
                if (!langueField.getText().isEmpty()) {
                    String insertSQL = "INSERT INTO langues (profil_id, langue, niveau) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                        ps.setInt(1, newProfilId);
                        ps.setString(2, langueField.getText());
                        ps.setString(3, ((JTextField) panel.getComponent(3)).getText());
                        ps.executeUpdate();
                    }
                }
            }

            // Insérer les visites médicales
            for (JPanel panel : visitePanels.subList(1, visitePanels.size())) {
                JTextField dateField = (JTextField) panel.getComponent(1);
                if (!dateField.getText().isEmpty()) {
                    String insertSQL = "INSERT INTO visites_medicales (profil_id, date_visite, notes) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                        ps.setInt(1, newProfilId);
                        ps.setString(2, dateField.getText());
                        ps.setString(3, ((JTextField) panel.getComponent(3)).getText());
                        ps.executeUpdate();
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l’ajout : " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erreur lors de l’ajout : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Erreur de format numérique : " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Vérifiez les champs numériques : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddProfile());
    }
}