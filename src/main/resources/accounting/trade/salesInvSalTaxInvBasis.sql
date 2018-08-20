select TAX as TAXID, ITSPERCENTAGE, sum(SUBTOTAL) as TAXABLE, sum(FOREIGNSUBTOTAL) as FOREIGNTAXABLE
from
(
  select TAXCATEGORY, SUBTOTAL, FOREIGNSUBTOTAL
  from SALESINVOICELINE 
  where SALESINVOICELINE.REVERSEDID is null and SALESINVOICELINE.ITSOWNER=:INVOICEID

  union all

  select TAXCATEGORY, SUBTOTAL, FOREIGNSUBTOTAL
  from SALESINVOICESERVICELINE 
  where SALESINVOICESERVICELINE.ITSOWNER=:INVOICEID
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID=TAXCATEGORY
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER=INVITEMTAXCATEGORY.ITSID
group by TAX;
