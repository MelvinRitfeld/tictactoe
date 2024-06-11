package tictactoe;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;

import tictactoe.database.DatabaseManager;
import tictactoe.entity.Player;
import tictactoe.entity.Games;

public class Main {
    public static void main(String[] args) {
        // Maakt verbinding met de database.
        DatabaseManager.connectToDB();


        // Creëert een Scanner-object voor invoer.
        Scanner scanner = new Scanner(System.in);

        // Vraagt de eerste speler om in te loggen of te registreren.
        Player player1 = loginOrRegisterPlayer(scanner, 1);
        // Vraagt de tweede speler om in te loggen of te registreren.
        Player player2 = loginOrRegisterPlayer(scanner, 2);

        // Variabele om bij te houden of de spelers opnieuw willen spelen.
        boolean playAgain = true;
        while (playAgain) {
            // Print een bericht dat het spel begint.
            System.out.println("TICTACTOE!!");

            // Initialiseert het spelbord met lege posities.
            char[][] board = {
                    {'-', '-', '-'},
                    {'-', '-', '-'},
                    {'-', '-', '-'}
            };
            // Stelt de huidige speler in op 'X'.
            char currentPlayer = 'X';
            // Stelt het huidige spelerobject in op player1.
            Player currentPlayerObj = player1;

            while (true) {
                // Print het spelbord.
                printBoard(board);
                int row, col;
                // Vraagt de huidige speler om een zet te doen totdat een geldige zet wordt ingevoerd.
                do {
                    System.out.print("Speler " + currentPlayer + ", voer je zet in (rij en kolom): ");
                    row = scanner.nextInt() - 1; // Vraagt om de rij (0-indexed)
                    col = scanner.nextInt() - 1; // Vraagt om de kolom (0-indexed)
                } while (row < 0 || row >= 3 || col < 0 || col >= 3 || board[row][col] != '-');

                // Plaatst de zet van de huidige speler op het bord.
                board[row][col] = currentPlayer;

                // Controleert of de huidige speler heeft gewonnen.
                if (checkWin(board, currentPlayer)) {
                    // Print het spelbord en een winnaarsbericht.
                    printBoard(board);
                    System.out.println("Speler " + currentPlayer + " wint!");
                    // Verhoogt de score van de huidige speler.
                    currentPlayerObj.setScore(currentPlayerObj.getScore() + 1);
                    try {
                        // Werkt de score van de huidige speler bij in de database.
                        DatabaseManager.updatePlayerScore(currentPlayerObj);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break; // Beëindigt de huidige spelronde.
                } else if (isBoardFull(board)) {
                    // Controleert of het bord vol is (gelijkspel).
                    printBoard(board);
                    System.out.println("Het spel is een gelijkspel!");
                    break; // Beëindigt de huidige spelronde.
                }

                // Wisselt van speler (X wordt O en O wordt X).
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                // Wisselt het spelerobject (player1 wordt player2 en vice versa).
                currentPlayerObj = (currentPlayerObj == player1) ? player2 : player1;
            }

            // Vraagt of de spelers nog een spel willen spelen.
            System.out.println("Wil je nog een spel spelen? (ja/nee)");
            scanner.nextLine(); // Verbruikt de nieuwe regel.
            String response = scanner.nextLine();
            if (!response.equalsIgnoreCase("ja")) {
                playAgain = false; // Beëindigt de while-lus als de spelers niet opnieuw willen spelen.
            }
        }

        // Sluit de scanner om resourcelekkage te voorkomen.
        scanner.close();

        // Print de top 10 spelers.
        printTop10Players();

        try {
            // Sluit de databaseverbinding.
            DatabaseManager.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Player loginOrRegisterPlayer(Scanner scanner, int playerNumber) {
        // Vraagt de speler om te kiezen tussen inloggen of registreren.
        System.out.println("Speler " + playerNumber + ", wil je (1) Inloggen of (2) Registreren?");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Verbruikt de nieuwe regel.

        if (choice == 1) {
            // Roept de methode aan om in te loggen.
            return loginPlayer(scanner);
        } else {
            // Roept de methode aan om te registreren.
            return registerPlayer(scanner);
        }
    }

    private static Player loginPlayer(Scanner scanner) {
        // Vraagt om de naam van de speler.
        System.out.print("Voer je naam in: ");
        String naam = scanner.nextLine();
        // Vraagt om de code van de speler.
        System.out.print("Voer je code in: ");
        String code = scanner.nextLine();

        Player player = null;
        try {
            // Haalt de speler op uit de database met de opgegeven naam en code.
            player = DatabaseManager.getPlayerByNaamAndCode(naam, code);
            if (player == null) {
                // Geeft een foutmelding bij ongeldige naam of code en vraagt opnieuw in te loggen.
                System.out.println("Ongeldige naam of code. Probeer het opnieuw.");
                return loginPlayer(scanner);
            } else {
                // Bevestigt een succesvolle login.
                System.out.println("Inloggen succesvol.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return player; // Geeft het spelerobject terug.
    }

    private static Player registerPlayer(Scanner scanner) {
        // Vraagt om de naam van de speler.
        System.out.print("Voer je naam in: ");
        String naam = scanner.nextLine();
        // Vraagt om de code van de speler.
        System.out.print("Voer je code in: ");
        String code = scanner.nextLine();
        // Vraagt om de geboortedatum van de speler.
        System.out.print("Voer je geboortedatum in (jjjj-mm-dd): ");
        String geboortedatum = scanner.nextLine();

        // Maakt een nieuw spelerobject aan met de opgegeven gegevens.
        Player player = new Player(naam, code, geboortedatum);
        try {
            // Voegt de nieuwe speler toe aan de database.
            DatabaseManager.insertPlayer(player);
            System.out.println("Registratie succesvol.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return player; // Geeft het nieuwe spelerobject terug.
    }

    public static void printBoard(char[][] board) {
        // Print het spelbord met kleur voor X (rood) en O (blauw).
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 'X') {
                    System.out.print("\u001B[31m" + board[i][j] + "\u001B[0m ");
                } else if (board[i][j] == 'O') {
                    System.out.print("\u001B[34m" + board[i][j] + "\u001B[0m ");
                } else {
                    System.out.print(board[i][j] + " ");
                }
            }
            System.out.println(); // Nieuwe regel na elke rij.
        }
    }

    public static boolean checkWin(char[][] board, char player) {
        // Controleert of een speler heeft gewonnen.
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true; // Controleert rijen.
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true; // Controleert kolommen.
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true; // Controleert diagonalen.
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) return true; // Controleert diagonalen.
        return false; // Geen winst gevonden.
    }

    public static boolean isBoardFull(char[][] board) {
        // Controleert of het bord vol is (geen lege posities).
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false; // Er is nog een lege positie.
                }
            }
        }
        return true; // Bord is vol.
    }

    private static void printTop10Players() {
        try {
            // Haalt de top 10 spelers op uit de database en print ze.
            List<Player> topPlayers = DatabaseManager.getTop10Players();
            System.out.println("Top 10 Spelers:");
            for (Player player : topPlayers) {
                System.out.println(player.getNaam() + " - " + player.getScore() + " overwinningen");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

