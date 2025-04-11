package profiler_app;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Timer;
import java.util.TimerTask;
import java.nio.file.*;
import java.io.*;

public class WelcomeWindow {

    private JFrame frame;
    private JPanel contentPanel;
    private JLabel lblProfiler;
    private JButton btnStart;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                WelcomeWindow window = new WelcomeWindow();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public WelcomeWindow() {
        initializeDatabase();
        initialize();
    }

    private void initializeDatabase() {
        try {
            Path targetDir = Paths.get(System.getProperty("user.home"), "ProfilerApp", "db");
            Path targetPath = targetDir.resolve("profiles.db");

            Files.createDirectories(targetDir);

            Path sourcePath = Paths.get("database", "profiles.db");

            if (!Files.exists(targetPath)) {
                if (Files.exists(sourcePath)) {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Base de données copiée vers : " + targetPath);
                } else {
                    System.err.println("Fichier source profiles.db introuvable dans database/");
                    JOptionPane.showMessageDialog(null, "Base de données source introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la gestion de la base de données : " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Erreur lors de la copie de la base de données : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Profiler");
        frame.setBounds(100, 100, 650, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(100, 150, 255), getWidth(), getHeight(), new Color(50, 100, 200));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new BorderLayout());
        frame.setContentPane(gradientPanel);

        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(null);
        gradientPanel.add(contentPanel, BorderLayout.CENTER);

        showWelcomeView();
    }

    private void showWelcomeView() {
        contentPanel.removeAll();

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        titlePanel.setOpaque(false);
        titlePanel.setBounds(0, -100, 650, 100);

        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 200, 50));
                g2d.fillOval(0, 0, 40, 40);
            }
        };
        iconLabel.setPreferredSize(new Dimension(40, 40));
        titlePanel.add(iconLabel);

        lblProfiler = new JLabel("Profiler");
        lblProfiler.setFont(new Font("Segoe UI", Font.BOLD, 50));
        lblProfiler.setForeground(Color.WHITE);
        titlePanel.add(lblProfiler);

        contentPanel.add(titlePanel);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBounds(75, 150, 500, 250);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new DropShadowBorder(new Color(0, 0, 0, 100), 10, 0.5f, 20, true, true, true, true),
            BorderFactory.createLineBorder(new Color(150, 200, 255), 2, true)
        ));
        panel.setLayout(new BorderLayout());

        JLabel lblWelcome = new JLabel("Bienvenue dans Profiler !", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        lblWelcome.setForeground(new Color(40, 40, 40));
        panel.add(lblWelcome, BorderLayout.CENTER);

        contentPanel.add(panel);

        btnStart = new JButton("Commencer");
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnStart.setBackground(new Color(255, 150, 50));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFocusPainted(false);
        btnStart.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        btnStart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStart.setOpaque(true);
        btnStart.setBorderPainted(false);
        btnStart.setBounds(225, 500, 200, 60);

        btnStart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnStart.setBackground(new Color(255, 180, 80));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnStart.setBackground(new Color(255, 150, 50));
            }
        });

        btnStart.addActionListener(e -> showViewProfile());

        contentPanel.add(btnStart);

        animateEntrance();

        frame.revalidate();
        frame.repaint();
    }

    private void animateEntrance() {
        Timer timer = new Timer();
        final int[] titleY = {-100};
        final int[] buttonY = {500};
        final int targetTitleY = 50;
        final int targetButtonY = 410;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (titleY[0] < targetTitleY) {
                    titleY[0] += 5;
                    contentPanel.getComponent(0).setLocation(0, titleY[0]);
                }

                if (buttonY[0] > targetButtonY) {
                    buttonY[0] -= 5;
                    btnStart.setLocation(225, buttonY[0]);
                }

                if (titleY[0] >= targetTitleY && buttonY[0] <= targetButtonY) {
                    contentPanel.getComponent(0).setLocation(0, targetTitleY);
                    btnStart.setLocation(225, targetButtonY);
                    timer.cancel();
                }

                frame.repaint();
            }
        }, 0, 20);

        Timer pulseTimer = new Timer();
        pulseTimer.scheduleAtFixedRate(new TimerTask() {
            float scale = 1.0f;
            boolean growing = true;

            @Override
            public void run() {
                if (growing) {
                    scale += 0.01f;
                    if (scale >= 1.05f) growing = false;
                } else {
                    scale -= 0.01f;
                    if (scale <= 1.0f) growing = true;
                }
                btnStart.setFont(new Font("Segoe UI", Font.BOLD, (int) (18 * scale)));
                frame.repaint();
            }
        }, 500, 50);
    }

    private void showViewProfile() {
        contentPanel.removeAll();
        new ViewProfile(contentPanel);
        frame.revalidate();
        frame.repaint();
    }
}