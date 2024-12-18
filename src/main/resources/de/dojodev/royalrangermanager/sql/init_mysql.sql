
-- properties table
CREATE TABLE IF NOT EXISTS properties(
    ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `key` TEXT NOT NULL,
    `value` TEXT NOT NULL
) ENGINE=INNODB;

-- general tables
CREATE TABLE users(
    ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    userName VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    gender INT NOT NULL,
    email VARCHAR(255) NOT NULL,
    admin BIT DEFAULT 0,
    trunkLeader BIT DEFAULT 0,
    trunkWait BIT DEFAULT 0,
    trunkHelper BIT DEFAULT 0,
    leader BIT DEFAULT 0,
    juniorLeader BIT DEFAULT 0
) ENGINE=INNODB;

CREATE TABLE ageGroups(
    ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nameEn VARCHAR(255) NOT NULL,
    nameDe VARCHAR(255) NOT NULL,
    minAge INT NOT NULL,
    maxAge INT NOT NULL
) ENGINE=INNODB;

CREATE TABLE teams(
    ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT DEFAULT '',
    note TEXT DEFAULT '',
    gender INT NOT NULL,
    ageGroupId INT NOT NULL,
    FOREIGN KEY(ageGroupId) REFERENCES ageGroups(ID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB;

CREATE TABLE teams_users(
    ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    teamId INT NOT NULL,
    userId INT NOT NULL,
    FOREIGN KEY(teamId) REFERENCES teams(ID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(userId) REFERENCES users(ID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB;

-- people tables
CREATE TABLE people(
    ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(255) NOT NULL,
    middleName VARCHAR(255) DEFAULT '',
    lastName VARCHAR(255) NOT NULL,
    gender INT NOT NULL,
    birthDate DATE NOT NULL,
    notes TEXT DEFAULT '',
    description TEXT DEFAULT '',
    medicines TEXT DEFAULT '',
    email VARCHAR(255) DEFAULT '',
    phone VARCHAR(255) DEFAULT '',
    street VARCHAR(255) DEFAULT '',
    number VARCHAR(5) DEFAULT '',
    locality VARCHAR(255) DEFAULT '',
    postalCode VARCHAR(10) DEFAULT '',
    ageGroupId INT DEFAULT 0,
    teamId INT DEFAULT 0,
    FOREIGN KEY (ageGroupId) REFERENCES ageGroups(ID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (teamId) REFERENCES teams(ID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB;

CREATE TABLE emergencyContacts(
    ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name TEXT NOT NULL,
    email VARCHAR(255) DEFAULT '',
    phone VARCHAR(255) DEFAULT '',
) ENGINE=INNODB;

CREATE TABLE people_emergencyContacts(
    ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    personId INT NOT NULL,
    emergencyContactId INT NOT NULL,
    FOREIGN KEY (personId) REFERENCES people(ID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (emergencyContactId) REFERENCES emergencyContacts(ID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB;

-- this is the last line
INSERT INTO properties(`key`, `value`) VALUES('version', '1');
INSERT INTO ageGroups(nameEn, nameDe, minAge, maxAge) VALUES('Discovery Rangers', 'Entdecker', 4, 6);
INSERT INTO ageGroups(nameEn, nameDe, minAge, maxAge) VALUES('Adventure Rangers', 'Forscher', 6, 8);
INSERT INTO ageGroups(nameEn, nameDe, minAge, maxAge) VALUES('Expedition Rangers', 'Kundschafter', 9, 11);
INSERT INTO ageGroups(nameEn, nameDe, minAge, maxAge) VALUES('Pathfinder', 'Pfadfinder', 12, 14);
INSERT INTO ageGroups(nameEn, nameDe, minAge, maxAge) VALUES('Pathranger', 'Pfadranger', 15, 17);