package tictactoe.entity;

// class-definitie voor de player entiteit.
public class Player {
    // private-string om de unieke ID van de speler op te slaan.
    private int player_id;

    // private-string om de naam van de speler op te slaan.
    private String naam;

    // private-string om de code van de speler op te slaan.
    private String code;

    // private-string om de geboortedatum van de speler op te slaan.
    private String geboortedatum;

    // private-string om de score van de speler op te slaan.
    private int score;

    // Constructor om een Player object te initialiseren met naam, code en geboortedatum.
    // De score wordt standaard op 0 gezet.
    public Player(String naam, String code, String geboortedatum) {
        this.naam = naam;
        this.code = code;
        this.geboortedatum = geboortedatum;
        this.score = 0;
    }

    // Getter-methode om de speler-iD op te halen.
    public int getPlayerId() {
        return player_id;
    }

    // Setter-methode om de speler-ID in te stellen.
    public void setPlayerId(int player_id) {
        this.player_id = player_id;
    }

    // Getter-methode om de naam van de speler op te halen.
    public String getNaam() {
        return naam;
    }

    // Setter-methode om de naam van de speler in te stellen.
    public void setNaam(String naam) {
        this.naam = naam;
    }

    // Getter-methode om de code van de speler op te halen.
    public String getCode() {
        return code;
    }

    // Setter-methode om de code van de speler in te stellen.
    public void setCode(String code) {
        this.code = code;
    }

    // Getter-methode om de geboortedatum van de speler op te halen.
    public String getGeboortedatum() {
        return geboortedatum;
    }

    // Setter-methode om de geboortedatum van de speler in te stellen.
    public void setGeboortedatum(String geboortedatum) {
        this.geboortedatum = geboortedatum;
    }

    // Getter-methode om de score van de speler op te halen.
    public int getScore() {
        return score;
    }

    // Setter-methode om de score van de speler in te stellen.
    public void setScore(int score) {
        this.score = score;
    }
}
