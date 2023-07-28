package exercise1;

import java.time.LocalDate;

public class Player {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String province;
    private String phoneNumber;
    private String gameTitle;
    private int score;
    private LocalDate datePlayed;

    public Player(int id, String name, String address, String postalCode, String province, String phoneNumber,
                  String gameTitle, int score, LocalDate datePlayed) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.province = province;
        this.phoneNumber = phoneNumber;
        this.gameTitle = gameTitle;
        this.score = score;
        this.datePlayed = datePlayed;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getProvince() {
        return province;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public int getScore() {
        return score;
    }

    public LocalDate getDatePlayed() {
        return datePlayed;
    }
}
