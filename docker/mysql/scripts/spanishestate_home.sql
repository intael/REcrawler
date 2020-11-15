CREATE TABLE `spanishestate_home` (
    senumber INT NOT NULL,
    sereference VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    currency VARCHAR(3) NOT NULL,
    region VARCHAR(255) DEFAULT NULL,
    location VARCHAR(255) DEFAULT NULL,
    type VARCHAR(255) DEFAULT NULL,
    bedrooms INT DEFAULT NULL,
    bathrooms INT DEFAULT NULL,
    surface DOUBLE DEFAULT NULL,
    plot DOUBLE DEFAULT NULL,
    title VARCHAR(255) DEFAULT NULL,
    description TEXT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;