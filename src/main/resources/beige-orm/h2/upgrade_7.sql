alter table SALESINVOICE add column FOREIGNCURRENCY bigint default null;
alter table SALESINVOICE add constraint fksalinvforcurrn FOREIGN KEY (FOREIGNCURRENCY) REFERENCES CURRENCY(ITSID);
alter table SALESINVOICE add column EXCHANGERATE decimal(19,4) default 0;
alter table SALESINVOICE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table SALESINVOICE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESINVOICE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table SALESINVOICELINE add column FOREIGNPRICE decimal(19,4) default 0;
alter table SALESINVOICELINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESINVOICELINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table SALESINVOICELINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNPRICE decimal(19,4) default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table SALESINVOICEGOODSTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESINVOICESERVICETAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESINVOICETAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESINVOICETAXLINE add column TAXABLEINVBAS decimal(19,4) default 0;
alter table SALESINVOICETAXLINE add column TAXABLEINVBASFC decimal(19,4) default 0;
alter table PAYMENTFROM add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PREPAYMENTFROM add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICE add column FOREIGNCURRENCY bigint default null;
alter table PURCHASEINVOICE add constraint fkpurinvforcurrn FOREIGN KEY (FOREIGNCURRENCY) REFERENCES CURRENCY(ITSID);
alter table PURCHASEINVOICE add column EXCHANGERATE decimal(19,4) default 0;
alter table PURCHASEINVOICE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICELINE add column FOREIGNPRICE decimal(19,4) default 0;
alter table PURCHASEINVOICELINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICELINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICELINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNPRICE decimal(19,4) default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICEGOODSTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICESERVICETAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICETAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICETAXLINE add column TAXABLEINVBAS decimal(19,4) default 0;
alter table PURCHASEINVOICETAXLINE add column TAXABLEINVBASFC decimal(19,4) default 0;
alter table PAYMENTTO add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PREPAYMENTTO add column FOREIGNTOTAL decimal(19,4) default 0;
alter table ACCSETTINGS add column SALTAXISINVOICEBASE tinyint not null default 0;
alter table ACCSETTINGS add column SALTAXROUNDMODE tinyint not null default 4;
update DATABASEINFO set DATABASEVERSION=7, DESCRIPTION='Beige Accounting OIO DB version 7';
