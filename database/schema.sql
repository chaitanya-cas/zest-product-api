CREATE DATABASE IF NOT EXISTS product_db;
USE product_db;

CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(255) NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_on TIMESTAMP NOT NULL,
    modified_by VARCHAR(100),
    modified_on TIMESTAMP
);

CREATE INDEX idx_product_name ON product(product_name);
CREATE INDEX idx_product_created_on ON product(created_on);

CREATE TABLE item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_item_product
        FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE INDEX idx_item_product_id ON item(product_id);
