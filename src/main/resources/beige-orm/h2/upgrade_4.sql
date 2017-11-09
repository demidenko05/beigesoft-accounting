alter table ACCOUNT add column ITSVERSION bigint not null default 1462867931627;
alter table INVITEMTYPE add column ITSVERSION bigint not null default 1462867931627;
alter table UNITOFMEASURE add column ITSVERSION bigint not null default 1462867931627;
alter table WAREHOUSE add column ITSVERSION bigint not null default 1462867931627;
alter table WAREHOUSESITE add column ITSVERSION bigint not null default 1462867931627;
alter table CURRENCY add column ITSVERSION bigint not null default 1462867931627;
alter table BANKACCOUNTUSED add column ITSVERSION bigint not null default 1462867931627;
alter table DEBTORCREDITORCATEGORYUSED add column ITSVERSION bigint not null default 1462867931627;
alter table DEBTORCREDITORUSED add column ITSVERSION bigint not null default 1462867931627;
alter table EMPLOYEECATEGORYUSED add column ITSVERSION bigint not null default 1462867931627;
alter table EMPLOYEEUSED add column ITSVERSION bigint not null default 1462867931627;
alter table EXPENSEUSED add column ITSVERSION bigint not null default 1462867931627;
alter table INVITEMCATEGORYUSED add column ITSVERSION bigint not null default 1462867931627;
alter table PROPERTYUSED add column ITSVERSION bigint not null default 1462867931627;
alter table TAXUSED add column ITSVERSION bigint not null default 1462867931627;
alter table WAGETYPEUSED add column ITSVERSION bigint not null default 1462867931627;
alter table ACCOUNTINGENTRY add column REVERSEDIDDATABASEBIRTH integer;
alter table PURCHASEINVOICE add column REVERSEDIDDATABASEBIRTH integer;
alter table SALESINVOICE add column REVERSEDIDDATABASEBIRTH integer;
alter table BEGINNINGINVENTORY add column REVERSEDIDDATABASEBIRTH integer;
alter table GOODSLOSS add column REVERSEDIDDATABASEBIRTH integer;
alter table MANUFACTURE add column REVERSEDIDDATABASEBIRTH integer;
alter table MANUFACTURINGPROCESS add column REVERSEDIDDATABASEBIRTH integer;
alter table PAYMENTFROM add column REVERSEDIDDATABASEBIRTH integer;
alter table PAYMENTTO add column REVERSEDIDDATABASEBIRTH integer;
alter table PREPAYMENTFROM add column REVERSEDIDDATABASEBIRTH integer;
alter table PREPAYMENTTO add column REVERSEDIDDATABASEBIRTH integer;
alter table PURCHASERETURN add column REVERSEDIDDATABASEBIRTH integer;
alter table SALESRETURN add column REVERSEDIDDATABASEBIRTH integer;
alter table WAGE add column REVERSEDIDDATABASEBIRTH integer;
update ACCOUNTINGENTRY set SOURCEDATABASEBIRTH=IDDATABASEBIRTH  where SOURCEDATABASEBIRTH is null;
update ACCOUNTINGENTRY set REVERSEDIDDATABASEBIRTH=IDDATABASEBIRTH  where REVERSEDID is not null;
update SUBACCOUNTLINE set ITSVERSION=1462867931627;
update INVITEMTAXCATEGORY set ITSVERSION=1462867931627;
update WAGELINE set ITSVERSION=1462867931627;
update WAGETAXLINE set ITSVERSION=1462867931627;
update DATABASEINFO set DATABASEVERSION=4, DESCRIPTION='Beige Accounting DB version 4';
