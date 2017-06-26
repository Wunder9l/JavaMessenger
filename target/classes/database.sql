-- Table: userEntities
CREATE TABLE userEntities (
  id       INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
)
  ENGINE = InnoDB;

CREATE TABLE chatEntities (
  id   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100)
)
  ENGINE = InnoDB;

CREATE TABLE user_chats (
  user_id INT NOT NULL,
  chat_id INT NOT NULL,

  FOREIGN KEY (chat_id) REFERENCES chatEntities (id),
  FOREIGN KEY (user_id) REFERENCES userEntities (id)
)
  ENGINE = InnoDB;

CREATE TABLE messageEntities (
  id       INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
  datetime DATETIME      NOT NULL,
  netMessage  VARCHAR(1000) NOT NULL,

  chat_id  INT           NOT NULL,
  user_id  INT           NOT NULL,

  FOREIGN KEY (chat_id) REFERENCES chatEntities (id),
  FOREIGN KEY (user_id) REFERENCES userEntities (id)
)
  ENGINE = InnoDB;


