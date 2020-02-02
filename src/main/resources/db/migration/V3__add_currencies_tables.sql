CREATE TABLE `currencies`(
    `userId` INT NOT NULL,
    `currencyCode` VARCHAR(3) NOT NULL,
    `fullName` VARCHAR(255) NOT NULL,
    `symbol` VARCHAR(255) NOT NULL,
    `fractionalName` VARCHAR(255) NOT NULL,
    `decimalPlaces` INT NOT NULL,

    PRIMARY KEY (`userId`, `currencyCode`),
    FOREIGN KEY (userId) REFERENCES users(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);

CREATE TABLE `currencyExchangeRate`(
    `exchangeRateId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `userId` INT NOT NULL,
    `currencyCode` VARCHAR(3) NOT NULL,
    `startTimestamp` BIGINT NOT NULL,
    `exchangeRate` DECIMAL(65,15) NOT NULL,
    `comment` TEXT DEFAULT NULL,

    FOREIGN KEY (`userId`) REFERENCES users(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,

    FOREIGN KEY (`userId`, `currencyCode`) REFERENCES currencies(`userId`, `currencyCode`)
      ON DELETE CASCADE
      ON UPDATE CASCADE,

    INDEX(`startTimestamp`)
);
