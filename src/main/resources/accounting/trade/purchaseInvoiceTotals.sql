select sum(SUBTOTAL) as SUBTOTAL, sum(ITSTOTAL) as ITSTOTAL, sum(TOTALTAXES) as TOTALTAXES, sum(FOREIGNSUBTOTAL) as FOREIGNSUBTOTAL, sum(FOREIGNTOTAL) as FOREIGNTOTAL, sum(FOREIGNTOTALTAXES) as FOREIGNTOTALTAXES
from
(
  select SUBTOTAL, ITSTOTAL, 0 as TOTALTAXES, FOREIGNSUBTOTAL, 0 as FOREIGNTOTALTAXES, FOREIGNTOTAL
  from PURCHASEINVOICELINE 
  where REVERSEDID is null and ITSOWNER=:ITSOWNER

  union all

  select SUBTOTAL, ITSTOTAL, 0 as TOTALTAXES, FOREIGNSUBTOTAL, 0 as FOREIGNTOTALTAXES, FOREIGNTOTAL
  from PURCHASEINVOICESERVICELINE 
  where ITSOWNER=:ITSOWNER

  union all

  select 0 as SUBTOTAL, 0 as ITSTOTAL, ITSTOTAL as TOTALTAXES, 0 as FOREIGNSUBTOTAL, FOREIGNTOTALTAXES, 0 FOREIGNTOTAL
  from PURCHASEINVOICETAXLINE 
  where ITSOWNER=:ITSOWNER
) as ALL_LINES;

