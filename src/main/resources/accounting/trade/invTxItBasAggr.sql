select ILID, TAXCATEGORY as TAXCATID, TAX as TAXID, TAX.ITSNAME as TAXNAME, ITSPERCENTAGE, sum(TOTALTAXES) as TOTALTAXES, sum(FOREIGNTOTALTAXES) as FOREIGNTOTALTAXES
from
(
  select ITSID as ILID, TAXCATEGORY, TOTALTAXES, FOREIGNTOTALTAXES
  from :TGOODSLN 
  where REVERSEDID is null and TAXCATEGORY is not null and ITSOWNER=:INVOICEID

  union all

  select -ITSID as ILID, TAXCATEGORY, TOTALTAXES, FOREIGNTOTALTAXES
  from :TSERVICELN 
  where TAXCATEGORY is not null and ITSOWNER=:INVOICEID
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID=TAXCATEGORY
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER=INVITEMTAXCATEGORY.ITSID
join TAX on INVITEMTAXCATEGORYLINE.TAX=TAX.ITSID
group by ILID, TAXCATID, TAXID, TAXNAME, ITSPERCENTAGE;
