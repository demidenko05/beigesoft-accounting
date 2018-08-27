select TAXCATEGORY as TAXCATID, TAX as TAXID, ITSPERCENTAGE, sum(SUBTOTAL) as SUBTOTAL, sum(FOREIGNSUBTOTAL) as FOREIGNSUBTOTAL, sum(ITSTOTAL) as ITSTOTAL, sum(FOREIGNTOTAL) as FOREIGNTOTAL
from
(
  select TAXCATEGORY, SUBTOTAL, FOREIGNSUBTOTAL, ITSTOTAL, FOREIGNTOTAL
  from PURCHASEINVOICELINE 
  where TAXCATEGORY is not null and REVERSEDID is null and ITSOWNER=:INVOICEID

  union all

  select TAXCATEGORY, SUBTOTAL, FOREIGNSUBTOTAL, ITSTOTAL, FOREIGNTOTAL
  from PURCHASEINVOICESERVICELINE 
  where TAXCATEGORY is not null and ITSOWNER=:INVOICEID
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID=TAXCATEGORY
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER=INVITEMTAXCATEGORY.ITSID
group by TAXCATID, TAX, ITSPERCENTAGE;
