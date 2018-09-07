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
alter table PURCHASERETURNTAXLINE add column TAXABLEINVBAS decimal(19,4) default 0;
alter table SALESRETURNTAXLINE add column TAXABLEINVBAS decimal(19,4) default 0;
alter table PAYMENTTO add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PREPAYMENTTO add column FOREIGNTOTAL decimal(19,4) default 0;
alter table ACCSETTINGS add column SALTAXISINVOICEBASE tinyint not null default 0;
alter table ACCSETTINGS add column SALTAXUSEAGGREGITBAS tinyint not null default 0;
alter table ACCSETTINGS add column SALTAXROUNDMODE tinyint not null default 4;
alter table ACCSETTINGS add column TTFFILENAME varchar(100) default 'DejaVuSerif';
alter table ACCSETTINGS add column TTFBOLDFILENAME varchar(100) default 'DejaVuSerif-Bold';
alter table ACCSETTINGS add column PAGESIZE tinyint not null default 2;
alter table ACCSETTINGS add column PAGEORIENTATION tinyint not null default 0;
alter table ACCSETTINGS add column MARGINLEFT decimal(19,4) default 30;
alter table ACCSETTINGS add column MARGINRIGHT decimal(19,4) default 15;
alter table ACCSETTINGS add column MARGINTOP decimal(19,4) default 20;
alter table ACCSETTINGS add column MARGINBOTTOM decimal(19,4) default 20;
alter table ACCSETTINGS add column FONTSIZE decimal(19,4) default 3.5;
alter table ACCSETTINGS add column TAXPRECISION integer not null default 3;
alter table PURCHASEINVOICE add column PRICEINCTAX tinyint not null default 0;
alter table SALESINVOICE add column PRICEINCTAX tinyint not null default 0;
alter table DEBTORCREDITOR add column TAXDESTINATION bigint default null;
alter table DEBTORCREDITOR add constraint fkdebcrtaxdest FOREIGN KEY (TAXDESTINATION) references TAXDESTINATION(ITSID);
alter table INVITEMTAXCATEGORY add column AGGRONLYPERCENT decimal(19,4) default 0;
alter table PURCHASEINVOICELINE add column TAXCATEGORY bigint default null;
alter table PURCHASEINVOICELINE add constraint fkpigltaxcat FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table PURCHASEINVOICESERVICELINE add column TAXCATEGORY bigint default null;
alter table PURCHASEINVOICESERVICELINE add constraint fkpisltaxcat FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SALESINVOICELINE add column TAXCATEGORY bigint default null;
alter table SALESINVOICELINE add constraint fksigltaxcat FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SALESINVOICESERVICELINE add column TAXCATEGORY bigint default null;
alter table SALESINVOICESERVICELINE add constraint fksisltaxcat FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SALESRETURNLINE add column TAXCATEGORY bigint default null;
alter table SALESRETURNLINE add constraint fksisltaxcat FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SERVICETOSALE add column DEFUNITOFMEASURE bigint default null;
alter table SERVICETOSALE add constraint fksertoselduom FOREIGN KEY (DEFUNITOFMEASURE) REFERENCES UNITOFMEASURE(ITSID);
alter table SERVICEPURCHASED add column DEFUNITOFMEASURE bigint default null;
alter table SERVICEPURCHASED add constraint fkserpurchduom FOREIGN KEY (DEFUNITOFMEASURE) REFERENCES UNITOFMEASURE(ITSID);
alter table PURCHASEINVOICE add column OMITTAXES tinyint not null default 0;
alter table SALESINVOICE add column OMITTAXES tinyint not null default 0;
alter table SALESRETURN add column OMITTAXES tinyint not null default 0;
update DATABASEINFO set DATABASEVERSION=7, DESCRIPTION='Beige Accounting OIO DB version 7';
