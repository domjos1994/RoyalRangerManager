
-- properties table
CREATE TABLE IF NOT EXISTS properties(
    ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `key` TEXT NOT NULL,
    `value` TEXT NOT NULL
) ENGINE=INNODB;


-- this is the last line
INSERT INTO properties(`key`, `value`) VALUES('version', '1');