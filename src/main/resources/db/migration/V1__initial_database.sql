CREATE TABLE mail (
    uuid UUID PRIMARY KEY,
    coop VARCHAR NOT NULL,
    lang VARCHAR NOT NULL,
    type_id INT NOT NULL,
    content VARCHAR NOT NULL,
    title VARCHAR NOT NULL
);