CREATE TABLE searchpath (
    id INT NOT NULL, 
    path VARCHAR(1024), 
    active BOOLEAN,  
    exclude BOOLEAN
);

CREATE TABLE content (
    id INT NOT NULL,
    name VARCHAR(256),
    path VARCHAR(1024),
    checksum VARCHAR(512),
    createdDateTime TIMESTAMP,
    lastModifiedDateTime TIMESTAMP
);