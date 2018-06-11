alter table SALESINVOICESERVICELINE add column UNITOFMEASURE integer default null references UNITOFMEASURE(ITSID);
alter table SALESINVOICESERVICELINE add column ITSQUANTITY real not null default 1;
alter table SALESINVOICESERVICELINE add column SUBTOTAL real not null default 0;
alter table PURCHASEINVOICESERVICELINE add column UNITOFMEASURE integer default null references UNITOFMEASURE(ITSID);
alter table PURCHASEINVOICESERVICELINE add column ITSQUANTITY real not null default 1;
alter table PURCHASEINVOICESERVICELINE add column SUBTOTAL real not null default 0;
alter table DEBTORCREDITOR add column ISFOREIGNER integer not null default 0;
alter table ACCSETTINGS add column USECURRENCYSIGN integer not null default 0;
alter table ACCSETTINGS add column PRINTCURRENCYLEFT integer not null default 0;
alter table CURRENCY add column ITSSIGN text default null;
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
update SALESINVOICESERVICELINE set SUBTOTAL=ITSPRICE, ITSVERSION=(ITSVERSION+1), UNITOFMEASURE=1 where SUBTOTAL=0;
update PURCHASEINVOICESERVICELINE set SUBTOTAL=ITSCOST, ITSVERSION=(ITSVERSION+1), UNITOFMEASURE=1 where SUBTOTAL=0;
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (25, 1, 'PurInvInvCatPayDbtCrdtAccrMSm', 1462867931627, 1, 'InvItemCategory,DebtorCreditor', 1, 0, 1, 'PURCHASEINVOICE.ITSID', 'PurchaseInvoice, Debit Inventory.InvItemCategory Credit AccPayable.DebtorCreditor for Subtotal amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (26, 1, 'PurInvSalTaxDbtAccPayCrAccrMSm', 1462867931627, 1, 'Tax,DebtorCreditor', 1, 0, 1, 'PURCHASEINVOICE.ITSID', 'PurchaseInvoice, Debit SalesTaxFromPurchase.Tax Credit AccPayable.DebtorCreditor for Tax amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (27, 1, 'PurInvExpenceDbtAccPayCrAccrMSm', 1462867931627, 1, 'Expense,DebtorCreditor', 1, 0, 1, 'PURCHASEINVOICE.ITSID', 'PurchaseInvoice , Debit AccExpense.ServicePurchasedCategory.Expense Credit AccPayable.DebtorCreditor for Subtotal amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (28, 1, 'PurInvSalTaxDbtAccPayCrCashMSm', 1462867931627, 1, 'Tax,DebtorCreditor', 0, 0, 1, 'PURCHASEINVOICE.ITSID', 'PurchaseInvoice, Debit SalesTaxFromPurchase.Tax Credit AccPayable.DebtorCreditor for Tax amount. Cash Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (29, 1, 'PaymentToSalTaxDbtAccPayCrCashMSm', 1462867931627, 8, 'Tax,DebtorCreditor', 0, 0, 1, 'PAYMENTTO.ITSID', 'PaymentTo, Debit SalesTaxFromPurchase.Tax Credit AccPayable.DebtorCreditor for Tax amount. Cash Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (30, 1, 'PurchRetRecievDbInvCatCrAccrMSm', 1462867931627, 13, 'InvItemCategory,DebtorCreditor', 1, 0, 1, 'PURCHASERETURN.ITSID', 'PurchaseReturn, Debit ReturnsReceivable.DebtorCreditor Credit Inventory.InvItemCategory for Subtotal amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (31, 1, 'PurchReturnReceivDbSalTaxFromPurRetCrAccrMSm', 1462867931627, 13, 'Tax,DebtorCreditor', 1, 0, 1, 'PURCHASERETURN.ITSID', 'PurchaseReturn, Debit ReturnsReceivable.DebtorCreditor Credit SalesTaxFromPurchReturns.Tax for Tax amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (32, 1, 'PurchReturnReceivDbSalTaxFromPurRetCrCashMSm', 1462867931627, 13, 'Tax,DebtorCreditor', 0, 0, 1, 'PURCHASERETURN.ITSID', 'PurchaseReturn, Debit ReturnsReceivable.DebtorCreditor Credit SalesTaxFromPurchReturns.Tax for Tax amount. Cash Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (33, 1, 'SalInvAccRecDbSalesServicesCatCrAccrMSm', 1462867931627, 2, 'AccReceivable,2004,SalesServices,2011', 1, 0, 1, 'SALESINVOICE.ITSID', 'SalesInvoice , Debit AccReievable.DebtorCreditor Credit SalesServices.ServiceToSaleCategory for Subtotal amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (34, 1, 'SalInvInvAccRecDbSalesInvItCatCrAccrMSm', 1462867931627, 2, 'InvItemCategory,DebtorCreditor', 1, 0, 1, 'SALESINVOICE.ITSID', 'SalesInvoice, Debit AccReceivable.DebtorCreditor Credit Sales.InvItemCategory for Subtotal amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (35, 1, 'SalInvAccRecDbSalTaxPayCrAccrMSm', 1462867931627, 2, 'DebtorCreditor,Tax', 1, 0, 1, 'SALESINVOICE.ITSID', 'SalesInvoice, Debit AccReceivable.DebtorCreditor Credit SalesTaxPay.Tax for Tax amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (36, 1, 'SalInvAccRecDbSalTaxPayCrCashMSm', 1462867931627, 2, 'DebtorCreditor,Tax', 0, 0, 1, 'SALESINVOICE.ITSID', 'SalesInvoice, Debit AccReceivable.DebtorCreditor Credit SalesTaxPay.Tax for Tax amount. Cash Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (37, 1, 'SalesReturnsDbtReturnPayCrdt', 1462867931627, 12, 'InvItemCategory,DebtorCreditor', 1, 0, 1, 'SALESRETURN.ITSID', 'SalesReturn, Debit SalesReturns.InvItemCategory Credit ReturnsPayable.DebtorCreditor for Subtotal amount. Accrual Symmetric.');
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (38, 1, 'SalReturnSalTaxFrSalRetDbRetPayCrAccrMSm', 1462867931627, 12, 'Tax,DebtorCreditor', 1, 0, 1, 'SALESRETURN.ITSID', 'SalesReturn, Debit SalesTaxFromSalReturns.Tax Credit ReturnsPayable.DebtorCreditor for Tax amount. Accrual Symmetric.');
update ACCENTRIESSOURCESLINE set ISUSED=0, ITSVERSION=(ITSVERSION+1) where ITSID in (1,5,11,15,17,19,20,21,23,24);
update DATABASEINFO set DATABASEVERSION=6, DESCRIPTION='Beige Accounting OIO DB version 6';

