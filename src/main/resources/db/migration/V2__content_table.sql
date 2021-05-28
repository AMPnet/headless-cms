CREATE TABLE content (
    uuid UUID PRIMARY KEY,
    text VARCHAR NOT NULL,
    coop VARCHAR NOT NULL,
    key VARCHAR NOT NULL,
    lang VARCHAR NOT NULL
);

CREATE INDEX content_coop_idx ON content(coop);
CREATE INDEX content_key_idx ON content(key);
CREATE INDEX content_lang_idx ON content(lang);
CREATE UNIQUE INDEX content_search_idx ON content(coop, key, lang);
