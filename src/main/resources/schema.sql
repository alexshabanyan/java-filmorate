DROP TABLE IF EXISTS GENRE CASCADE;
DROP TABLE IF EXISTS MPA CASCADE;
DROP TABLE IF EXISTS FILM CASCADE;
DROP TABLE IF EXISTS FILM_GENRE CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS FRIENDSHIP CASCADE;
DROP TABLE IF EXISTS FILM_LIKES CASCADE;

CREATE TABLE IF NOT EXISTS GENRE (
    ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NAME VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS MPA (
    ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NAME VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS FILM (
   ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   NAME VARCHAR(255),
   DESCRIPTION VARCHAR(199),
   RELEASE_DATE DATE,
   DURATION INT,
   MPA_ID INT REFERENCES MPA (id)
);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
   ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   GENRE_ID INT REFERENCES GENRE (id) NOT NULL,
   FILM_ID INT REFERENCES FILM (id) NOT NULL
);

CREATE TABLE IF NOT EXISTS USERS (
    ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    EMAIL VARCHAR(255),
    LOGIN VARCHAR(255),
    NAME VARCHAR(255),
    BIRTHDAY DATE
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP (
    ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    USER_ID INT REFERENCES USERS (ID),
    SEND_TO INT REFERENCES USERS (ID),
    APPROVED BOOLEAN
);

CREATE TABLE IF NOT EXISTS FILM_LIKES (
    ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    FILM_ID INT REFERENCES FILM (ID),
    USER_ID INT REFERENCES USERS (ID)
);