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
alter table SALESINVOICETAXLINE add column TAXABLEINVBAS real default 0;
alter table SALESINVOICETAXLINE add column TAXABLEINVBASFC real default 0;
alter table PAYMENTFROM add column FOREIGNTOTAL real default 0;
alter table PREPAYMENTFROM add column FOREIGNTOTAL real default 0;
alter table PURCHASEINVOICE add column FOREIGNCURRENCY integer default null references CURRENCY(ITSID);
alter table PURCHASEINVOICE add column EXCHANGERATE real default 0;
alter table PURCHASEINVOICE add column FOREIGNSUBTOTAL real default 0;
alter table PURCHASEINVOICE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICE add column FOREIGNTOTAL real default 0;
alter table PURCHASEINVOICELINE add column FOREIGNPRICE real default 0;
alter table PURCHASEINVOICELINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICELINE add column FOREIGNSUBTOTAL real default 0;
alter table PURCHASEINVOICELINE add column FOREIGNTOTAL real default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNPRICE real default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNSUBTOTAL real default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNTOTAL real default 0;
alter table PURCHASEINVOICEGOODSTAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICESERVICETAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICETAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICETAXLINE add column TAXABLEINVBAS real default 0;
alter table PURCHASEINVOICETAXLINE add column TAXABLEINVBASFC real default 0;
alter table PAYMENTTO add column FOREIGNTOTAL real default 0;
alter table PREPAYMENTTO add column FOREIGNTOTAL real default 0;
alter table ACCSETTINGS add column SALTAXISINVOICEBASE integer not null default 0;
alter table ACCSETTINGS add column SALTAXUSEAGGREGITBAS integer not null default 0;
alter table ACCSETTINGS add column SALTAXROUNDMODE integer not null default 4;
alter table ACCSETTINGS add column TTFFILENAME text default 'DejaVuSerif';
alter table ACCSETTINGS add column TTFBOLDFILENAME text default 'DejaVuSerif-Bold';
alter table ACCSETTINGS add column PAGESIZE integer not null default 2;
alter table ACCSETTINGS add column PAGEORIENTATION integer not null default 0;
alter table ACCSETTINGS add column MARGINLEFT real default 30;
alter table ACCSETTINGS add column MARGINRIGHT real default 15;
alter table ACCSETTINGS add column MARGINTOP real default 20;
alter table ACCSETTINGS add column MARGINBOTTOM real default 20;
alter table ACCSETTINGS add column FONTSIZE real default 3.5;
update DATABASEINFO set DATABASEVERSION=7, DESCRIPTION='Beige Accounting OIO DB version 7';
