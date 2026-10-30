CREATE TABLE post (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(200) NOT NULL,
                      text TEXT,
                      type VARCHAR(10)  NOT NULL,
                      post_path VARCHAR(255) NOT NULL,
                      data_creation TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE comment (
                         id BIGSERIAL PRIMARY KEY,
                         post_id BIGINT NOT NULL REFERENCES post(id) ON DELETE CASCADE,
                         a_name VARCHAR(50) NOT NULL,
                         text VARCHAR(2000) NOT NULL,
                         date TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_comment_post ON comment(post_id, date);

CREATE TABLE profile (
                         id BIGINT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         bio TEXT,
                         icon_path VARCHAR(500),
                         donate VARCHAR(500)
);
INSERT INTO profile (id, name, bio) VALUES (1, 'Автор', 'Описние TODO');
