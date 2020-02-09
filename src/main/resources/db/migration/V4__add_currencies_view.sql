-- Note that this view uses UNIX_TIMESTAMP() which will break with the Year 2038 problem.
-- Hopefully, MariaDB will have a replacement for it by then.

CREATE VIEW currenciesWithCurrentExchangeRate AS
    SELECT currencies.*, rates.exchangeRate
    FROM currencyExchangeRate as rates
    INNER JOIN
        (SELECT currencyId, MAX(startTimestamp) AS maxCurrentTimestamp
        FROM currencyExchangeRate
        WHERE startTimestamp <= UNIX_TIMESTAMP()
        GROUP BY currencyId) as maxCurrentTimestamps
    ON rates.currencyId = maxCurrentTimestamps.currencyId AND rates.startTimestamp = maxCurrentTimestamps.maxCurrentTimestamp
    INNER JOIN currencies
      ON rates.currencyId = currencies.currencyId;
