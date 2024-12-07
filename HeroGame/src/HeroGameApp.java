import java.sql.*;
import java.util.Scanner;

public class HeroGameApp {
    private Connection connection;

    public HeroGameApp() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/herogame?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "";

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    public void run() {
        connectToDatabase();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("HeroGame Menu:");
            System.out.println("1. Add Player");
            System.out.println("2. Remove Player");
            System.out.println("3. Add National");
            System.out.println("4. Remove National");
            System.out.println("5. Show All Players");
            System.out.println("6. Find Player by Name");
            System.out.println("7. Show Top 10 Players");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addPlayer(scanner);
                    break;
                case 2:
                    removePlayer(scanner);
                    break;
                case 3:
                    addNational(scanner);
                    break;
                case 4:
                    removeNational(scanner);
                    break;
                case 5:
                    showAllPlayers();
                    break;
                case 6:
                    findPlayerByName(scanner);
                    break;
                case 7:
                    showTop10Players();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    closeConnection();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void addPlayer(Scanner scanner) {
        try {
            System.out.print("Enter player name: ");
            String playerName = scanner.nextLine();
            System.out.print("Enter high score: ");
            int highScore = scanner.nextInt();
            System.out.print("Enter level: ");
            int level = scanner.nextInt();
            System.out.print("Enter national ID: ");
            int nationalId = scanner.nextInt();

            String query = "INSERT INTO Player (PlayerName, HighScore, Level, NationalId) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, playerName);
            stmt.setInt(2, highScore);
            stmt.setInt(3, level);
            stmt.setInt(4, nationalId);
            stmt.executeUpdate();

            System.out.println("Player added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removePlayer(Scanner scanner) {
        try {
            System.out.print("Enter player ID to remove: ");
            int playerId = scanner.nextInt();

            String query = "DELETE FROM Player WHERE PlayerId = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, playerId);
            stmt.executeUpdate();

            System.out.println("Player removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addNational(Scanner scanner) {
        try {
            System.out.print("Enter national name: ");
            String nationalName = scanner.nextLine();

            String query = "INSERT INTO National (NationalName) VALUES (?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nationalName);
            stmt.executeUpdate();

            System.out.println("National added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeNational(Scanner scanner) {
        try {
            System.out.print("Enter national ID to remove: ");
            int nationalId = scanner.nextInt();

            String query = "DELETE FROM National WHERE NationalId = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, nationalId);
            stmt.executeUpdate();

            System.out.println("National removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAllPlayers() {
        try {
            String query = "SELECT p.PlayerId, p.PlayerName, p.HighScore, p.Level, n.NationalName " +
                    "FROM Player p JOIN National n ON p.NationalId = n.NationalId";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.printf("%-10s %-20s %-10s %-10s %-20s\n", "PlayerId", "PlayerName", "HighScore", "Level",
                    "National");
            while (rs.next()) {
                System.out.printf("%-10d %-20s %-10d %-10d %-20s\n",
                        rs.getInt("PlayerId"),
                        rs.getString("PlayerName"),
                        rs.getInt("HighScore"),
                        rs.getInt("Level"),
                        rs.getString("NationalName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void findPlayerByName(Scanner scanner) {
        try {
            System.out.print("Enter player name to search: ");
            String name = scanner.nextLine();

            String query = "SELECT * FROM Player WHERE PlayerName LIKE ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            System.out.printf("%-10s %-20s %-10s %-10s\n", "PlayerId", "PlayerName", "HighScore", "Level");
            while (rs.next()) {
                System.out.printf("%-10d %-20s %-10d %-10d\n",
                        rs.getInt("PlayerId"),
                        rs.getString("PlayerName"),
                        rs.getInt("HighScore"),
                        rs.getInt("Level"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showTop10Players() {
        try {
            String query = "SELECT PlayerId, PlayerName, HighScore, Level FROM Player ORDER BY HighScore DESC LIMIT 10";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.printf("%-10s %-20s %-10s %-10s\n", "PlayerId", "PlayerName", "HighScore", "Level");
            while (rs.next()) {
                System.out.printf("%-10d %-20s %-10d %-10d\n",
                        rs.getInt("PlayerId"),
                        rs.getString("PlayerName"),
                        rs.getInt("HighScore"),
                        rs.getInt("Level"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
