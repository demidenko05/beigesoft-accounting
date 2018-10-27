alter table TAXDESTINATION add column REGZIP text;
update DATABASEINFO set DATABASEVERSION=8, DESCRIPTION='Beige Accounting DB version 8';
