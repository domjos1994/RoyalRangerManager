
-- properties table
CREATE TABLE properties(
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    'key' VARCHAR(2000) NOT NULL,
    'value' VARCHAR(5000) NOT NULL
);


-- this is the last line
INSERT INTO properties('key', 'value') VALUES('version', '1');