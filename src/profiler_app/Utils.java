package profiler_app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Paths;

public class Utils {
    public static Connection connectToDatabase() {
        try {
            // Chemin dans le répertoire utilisateur : %USERPROFILE%\ProfilerApp\db\profiles.db
            String dbPath = Paths.get(System.getProperty("user.home"), "ProfilerApp", "db", "profiles.db").toString();
            String url = "jdbc:sqlite:" + dbPath;
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            return null;
        }
    }
}