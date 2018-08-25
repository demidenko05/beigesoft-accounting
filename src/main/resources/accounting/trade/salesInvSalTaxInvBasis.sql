select TAX as TAXID, ITSPERCENTAGE, sum(SUBTOTAL) as SUBTOTAL, sum(FOREIGNSUBTOTAL) as FOREIGNSUBTOTAL, sum(ITSTOTAL) as ITSTOTAL, sum(FOREIGNTOTAL) as FOREIGNTOTAL
from
(
  select TAXCATEGORY, SUBTOTAL, FOREIGNSUBTOTAL, ITSTOTAL, FOREIGNTOTAL
  from SALESINVOICELINE 
  where TAXCATEGORY is not null and SALESINVOICELINE.REVERSEDID is null and SALESINVOICELINE.ITSOWNER=:INVOICEID

  union all

  select TAXCATEGORY, SUBTOTAL, FOREIGNSUBTOTAL, ITSTOTAL, FOREIGNTOTAL
  from SALESINVOICESERVICELINE 
  where TAXCATEGORY is not null and SALESINVOICESERVICELINE.ITSOWNER=:INVOICEID
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID=TAXCATEGORY
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER=INVITEMTAXCATEGORY.ITSID
group by TAX;
