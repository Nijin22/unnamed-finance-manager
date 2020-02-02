CREATE TABLE `users`(
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(255) NOT NULL,
    `password` BINARY(60) NOT NULL,

    CONSTRAINT unique_emails UNIQUE (email)
);
