package com.playersgamerecordsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    Connection conn;

    Database() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Player_Game_Record_System", "root", "root");
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
                String insertSql = "INSERT INTO Game (game_title) VALUES (?)";
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

    public int updatePlayerAndGame(int playerId, int gameId, Date playingDate, int score) {
        int playerGameId = 0;
        String selectSql = "SELECT player_game_id FROM PlayerAndGame WHERE player_id = ? AND game_id = ?";
        String updateSql = "UPDATE PlayerAndGame SET playing_date = ?, score = ? WHERE player_game_id = ?";

        try (PreparedStatement selectPstmt = conn.prepareStatement(selectSql)) {
            selectPstmt.setInt(1, playerId);
            selectPstmt.setInt(2, gameId);
            try (ResultSet rs = selectPstmt.executeQuery()) {
                if (rs.next()) {
                    playerGameId = rs.getInt("player_game_id");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if (playerGameId > 0) {
            try (PreparedStatement updater = conn.prepareStatement(updateSql)) {
                updater.setDate(1, playingDate);
                updater.setInt(2, score);
                updater.setInt(3, playerGameId);
                updater.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return playerGameId;
    }


    public int insertPlayer(String firstName, String lastName, String address, String postalCode, String province, String phoneNumber) {
        int playerId = 0;
        String sql = "INSERT INTO Player (first_name, last_name, address, postal_code, province, phone_number) VALUES (?, ?, ?, ?, ?, ?)";

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
        String sql = "INSERT INTO PlayerAndGame (player_id, game_id, playing_date, score) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, gameId);
            pstmt.setDate(3, playingDate);
            pstmt.setInt(4, score);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
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

    public int getGameId(int playerId) {
        int gameId = 0;
        String sql = "SELECT game_id FROM PlayerAndGame WHERE player_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    gameId = rs.getInt("game_id");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return gameId;
    }


    public void updateData(int playerId, String firstName, String lastName, String address, String postalCode, String province, String phoneNumber, String gameTitle, Date playingDate, String score) {
        StringBuilder sql = new StringBuilder("UPDATE players SET ");
        List<Object> values = new ArrayList<>();
        if (!firstName.isEmpty()) {
            sql.append("first_name = ?, ");
            values.add(firstName);
        }
        if (!lastName.isEmpty()) {
            sql.append("last_name = ?, ");
            values.add(lastName);
        }
        if (!address.isEmpty()) {
            sql.append("address = ?, ");
            values.add(address);
        }
        if (!province.isEmpty()) {
            sql.append("province = ?, ");
            values.add(province);
        }
        if (!postalCode.isEmpty()) {
            sql.append("postal_code = ?, ");
            values.add(postalCode);
        }
        if (!phoneNumber.isEmpty()) {
            sql.append("phone_number = ?, ");
            values.add(phoneNumber);
        }
        if (!values.isEmpty()) {
            sql.setLength(sql.length() - 2); // Remove trailing comma and space
            sql.append(" WHERE id = ?");
            values.add(playerId);
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    pstmt.setObject(i + 1, values.get(i));
                }
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        int gameId;
        if(!gameTitle.isEmpty()) {
            gameId = getGameId(gameTitle);
            updatePlayerAndGame(playerId,gameId);
        } else {
            gameId = getGameId(playerId);
        }
        if(!score.isEmpty() && playingDate !=null) {
            updatePlayerAndGame(playerId,gameId,playingDate,Integer.parseInt(score));
        } else if(!score.isEmpty()) {
            updatePlayerAndGame(playerId,gameId,Integer.parseInt(score));
        } else if( playingDate !=null){
            updatePlayerAndGame(playerId,gameId,playingDate);
        }
    }

    private void updatePlayerAndGame(int playerId, int gameId) {
        int playerGameId = 0;
        String selectSql = "SELECT player_game_id FROM PlayerAndGame WHERE player_id = ?";
        String updateSql = "UPDATE PlayerAndGame SET game_id = ? WHERE player_game_id = ?";

        try (PreparedStatement selectPstmt = conn.prepareStatement(selectSql)) {
            selectPstmt.setInt(1, playerId);
            try (ResultSet rs = selectPstmt.executeQuery()) {
                if (rs.next()) {
                    playerGameId = rs.getInt("player_game_id");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if (playerGameId > 0) {
            try (PreparedStatement updater = conn.prepareStatement(updateSql)) {
                updater.setInt(1, gameId);
                updater.setInt(2, playerGameId);
                updater.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public int updatePlayerAndGame(int playerId, int gameId, int score) {
        int playerGameId = 0;
        String selectSql = "SELECT player_game_id FROM PlayerAndGame WHERE player_id = ? AND game_id = ?";
        String updateSql = "UPDATE PlayerAndGame SET score = ? WHERE player_game_id = ?";

        try (PreparedStatement selectPstmt = conn.prepareStatement(selectSql)) {
            selectPstmt.setInt(1, playerId);
            selectPstmt.setInt(2, gameId);
            try (ResultSet rs = selectPstmt.executeQuery()) {
                if (rs.next()) {
                    playerGameId = rs.getInt("player_game_id");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if (playerGameId > 0) {
            try (PreparedStatement updater = conn.prepareStatement(updateSql)) {
                updater.setInt(1, score);
                updater.setInt(2, playerGameId);
                updater.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return playerGameId;
    }

    public int updatePlayerAndGame(int playerId, int gameId, Date playingDate) {
        int playerGameId = 0;
        String selectSql = "SELECT player_game_id FROM PlayerAndGame WHERE player_id = ? AND game_id = ?";
        String updateSql = "UPDATE PlayerAndGame SET playing_date = ? WHERE player_game_id = ?";

        try (PreparedStatement selectPstmt = conn.prepareStatement(selectSql)) {
            selectPstmt.setInt(1, playerId);
            selectPstmt.setInt(2, gameId);
            try (ResultSet rs = selectPstmt.executeQuery()) {
                if (rs.next()) {
                    playerGameId = rs.getInt("player_game_id");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if (playerGameId > 0) {
            try (PreparedStatement updater = conn.prepareStatement(updateSql)) {
                updater.setDate(1, playingDate);
                updater.setInt(2, playerGameId);
                updater.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return playerGameId;
    }

}