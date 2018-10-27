alter table TAXDESTINATION add column REGZIP varchar(10);
update DATABASEINFO set DATABASEVERSION=8, DESCRIPTION='Beige Accounting DB version 8';
