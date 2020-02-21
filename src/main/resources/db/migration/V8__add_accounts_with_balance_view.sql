CREATE VIEW accountsWithBalance AS
SELECT
    curr.userId,
    acc.accountId, acc.accountName, acc.belongsToUser, acc.notes,
    curr.currencyId, curr.shortName as currencyName, curr.exchangeRate as currentExchangeRate,
    IFNULL(aggregatedBc.currentBalance, 0) as currentBalance
  FROM accounts as acc
  INNER JOIN currenciesWithCurrentExchangeRate as curr ON acc.currencyId = curr.currencyId
  LEFT OUTER JOIN
    (
      SELECT bc.accountId, SUM(bc.value) as currentBalance
      FROM balanceChanges as bc
      INNER JOIN transactions as tx ON bc.transactionId = tx.transactionId
      WHERE tx.`timestamp` < UNIX_TIMESTAMP()
      GROUP BY bc.accountId
    ) aggregatedBc
    ON aggregatedBc.accountId = acc.accountId
  WHERE curr.userId = '1'
  GROUP BY acc.accountId;
