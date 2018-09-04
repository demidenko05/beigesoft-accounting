select ILID, TAXCATEGORY as TAXCATID, TAX as TAXID, ITSPERCENTAGE, sum(TOTALTAXES) as TOTALTAXES
from
(
  select ITSID as ILID, TAXCATEGORY, TOTALTAXES
  from SALESRETURNLINE 
  where REVERSEDID is null and TAXCATEGORY is not null and ITSOWNER=:INVOICEID
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID=TAXCATEGORY
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER=INVITEMTAXCATEGORY.ITSID
group by ILID, TAXCATID, TAXID, ITSPERCENTAGE;
