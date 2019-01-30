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
alter table WAREHOUSEREST add column ITSVERSION bigint not null default 1;
update INVITEM set KNOWNCOST=0 where KNOWNCOST is null;
update DATABASEINFO set DATABASEVERSION=8, DESCRIPTION='Beige Accounting DB version 8';
