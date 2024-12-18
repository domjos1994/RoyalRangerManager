
-- properties table
CREATE TABLE properties(
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    'key' VARCHAR(2000) NOT NULL,
    'value' VARCHAR(5000) NOT NULL
);

-- general tables
CREATE TABLE users(
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    userName VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    gender INTEGER NOT NULL,
    email VARCHAR(255) NOT NULL,
    admin BIT DEFAULT 0,
    trunkLeader BIT DEFAULT 0,
    trunkWait BIT DEFAULT 0,
    trunkHelper BIT DEFAULT 0,
    leader BIT DEFAULT 0,
    juniorLeader BIT DEFAULT 0
);

CREATE TABLE ageGroups(
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    nameEn VARCHAR(255) NOT NULL,
    nameDe VARCHAR(255) NOT NULL,
    minAge INTEGER NOT NULL,
    maxAge INTEGER NOT NULL
);

CREATE TABLE teams(
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT DEFAULT '',
    note TEXT DEFAULT '',
    gender INTEGER NOT NULL,
    ageGroupId INTEGER NOT NULL,
    FOREIGN KEY(ageGroupId) REFERENCES ageGroups(ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE teams_users(
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    teamId INTEGER NOT NULL,
    userId INTEGER NOT NULL,
    FOREIGN KEY(teamId) REFERENCES teams(ID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(userId) REFERENCES users(ID) ON DELETE CASCADE ON UPDATE CASCADE
);

-- people tables
CREATE TABLE people(
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    firstName VARCHAR(255) NOT NULL,
    middleName VARCHAR(255) DEFAULT '',
    lastName VARCHAR(255) NOT NULL,
    gender INTEGER NOT NULL,
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
    ageGroupId INTEGER DEFAULT 0,
    teamId INTEGER DEFAULT 0,
    FOREIGN KEY (ageGroupId) REFERENCES ageGroups(ID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (teamId) REFERENCES teams(ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE emergencyContacts(
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email VARCHAR(255) DEFAULT '',
    phone VARCHAR(255) DEFAULT ''
);

CREATE TABLE people_emergencyContacts(
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    personId INTEGER NOT NULL,
    emergencyContactId INTEGER NOT NULL,
    FOREIGN KEY (personId) REFERENCES people(ID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (emergencyContactId) REFERENCES emergencyContacts(ID) ON DELETE CASCADE ON UPDATE CASCADE
);

-- this is the last line
INSERT INTO properties('key', 'value') VALUES('version', '1');
INSERT INTO ageGroups(nameEn, nameDe, minAge, maxAge) VALUES('Discovery Rangers', 'Entdecker', 4, 6);
INSERT INTO ageGroups(nameEn, nameDe, minAge, maxAge) VALUES('Adventure Rangers', 'Forscher', 6, 8);
INSERT INTO ageGroups(nameEn, nameDe, minAge, maxAge) VALUES('Expedition Rangers', 'Kundschafter', 9, 11);
INSERT INTO ageGroups(nameEn, nameDe, minAge, maxAge) VALUES('Pathfinder', 'Pfadfinder', 12, 14);
INSERT INTO ageGroups(nameEn, nameDe, minAge, maxAge) VALUES('Pathranger', 'Pfadranger', 15, 17);