select sum(SUBTOTAL) as SUBTOTAL, sum(ITSTOTAL) as ITSTOTAL, sum(TOTALTAXES) as TOTALTAXES
from
(
  select SUBTOTAL, ITSTOTAL, 0 as TOTALTAXES
  from SALESRETURNLINE 
  where REVERSEDID is null and ITSOWNER=:ITSOWNER

  union all

  select 0 as SUBTOTAL, 0 as ITSTOTAL, ITSTOTAL as TOTALTAXES
  from SALESRETURNTAXLINE 
  where ITSOWNER=:ITSOWNER
) as ALL_LINES;
