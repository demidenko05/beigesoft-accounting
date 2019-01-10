alter table PURCHASERETURN add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table PURCHASERETURN add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASERETURN add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASERETURNLINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table PURCHASERETURNLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASERETURNLINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASERETURNGOODSTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASERETURNTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASERETURNTAXLINE add column TAXABLEINVBASFC decimal(19,4) default 0;
alter table SALESRETURN add column PRICEINCTAX tinyint not null default 0;
alter table SALESRETURN add column FOREIGNCURRENCY bigint default null;
alter table SALESRETURN add constraint fksalretforcurrn FOREIGN KEY (FOREIGNCURRENCY) REFERENCES CURRENCY(ITSID);
alter table SALESRETURN add column EXCHANGERATE decimal(19,4) default 0;
alter table SALESRETURN add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table SALESRETURN add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESRETURN add column FOREIGNTOTAL decimal(19,4) default 0;
alter table SALESRETURNLINE add column FOREIGNPRICE decimal(19,4) default 0;
alter table SALESRETURNLINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table SALESRETURNLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESRETURNLINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table SALESRETURNGOODSTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESRETURNTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESRETURNTAXLINE add column TAXABLEINVBASFC decimal(19,4) default 0;
alter table TAXDESTINATION add column REGZIP varchar(10);
alter table CURRENCY add column STCO varchar(5);
alter table SERVICETOSALE add column TMME tinyint not null default 0;
alter table SERVICETOSALE add column TMAD integer default null;
alter table SESERVICE add column TMME tinyint not null default 0;
alter table SESERVICE add column TMAD integer default null;
alter table SESERVICE add column TAXCATEGORY bigint default null;
alter table SESERVICE add constraint fkseservtcat FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SESERVICE add column DEFUNITOFMEASURE bigint default null;
alter table SESERVICE add constraint fkseservuom FOREIGN KEY (DEFUNITOFMEASURE) REFERENCES UNITOFMEASURE(ITSID);
alter table SEGOODS add column TAXCATEGORY bigint default null;
alter table SEGOODS add constraint fksegoodtcat FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SEGOODS add column DEFUNITOFMEASURE bigint default null;
alter table SEGOODS add constraint fksegooduom FOREIGN KEY (DEFUNITOFMEASURE) REFERENCES UNITOFMEASURE(ITSID);
update DATABASEINFO set DATABASEVERSION=8, DESCRIPTION='Beige Accounting DB version 8';
