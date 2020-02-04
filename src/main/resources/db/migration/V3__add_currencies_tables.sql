CREATE TABLE `currencies`(
    `currencyId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `userId` INT NOT NULL,
    `shortName` VARCHAR(255) NOT NULL,
    `fullName` VARCHAR(255) NOT NULL,
    `fractionalName` VARCHAR(255) NOT NULL,
    `decimalPlaces` INT NOT NULL,

    FOREIGN KEY (userId) REFERENCES users(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);

CREATE TABLE `currencyExchangeRate`(
    `exchangeRateId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `currencyId` INT NOT NULL,
    `startTimestamp` BIGINT NOT NULL,
    `exchangeRate` DECIMAL(65,15) NOT NULL,
    `comment` TEXT DEFAULT NULL,

    FOREIGN KEY (`currencyId`) REFERENCES currencies(`currencyId`)
      ON DELETE CASCADE
      ON UPDATE CASCADE,

    INDEX(`startTimestamp`)
);
