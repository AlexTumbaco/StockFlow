CREATE TABLE categories (
    id BIGINT IDENTITY(1,1) NOT NULL,
    name NVARCHAR(120) NOT NULL,
    description NVARCHAR(1000) NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uk_categories_name UNIQUE (name)
);

CREATE TABLE products (
    id BIGINT IDENTITY(1,1) NOT NULL,
    name NVARCHAR(160) NOT NULL,
    sku NVARCHAR(64) NOT NULL,
    description NVARCHAR(2000) NULL,
    price DECIMAL(19,4) NOT NULL,
    stock INT NOT NULL CONSTRAINT df_products_stock DEFAULT (0),
    minimum_stock INT NOT NULL,
    active BIT NOT NULL CONSTRAINT df_products_active DEFAULT (1),
    category_id BIGINT NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT uk_products_sku UNIQUE (sku),
    CONSTRAINT ck_products_price_non_negative CHECK (price >= 0),
    CONSTRAINT ck_products_stock_non_negative CHECK (stock >= 0),
    CONSTRAINT ck_products_minimum_stock_non_negative CHECK (minimum_stock >= 0),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id)
);
