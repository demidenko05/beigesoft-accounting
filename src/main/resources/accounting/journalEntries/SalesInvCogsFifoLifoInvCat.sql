select 2 as SOURCETYPE, DRAWINGOWNERID as SOURCEID, ITSDATE,
'COGS' as ACCDEBIT, 2001 as SUBACCDEBITTYPE, INVITEMCATEGORY.ITSID as SUBACCDEBITID, INVITEMCATEGORY.ITSNAME as SUBACCDEBIT, sum(ITSTOTAL) as DEBIT,
'Inventory' as ACCCREDIT, 2001 as SUBACCCREDITTYPE, INVITEMCATEGORY.ITSID as SUBACCCREDITID, INVITEMCATEGORY.ITSNAME as SUBACCCREDIT, sum(ITSTOTAL) as CREDIT
from COGSENTRY
join INVITEM on INVITEM.ITSID = COGSENTRY.INVITEM
join INVITEMCATEGORY on INVITEMCATEGORY.ITSID = INVITEM.ITSCATEGORY
where REVERSEDID is null and DRAWINGTYPE=1002 :WHEREADD
group by SOURCETYPE, SOURCEID, ITSDATE, ACCDEBIT, SUBACCDEBITTYPE, SUBACCDEBITID, SUBACCDEBIT, ACCCREDIT, SUBACCCREDITTYPE, SUBACCCREDITID, SUBACCCREDIT
