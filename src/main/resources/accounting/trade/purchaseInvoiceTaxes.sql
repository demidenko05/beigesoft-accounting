select TAX as TAXID,  sum(ITSTOTAL) as TOTALTAX, sum(FOREIGNTOTALTAXES) as FOREIGNTOTALTAXES
from
(
  select TAX, ITSTOTAL, FOREIGNTOTALTAXES
  from PURCHASEINVOICEGOODSTAXLINE 
  where REVERSEDID is null and INVOICEID=:INVOICEID

  union all

  select TAX, ITSTOTAL, FOREIGNTOTALTAXES
  from PURCHASEINVOICESERVICETAXLINE 
  where INVOICEID=:INVOICEID
) as ALL_LINES
group by TAX;
