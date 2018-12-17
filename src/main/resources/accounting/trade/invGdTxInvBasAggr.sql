select TAXCATEGORY as TAXCATID, TAX as TAXID, TAX.ITSNAME as TAXNAME, INVITEMTAXCATEGORYLINE.ITSPERCENTAGE as ITSPERCENTAGE, sum(SUBTOTAL) as SUBTOTAL, sum(FOREIGNSUBTOTAL) as FOREIGNSUBTOTAL, sum(ITSTOTAL) as ITSTOTAL, sum(FOREIGNTOTAL) as FOREIGNTOTAL
from
( select TAXCATEGORY, SUBTOTAL, FOREIGNSUBTOTAL, ITSTOTAL, FOREIGNTOTAL
  from :TGOODLN 
  where TAXCATEGORY is not null and REVERSEDID is null and ITSOWNER=:INVOICEID
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID=ALL_LINES.TAXCATEGORY
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER=INVITEMTAXCATEGORY.ITSID
join TAX on INVITEMTAXCATEGORYLINE.TAX=TAX.ITSID
group by TAXCATID, TAXID, TAXNAME, INVITEMTAXCATEGORYLINE.ITSPERCENTAGE;
