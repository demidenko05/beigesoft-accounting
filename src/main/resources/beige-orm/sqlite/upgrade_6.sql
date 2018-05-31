alter table DEBTORCREDITOR add column ISFOREIGNER integer not null default 0;
alter table ACCSETTINGS add column USECURRENCYSIGN integer not null default 0;
alter table ACCSETTINGS add column PRINTCURRENCYLEFT integer not null default 0;
alter table CURRENCY add column ITSSIGN text default null;
update CURRENCY set ITSSIGN='€', ITSVERSION=(ITSVERSION+1) where ITSID=978;
update CURRENCY set ITSSIGN='$', ITSVERSION=(ITSVERSION+1) where ITSID=840;
update CURRENCY set ITSSIGN='₽', ITSVERSION=(ITSVERSION+1) where ITSID=643;
insert into LANGUAGES (ITSID, ITSNAME, ITSVERSION) values ('ru', 'Русский', 1462867931627);
insert into LANGUAGES (ITSID, ITSNAME, ITSVERSION) values ('en', 'English', 1462867931627);
insert into COUNTRIES (ITSID, ITSNAME, ITSVERSION) values ('US', 'USA', 1462867931627);
insert into COUNTRIES (ITSID, ITSNAME, ITSVERSION) values ('RU', 'РФ', 1462867931627);
insert into DECIMALSEPARATOR (ITSID, ITSNAME, ITSVERSION) values (',', 'comma', 1462867931627);
insert into DECIMALSEPARATOR (ITSID, ITSNAME, ITSVERSION) values ('.', 'dot', 1462867931627);
insert into DECIMALGROUPSEPARATOR (ITSID, ITSNAME, ITSVERSION) values (',', 'comma', 1462867931627);
insert into DECIMALGROUPSEPARATOR (ITSID, ITSNAME, ITSVERSION) values ('space', 'space', 1462867931627);
insert into LANGPREFERENCES (DECIMALGROUPSEP, LANG, COUNTRY, DECIMALSEP, ISDEFAULT, ITSVERSION, DIGITSINGROUP) values (',', 'en', 'US', '.', 1, 1462867931627, 3);
insert into LANGPREFERENCES (DECIMALGROUPSEP, LANG, COUNTRY, DECIMALSEP, ISDEFAULT, ITSVERSION, DIGITSINGROUP) values ('space', 'ru', 'RU', ',', 0, 1462867931627, 3);
update DATABASEINFO set DATABASEVERSION=6, DESCRIPTION='Beige Accounting OIO DB version 6';

