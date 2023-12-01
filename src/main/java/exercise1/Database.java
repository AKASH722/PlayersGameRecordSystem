package exercise1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    Connection conn;

    Database() {
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:your_oracle_service", "your_username", "your_password");
//            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Player_Game_Record_System", "root", "root");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getGameId(String gameTitle) {
        int gameId = 0;
        try {
            String selectSql = "SELECT game_id FROM Game WHERE game_title = ?";
            PreparedStatement preparedStatement1 = conn.prepareStatement(selectSql);
            preparedStatement1.setString(1, gameTitle);
            ResultSet rs = preparedStatement1.executeQuery();
            if (rs.next()) {
                gameId = rs.getInt("game_id");
            } else {
//                String insertSql = "INSERT INTO Game (game_title) VALUES (?)";
                String insertSql = "INSERT INTO Game (game_id, game_title) VALUES (game_seq.NEXTVAL, ?)";
                PreparedStatement preparedStatement2 = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement2.setString(1, gameTitle);
                preparedStatement2.executeUpdate();
                ResultSet rs2 = preparedStatement2.getGeneratedKeys();
                if (rs2.next()) {
                    gameId = rs2.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return gameId;
    }

    public int insertPlayer(String firstName, String lastName, String address, String postalCode, String province, String phoneNumber) {
        int playerId = 0;
//        String sql = "INSERT INTO Player (first_name, last_name, address, postal_code, province, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
        String sql = "INSERT INTO Player (player_id, first_name, last_name, address, postal_code, province, phone_number) VALUES (player_seq.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, postalCode);
            preparedStatement.setString(5, province);
            preparedStatement.setString(6, phoneNumber);
            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    playerId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return playerId;
    }

    public int insertPlayerAndGame(int playerId, int gameId, Date playingDate, int score) {
        int playerGameId = 0;
//        String sql = "INSERT INTO PlayerAndGame (player_id, game_id, playing_date, score) VALUES (?, ?, ?, ?)";
        String sql = "INSERT INTO PlayerAndGame (player_game_id, player_id, game_id, playing_date, score) VALUES (player_game_seq.NEXTVAL, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, playerId);
            stmt.setInt(2, gameId);
            stmt.setDate(3, playingDate);
            stmt.setInt(4, score);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                playerGameId = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return playerGameId;
    }

    public ObservableList<Player> getData() {
        ObservableList<Player> data = FXCollections.observableArrayList();
        String sql = "SELECT p.player_id, p.first_name, p.last_name, p.address, p.postal_code, p.province, p.phone_number, g.game_title, pg.score, pg.playing_date " +
            "FROM Player p " +
            "JOIN PlayerAndGame pg ON p.player_id = pg.player_id " +
            "JOIN Game g ON pg.game_id = g.game_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int playerId = rs.getInt("player_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String address = rs.getString("address");
                String postalCode = rs.getString("postal_code");
                String province = rs.getString("province");
                String phoneNumber = rs.getString("phone_number");
                String gameTitle = rs.getString("game_title");
                int score = rs.getInt("score");
                Date playingDate = rs.getDate("playing_date");

                Player playerData = new Player(playerId, firstName + " " + lastName, address, postalCode, province, phoneNumber, gameTitle, score, playingDate.toLocalDate());
                data.add(playerData);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return data;
    }

    public boolean playerExists(int playerId) {
        boolean exists = false;
        String sql = "SELECT 1 FROM Player WHERE player_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    exists = true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return exists;
    }

    public void updateData(int playerId, String firstName, String lastName, String address, String postalCode, String province, String phoneNumber, String gameTitle, Date playingDate, int score) {
        try {
            if(firstName!=null || lastName!=null ||address!=null || postalCode!=null || province != null || phoneNumber!=null){
                StringBuilder queryBuilder = new StringBuilder("UPDATE Player SET ");
                List<String> values = new ArrayList<>();
                System.out.println(firstName);
                System.out.println(lastName);
                if (firstName != null) {
                    queryBuilder.append("first_name = ?, ");
                    values.add(firstName);
                }
                if (lastName != null) {
                    queryBuilder.append("last_name = ?, ");
                    values.add(lastName);
                }
                if (address != null) {
                    queryBuilder.append("address = ?, ");
                    values.add(address);
                }
                if (postalCode != null) {
                    queryBuilder.append("postal_code = ?, ");
                    values.add(postalCode);
                }
                if (province != null) {
                    queryBuilder.append("province = ?, ");
                    values.add(province);
                }
                if (phoneNumber != null) {
                    queryBuilder.append("phone_number = ?, ");
                    values.add(phoneNumber);
                }

                // Remove the last comma and space
                queryBuilder.setLength(queryBuilder.length() - 2);

                queryBuilder.append(" WHERE player_id = ?");

                PreparedStatement preparedStatement = conn.prepareStatement(queryBuilder.toString());
                int i;
                for (i = 0; i < values.size(); i++) {
                    preparedStatement.setString(i + 1, values.get(i));
                }
                preparedStatement.setInt(i + 1, playerId);

                preparedStatement.executeUpdate();
            }

            int gameId = gameTitle!=null ? getGameId(gameTitle) : getGameId(playerId);
            int playerGameId = getPlayerGameId(playerId);
            if(playingDate != null) {
                String query1 = "UPDATE PlayerAndGame SET player_id = ?, game_id = ?, playing_date = ? WHERE player_game_id = ?";
                PreparedStatement preparedStatement1 = conn.prepareStatement(query1);

                preparedStatement1.setInt(1, playerId);
                preparedStatement1.setInt(2, gameId);
                preparedStatement1.setDate(3, playingDate);
                preparedStatement1.setInt(4, playerGameId);

                preparedStatement1.executeUpdate();
            }
            if (score != -1) {
                String query1 = "UPDATE PlayerAndGame SET player_id = ?, game_id = ?, score = ? WHERE player_game_id = ?";
                PreparedStatement preparedStatement1 = conn.prepareStatement(query1);

                preparedStatement1.setInt(1, playerId);
                preparedStatement1.setInt(2, gameId);
                preparedStatement1.setInt(3, score);
                preparedStatement1.setInt(4, playerGameId);

                preparedStatement1.executeUpdate();
            } else {
                String query1 = "UPDATE PlayerAndGame SET player_id = ?, game_id = ? WHERE player_game_id = ?";
                PreparedStatement preparedStatement1 = conn.prepareStatement(query1);

                preparedStatement1.setInt(1, playerId);
                preparedStatement1.setInt(2, gameId);
                preparedStatement1.setInt(3, playerGameId);
                preparedStatement1.executeUpdate();

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private int getPlayerGameId(int playerId) throws SQLException {
        String query = "SELECT player_game_id FROM PlayerAndGame WHERE player_id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);

        preparedStatement.setInt(1, playerId);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("player_game_id");
        }
        return -1;
    }

    private int getGameId(int playerId) throws SQLException {
        String query = "SELECT game_id FROM PlayerAndGame WHERE player_id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, playerId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("game_id");
        }
        return -1;
    }

}