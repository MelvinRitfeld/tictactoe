package tictactoe.database; // Dit vertelt de computer dat alle database-code in dit bestand hoort bij het 'tictactoe.database'-pakket.

import tictactoe.entity.Player; // We gebruiken deze stukjes code om met spelers en spellen te werken.
import tictactoe.entity.Games;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager { // Dit is als een speciale doos waarin al onze database-spullen zitten.

    private static final String USERNAME = "root"; //  gebruikersnaam voor de database.
    private static final String PASSWORD = "Dic@_kylian1$"; //  wachtwoord voor de database.
    private static final String URL = "jdbc:mysql://localhost:3306/tictactoe"; //  het adres van de database.

    private static Connection connection; // eigenworden dit is waarmee we met de database kunnen praten.

    // Deze methode maakt verbinding met de database. Als er nog geen verbinding is, maakt het een nieuwe verbinding.
    public static Connection connectToDB() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Verbinding is stabiel en actief: " + connection.getClientInfo());// als er een verbinding is zal er een text geplaatst worden
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return connection; // We zeggen tegen de computer dat we nu met de database kunnen praten.
    }

    // Deze methode voegt een nieuwe speler toe aan de database.
    public static int insertPlayer(Player player) throws SQLException {
        String sql = "INSERT INTO player (naam, code, geboortedatum, score) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, player.getNaam()); // We schrijven de naam van de speler bij elkaar.
        statement.setString(2, player.getCode()); // We schrijven de code van de speler bij elkaar.
        statement.setString(3, player.getGeboortedatum()); // We schrijven de geboortedatum van de speler bij elkaar.
        statement.setInt(4, player.getScore()); // We schrijven de score van de speler bij elkaar.

        int affectedRows = statement.executeUpdate(); // We geven alles aan de database.
        if (affectedRows == 0) { // Als de database het niet begrijpt...
            throw new SQLException("Er is iets fout gegaan bij het toevoegen van de speler."); // ...vertellen we de computer dat er iets mis is gegaan.
        }

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) { // Als de database het wel begrijpt
            if (generatedKeys.next()) {
                player.setPlayerId(generatedKeys.getInt(1)); // krijgen we een nummer terug dat de database heeft gemaakt voor deze nieuwe speler.
            } else {
                throw new SQLException("We konden geen ID krijgen voor de speler."); // Als er iets misgaat, vertellen we de computer het.
            }
        }

        statement.close(); // We vertellen de computer dat we klaar zijn.
        return player.getPlayerId(); // We vertellen de computer om het nummer van de nieuwe speler te onthouden voor later gebruik.
    }

    // Deze methode voegt een nieuw spel toe aan de database.
    public static int insertGames(Games games) throws SQLException {
        String sql = "INSERT INTO games (player_id) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, games.getPlayerId());

        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating game failed, no rows affected.");
        }

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                games.setGameId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating game failed, no ID obtained.");
            }
        }

        statement.close();
        return games.getGameId();
    }

    // Deze methode werkt de score van een speler bij in de database.
    public static void updatePlayerScore(Player player) throws SQLException {
        String sql = "UPDATE player SET score = ? WHERE player_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, player.getScore());
        statement.setInt(2, player.getPlayerId());

        statement.executeUpdate();
        statement.close();
    }

    // Deze methode zoekt een speler op basis van hun naam en code in de database.
    public static Player getPlayerByNaamAndCode(String naam, String code) throws SQLException {
        String sql = "SELECT * FROM player WHERE naam = ? AND code = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, naam);
        statement.setString(2, code);

        ResultSet resultSet = statement.executeQuery();
        Player player = null;
        if (resultSet.next()) {
            player = new Player(
                    resultSet.getString("naam"),
                    resultSet.getString("code"),
                    resultSet.getDate("geboortedatum").toString()
            );
            player.setPlayerId(resultSet.getInt("player_id"));
            player.setScore(resultSet.getInt("score"));
        }

        resultSet.close();
        statement.close();
        return player;
    }

    // Deze methode haalt de top 10 spelers op uit de database op basis van hun score.
    public static List<Player> getTop10Players() throws SQLException {
        String sql = "SELECT * FROM player ORDER BY score DESC LIMIT 10"; // We maken een SQL-query die de top 10 spelers selecteert op basis van hun score.
        PreparedStatement statement = connection.prepareStatement(sql); // We maken een PreparedStatement-object aan met de SQL-query.

        ResultSet resultSet = statement.executeQuery(); // We voeren de query uit en slaan het resultaat op in een ResultSet-object.
        List<Player> players = new ArrayList<>(); // We maken een lege lijst om de top 10 spelers in op te slaan.

        while (resultSet.next()) { // Voor elke speler in het resultaat:
            Player player = new Player( // We maken een nieuw Player-object aan met de gegevens uit de database.
                    resultSet.getString("naam"), // We halen de naam van de speler op.
                    resultSet.getString("code"), // We halen de code van de speler op.
                    resultSet.getDate("geboortedatum").toString() // We halen de geboortedatum van de speler op en zetten deze om naar een string.
            );
            player.setPlayerId(resultSet.getInt("player_id")); // We stellen de speler-ID in.
            player.setScore(resultSet.getInt("score")); // We stellen de score van de speler in.
            players.add(player); // We voegen de speler toe aan de lijst met top 10 spelers.
        }

        resultSet.close(); // We sluiten het ResultSet om memory te besparen.
        statement.close(); // We sluiten de PreparedStatement om memory te besparen.

        return players; // We retourneren de lijst met top 10 spelers.
    }


    // Deze methode sluit de verbinding met de database.
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

