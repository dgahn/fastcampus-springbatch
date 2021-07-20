CREATE TABLE IF NOT EXISTS person(
  id BIGSERIAL primary key,
  name VARCHAR(255),
  age VARCHAR(255),
  address VARCHAR(255)
);

INSERT INTO person(name, age, address) VALUES ('이경원', '32', '인천');
INSERT INTO person(name, age, address) VALUES ('홍길동', '30', '서울');
INSERT INTO person(name, age, address) VALUES ('아무개', '25', '강원');

