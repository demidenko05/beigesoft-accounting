alter table DEBTORCREDITOR add column ISFOREIGNER tinyint not null default 0;
update DATABASEINFO set DATABASEVERSION=6, DESCRIPTION='Beige Accounting OIO DB version 6';
