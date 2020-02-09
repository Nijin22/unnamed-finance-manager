ALTER TABLE users
  ADD defaultCurrency INT; -- can be NULL

ALTER TABLE users
  ADD FOREIGN KEY (defaultCurrency) REFERENCES currencies(currencyId) ON DELETE RESTRICT;
