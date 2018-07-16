alter table SALESINVOICE add column FOREIGNCURRENCY integer default null references CURRENCY(ITSID);
alter table SALESINVOICE add column EXCHANGERATE real default 0;
alter table SALESINVOICE add column FOREIGNSUBTOTAL real default 0;
alter table SALESINVOICE add column FOREIGNTOTALTAXES real default 0;
alter table SALESINVOICE add column FOREIGNTOTAL real default 0;
alter table SALESINVOICELINE add column FOREIGNPRICE real default 0;
alter table SALESINVOICELINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESINVOICELINE add column FOREIGNSUBTOTAL real default 0;
alter table SALESINVOICELINE add column FOREIGNTOTAL real default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNPRICE real default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNSUBTOTAL real default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNTOTAL real default 0;
alter table SALESINVOICEGOODSTAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESINVOICESERVICETAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESINVOICETAXLINE add column FOREIGNTOTALTAXES real default 0;
update DATABASEINFO set DATABASEVERSION=7, DESCRIPTION='Beige Accounting OIO DB version 7';