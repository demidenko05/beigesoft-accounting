select TAX as TAXID, TAX.ITSNAME as TAXNAME, sum(ITSTOTAL) as TOTALTAX, sum(FOREIGNTOTALTAXES) as FOREIGNTOTALTAXES
from
(
  select TAX, ITSTOTAL, FOREIGNTOTALTAXES
  from :TGOODTXLN 
  where REVERSEDID is null and INVOICEID=:INVOICEID
) as ALL_LINES
join TAX on ALL_LINES.TAX=TAX.ITSID
group by TAX, TAXNAME;
