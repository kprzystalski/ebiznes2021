# --- !Ups


CREATE TABLE "user"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "providerId"  VARCHAR NOT NULL,
    "providerKey" VARCHAR NOT NULL,
    "email"       VARCHAR NOT NULL
);

CREATE TABLE "authToken"
(
    "id"     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "userId" INT     NOT NULL,
    FOREIGN KEY (userId) references user (id)
);

CREATE TABLE "passwordInfo"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "providerId"  VARCHAR NOT NULL,
    "providerKey" VARCHAR NOT NULL,
    "hasher"      VARCHAR NOT NULL,
    "password"    VARCHAR NOT NULL,
    "salt"        VARCHAR
);

CREATE TABLE "oAuth2Info"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "providerId"  VARCHAR NOT NULL,
    "providerKey" VARCHAR NOT NULL,
    "accessToken" VARCHAR NOT NULL,
    "tokenType"   VARCHAR,
    "expiresIn"   INTEGER
);


# --- !Downs

DROP TABLE "user";
DROP TABLE "authToken";
DROP TABLE "passwordInfo";
DROP TABLE "oAuth2Info";
