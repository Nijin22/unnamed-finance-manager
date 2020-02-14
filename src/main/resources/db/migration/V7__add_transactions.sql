CREATE TABLE `transactions`(
    `transactionId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `userId` INT NOT NULL,
    `transactionName` VARCHAR(255) NOT NULL,
    `timestamp` BIGINT NOT NULL,
    `notes` MEDIUMTEXT NOT NULL,

    FOREIGN KEY (`userId`) REFERENCES `users`(`id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);

CREATE TABLE `balanceChanges`(
    `balanceChangeId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `transactionId` INT NOT NULL,
    `accountId` INT NOT NULL,
    `value` DECIMAL(65,15) NOT NULL,

    FOREIGN KEY (`transactionId`) REFERENCES `transactions`(`transactionId`)
      ON DELETE CASCADE
      ON UPDATE CASCADE,

    FOREIGN KEY (`accountId`) REFERENCES `accounts`(`accountId`)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);
