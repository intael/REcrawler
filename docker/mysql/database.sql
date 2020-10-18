CREATE TABLE `fotocasa_home` (
    id VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY,
    price FLOAT NOT NULL,
    currency VARCHAR(3) not null,
    surface FLOAT not null,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    fotocasa_reference VARCHAR(255) NOT NULL,
    home_category VARCHAR(255) NOT NULL,
    number_of_bedrooms INT DEFAULT NULL,
    number_of_bathrooms INT DEFAULT NULL,
    agency_name VARCHAR(255) DEFAULT NULL,
    agency_reference VARCHAR(255) DEFAULT NULL,
    status VARCHAR(255) DEFAULT NULL,
    antiquity VARCHAR(255) DEFAULT NULL,
    parking VARCHAR(255) DEFAULT NULL,
    floor VARCHAR(255) DEFAULT NULL,
    orientation VARCHAR(255) DEFAULT NULL,
    hot_water TINYINT(1) DEFAULT NULL,
    heating TINYINT(1) DEFAULT NULL,
    furnished TINYINT(1) DEFAULT NULL,
    elevator TINYINT(1) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;