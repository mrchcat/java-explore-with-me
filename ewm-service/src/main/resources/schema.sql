--удалить
DROP TABLE IF EXISTS categories;
--удалить
CREATE TABLE IF NOT EXISTS categories (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar NOT NULL UNIQUE
);

