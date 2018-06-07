alter table SALESINVOICESERVICELINE add column UNITOFMEASURE bigint unsigned not null default 1;
alter table SALESINVOICESERVICELINE add FOREIGN KEY (UNITOFMEASURE) REFERENCES UNITOFMEASURE(ITSID);
alter table SALESINVOICESERVICELINE add column ITSQUANTITY decimal(19,4) not null default 1;
alter table SALESINVOICESERVICELINE add column SUBTOTAL decimal(19,4) not null default 0;
alter table PURCHASEINVOICESERVICELINE add column UNITOFMEASURE bigint unsigned not null default 1;
alter table PURCHASEINVOICESERVICELINE add FOREIGN KEY (UNITOFMEASURE) REFERENCES UNITOFMEASURE(ITSID);
alter table PURCHASEINVOICESERVICELINE add column ITSQUANTITY decimal(19,4) not null default 1;
alter table PURCHASEINVOICESERVICELINE add column SUBTOTAL decimal(19,4) not null default 0;
alter table DEBTORCREDITOR add column ISFOREIGNER tinyint not null default 0;
alter table ACCSETTINGS add column USECURRENCYSIGN tinyint not null default 0;
alter table ACCSETTINGS add column PRINTCURRENCYLEFT tinyint not null default 0;
alter table CURRENCY add column ITSSIGN varchar(6) default null;
update CURRENCY set ITSSIGN='€', ITSVERSION=(ITSVERSION+1) where ITSID=978;
update CURRENCY set ITSSIGN='$', ITSVERSION=(ITSVERSION+1) where ITSID=840;
update CURRENCY set ITSSIGN='₽', ITSVERSION=(ITSVERSION+1) where ITSID=643;
insert into LANGUAGES (ITSID, ITSNAME, ITSVERSION) values ('ru', 'Русский', 1462867931627);
insert into LANGUAGES (ITSID, ITSNAME, ITSVERSION) values ('en', 'English', 1462867931627);
insert into COUNTRIES (ITSID, ITSNAME, ITSVERSION) values ('US', 'USA', 1462867931627);
insert into COUNTRIES (ITSID, ITSNAME, ITSVERSION) values ('RU', 'РФ', 1462867931627);
insert into DECIMALSEPARATOR (ITSID, ITSNAME, ITSVERSION) values (',', 'comma', 1462867931627);
insert into DECIMALSEPARATOR (ITSID, ITSNAME, ITSVERSION) values ('.', 'dot', 1462867931627);
insert into DECIMALGROUPSEPARATOR (ITSID, ITSNAME, ITSVERSION) values (',', 'comma', 1462867931627);
insert into DECIMALGROUPSEPARATOR (ITSID, ITSNAME, ITSVERSION) values ('space', 'space', 1462867931627);
insert into LANGPREFERENCES (DECIMALGROUPSEP, LANG, COUNTRY, DECIMALSEP, ISDEFAULT, ITSVERSION, DIGITSINGROUP) values (',', 'en', 'US', '.', 1, 1462867931627, 3);
insert into LANGPREFERENCES (DECIMALGROUPSEP, LANG, COUNTRY, DECIMALSEP, ISDEFAULT, ITSVERSION, DIGITSINGROUP) values ('space', 'ru', 'RU', ',', 0, 1462867931627, 3);
update SALESINVOICESERVICELINE set SUBTOTAL=ITSPRICE, ITSVERSION=(ITSVERSION+1) where SUBTOTAL=0;
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (25, 1, 'PurInvInvCatPayDbtCrdtAccrMSm', 1462867931627, 1, 'InvItemCategory,DebtorCreditor', 1, 0, 1, 'PURCHASEINVOICE.ITSID', 'PurchaseInvoice, Debit Inventory per InvItemCategory, Credit AccPayable per DebtorCreditor for Subtotal amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (26, 1, 'PurInvSalTaxDbtAccPayCrAccrMSm', 1462867931627, 1, 'Tax,DebtorCreditor', 1, 0, 1, 'PURCHASEINVOICE.ITSID', 'PurchaseInvoice, Debit SalesTaxFromPurchase per Tax, Credit AccPayable per DebtorCreditor for Tax amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (27, 1, 'PurInvExpenceDbtAccPayCrAccrMSm', 1462867931627, 1, 'Expense,DebtorCreditor', 1, 0, 1, 'PURCHASEINVOICE.ITSID', 'PurchaseInvoice , Debit AccExpense per ServicePurchasedCategory.Expense, Credit AccPayable per DebtorCreditor for Subtotal amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (28, 1, 'PurInvSalTaxDbtAccPayCrCashMSm', 1462867931627, 1, 'Tax,DebtorCreditor', 0, 0, 1, 'PURCHASEINVOICE.ITSID', 'PurchaseInvoice, Debit SalesTaxFromPurchase per Tax, Credit AccPayable per DebtorCreditor for Tax amount. Cash Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (29, 1, 'PaymentToSalTaxDbtAccPayCrCashMSm', 1462867931627, 8, 'Tax,DebtorCreditor', 0, 0, 1, 'PAYMENTTO.ITSID', 'PaymentTo, Debit SalesTaxFromPurchase per Tax, Credit AccPayable per DebtorCreditor for Tax amount. Cash Symmetric.');
update DATABASEINFO set DATABASEVERSION=6, DESCRIPTION='Beige Accounting OIO DB version 6';
