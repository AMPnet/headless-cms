-- Mail
CREATE TABLE mail (
    id UUID PRIMARY KEY,
    coop VARCHAR NOT NULL,
    title VARCHAR NOT NULL,
    content VARCHAR NOT NULL,
    type_id INT NOT NULL,
    lang VARCHAR NOT NULL
);
