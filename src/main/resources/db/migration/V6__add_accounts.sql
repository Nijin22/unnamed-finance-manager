CREATE TABLE `accounts`(
    `accountId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `currencyId` INT NOT NULL,
    `accountName` VARCHAR(255) NOT NULL,
    `belongsToUser` BOOLEAN NOT NULL, -- a user must create accounts for all organisations they interact with
                                      -- these are marked with a '0'. Personal accounts,  with a '1'
    `notes` MEDIUMTEXT NOT NULL,

    FOREIGN KEY (`currencyId`) REFERENCES currencies(`currencyId`)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);
