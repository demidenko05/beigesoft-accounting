select ILID, TAXCATEGORY as TAXCATID, TAX as TAXID, ITSPERCENTAGE, sum(SUBTOTAL) as SUBTOTAL, sum(FOREIGNSUBTOTAL) as FOREIGNSUBTOTAL, sum(ITSTOTAL) as ITSTOTAL, sum(FOREIGNTOTAL) as FOREIGNTOTAL
from
(
  select ITSID as ILID, TAXCATEGORY, SUBTOTAL, FOREIGNSUBTOTAL, ITSTOTAL, FOREIGNTOTAL
  from SALESINVOICELINE 
  where TAXCATEGORY is not null and SALESINVOICELINE.REVERSEDID is null and SALESINVOICELINE.ITSOWNER=:INVOICEID

  union all

  select -ITSID as ILID, TAXCATEGORY, SUBTOTAL, FOREIGNSUBTOTAL, ITSTOTAL, FOREIGNTOTAL
  from SALESINVOICESERVICELINE 
  where TAXCATEGORY is not null and SALESINVOICESERVICELINE.ITSOWNER=:INVOICEID
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID=TAXCATEGORY
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER=INVITEMTAXCATEGORY.ITSID
group by ILID, TAXCATID, TAX order by ILID;
