DROP TABLE IF EXISTS PRODUCTS;

CREATE TABLE PRODUCTS (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  name_product VARCHAR(250) NOT NULL,
  price INT NOT NULL,
  category_product VARCHAR(250) NOT NULL,
  supplier_id INT NOT NULL
);

DROP TABLE IF EXISTS SUPPLIERS;

CREATE TABLE SUPPLIERS (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  name_supplier VARCHAR(250) NOT NULL,
  country VARCHAR(250) NOT NULL
);

ALTER TABLE PRODUCTS
    ADD FOREIGN KEY (supplier_id)
    REFERENCES SUPPLIERS(id);

INSERT INTO SUPPLIERS (name_supplier, country) VALUES
    ('HP', 'Germany'),
    ('Samsung', 'USA');

INSERT INTO PRODUCTS (name_product, price, supplier_id, category_product) VALUES
    ('Home printer', 180, 1, 'printer'),
    ('Spectre x360', 1400, 1, 'laptop'),
    ('GalaxyBook 2', 1620, 2, 'laptop'),
    ('Galaxy Z Flip4', 900, 2, 'phone');