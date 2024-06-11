package tictactoe.entity;

// class-definitie voor de games-entiteit.
public class Games {
    // private int om de ID van de game op te slaan.
    private int game_id;

    // private int om de ID van de speler die de game speelt op te slaan.
    private int player_id;

    // Constructor voor het initialiseren van een game met alleen een speler-ID.
    public Games(int player_id) {
        this.player_id = player_id;
    }

    // Volledige constructor voor het initialiseren van een game met een speler-ID en een game-ID.
    public Games(int player_id, int game_id) {
        this.player_id = player_id;
        this.game_id = game_id;
    }

    // Getter-methode om de game-ID op te halen.
    public int getGameId() {
        return game_id;
    }

    // Setter-methode om de game-ID in te stellen.
    public void setGameId(int game_id) {
        this.game_id = game_id;
    }

    // Getter-methode om de speler-ID op te halen.
    public int getPlayerId() {
        return player_id;
    }

    // Setter-methode om de speler-ID in te stellen.
    public void setPlayerId(int player_id) {
        this.player_id = player_id;
    }
}
