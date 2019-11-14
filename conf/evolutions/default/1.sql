# --- !Ups
CREATE SCHEMA auth;

CREATE TABLE auth.role (
  id   SERIAL NOT NULL PRIMARY KEY,
  name VARCHAR
);

INSERT INTO auth.role (name)
VALUES ('user'),
       ('admin');

CREATE TABLE auth.user (
  id         UUID    NOT NULL PRIMARY KEY,
  first_name VARCHAR,
  last_name  VARCHAR,
  email      VARCHAR,
  role_id    INT     NOT NULL,
  activated  BOOLEAN NOT NULL,
  avatar_url VARCHAR,
  CONSTRAINT auth_user_role_id_fk FOREIGN KEY (role_id) REFERENCES auth.role (id)
);

CREATE TABLE auth.login_info (
  id           BIGSERIAL NOT NULL PRIMARY KEY,
  provider_id  VARCHAR,
  provider_key VARCHAR
);

CREATE TABLE auth.user_login_info (
  user_id       UUID   NOT NULL,
  login_info_id BIGINT NOT NULL,
  CONSTRAINT auth_user_login_info_user_id_fk FOREIGN KEY (user_id) REFERENCES auth.user (id),
  CONSTRAINT auth_user_login_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES auth.login_info (id)
);

CREATE TABLE auth.oauth1_info (
  id            BIGSERIAL NOT NULL PRIMARY KEY,
  token         VARCHAR   NOT NULL,
  secret        VARCHAR   NOT NULL,
  login_info_id BIGINT    NOT NULL,
  CONSTRAINT auth_oauth1_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES auth.login_info (id)
);

CREATE TABLE auth.oauth2_info (
  id            BIGSERIAL NOT NULL PRIMARY KEY,
  access_token  VARCHAR   NOT NULL,
  token_type    VARCHAR,
  expires_in    INT,
  refresh_token VARCHAR,
  login_info_id BIGINT    NOT NULL,
  CONSTRAINT auth_oauth2_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES auth.login_info (id)
);

CREATE TABLE auth.password_info (
  hasher        VARCHAR NOT NULL,
  password      VARCHAR NOT NULL,
  salt          VARCHAR,
  login_info_id BIGINT  NOT NULL,
  CONSTRAINT auth_password_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES auth.login_info (id)
);

CREATE TABLE auth.token (
  id      UUID        NOT NULL PRIMARY KEY,
  user_id UUID        NOT NULL,
  expiry  TIMESTAMPTZ NOT NULL,
  CONSTRAINT auth_token_user_id_fk FOREIGN KEY (user_id) REFERENCES auth.user (id)
);

# --- !Downs

DROP TABLE auth.token;
DROP TABLE auth.password_info;
DROP TABLE auth.oauth2_info;
DROP TABLE auth.oauth1_info;
DROP TABLE auth.user_login_info;
DROP TABLE auth.login_info;
DROP TABLE auth.user;
DROP TABLE auth.role;
DROP SCHEMA auth;
