select *
from
  (
    :SOURCEALL
  ) as UNION_RECORDS
order by ITSDATE desc;
