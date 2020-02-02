CREATE TABLE `sessions`(
    `token` VARCHAR(255) NOT NULL PRIMARY KEY,
    `userId` INT NOT NULL,
    `client` VARCHAR(255) NOT NULL,
    `creationTimestamp` BIGINT NOT NULL,
    `lastUsageTimestamp` BIGINT NOT NULL,

    FOREIGN KEY (userId) REFERENCES users(id)
);
