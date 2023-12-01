# PlayersGameRecordSystem
Source Code: src/main/java/com/PlayersGameRecordSystem

### Database

CREATE TABLE Game (
                      game_id INT AUTO_INCREMENT PRIMARY KEY,
                      game_title VARCHAR(100)
);

CREATE TABLE Player (
                        player_id INT AUTO_INCREMENT PRIMARY KEY,
                        first_name VARCHAR(50),
                        last_name VARCHAR(50),
                        address VARCHAR(100),
                        postal_code VARCHAR(10),
                        province VARCHAR(50),
                        phone_number VARCHAR(20)
);

CREATE TABLE PlayerAndGame (
                               player_game_id INT AUTO_INCREMENT PRIMARY KEY,
                               player_id INT,
                               game_id INT,
                               playing_date DATE,
                               score INT,
                               FOREIGN KEY (player_id) REFERENCES Player(player_id),
                               FOREIGN KEY (game_id) REFERENCES Game(game_id)
);
