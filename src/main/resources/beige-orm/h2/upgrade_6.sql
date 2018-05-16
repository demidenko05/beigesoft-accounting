alter table DEBTORCREDITOR add column ISFOREIGNER integer not null default 0;
alter table CURRENCY add column ITSSIGN varchar(6) default null;
update CURRENCY set ITSSIGN='€', ITSVERSION=(ITSVERSION+1) where ITSID=978;
update CURRENCY set ITSSIGN='$', ITSVERSION=(ITSVERSION+1) where ITSID=840;
update CURRENCY set ITSSIGN='₽', ITSVERSION=(ITSVERSION+1) where ITSID=643;
update DATABASEINFO set DATABASEVERSION=6, DESCRIPTION='Beige Accounting OIO DB version 6';
