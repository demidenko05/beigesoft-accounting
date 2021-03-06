select 2 as SOURCETYPE, SALESINVOICE.:IDNAME as SOURCEID, SALESINVOICE.ITSDATE as ITSDATE,
'AccReceivable' as ACCDEBIT, 2004 as SUBACCDEBITTYPE, DEBTORCREDITOR.ITSID as SUBACCDEBITID, DEBTORCREDITOR.ITSNAME as SUBACCDEBIT, sum(SALESINVOICELINE.SUBTOTAL) as DEBIT,
'Sales' as ACCCREDIT, 2001 as SUBACCCREDITTYPE, INVITEMCATEGORY.ITSID as SUBACCCREDITID, INVITEMCATEGORY.ITSNAME as SUBACCCREDIT, sum(SALESINVOICELINE.SUBTOTAL) as CREDIT
from SALESINVOICELINE
join SALESINVOICE on SALESINVOICE.ITSID= SALESINVOICELINE.ITSOWNER
join INVITEM on INVITEM.ITSID=SALESINVOICELINE.INVITEM
join INVITEMCATEGORY on INVITEMCATEGORY.ITSID=INVITEM.ITSCATEGORY
join DEBTORCREDITOR on DEBTORCREDITOR.ITSID=SALESINVOICE.CUSTOMER
where SALESINVOICELINE.REVERSEDID is null and SALESINVOICE.REVERSEDID is null and HASMADEACCENTRIES=0 :WHEREADD
group by SOURCETYPE, SOURCEID, ITSDATE, ACCDEBIT, SUBACCDEBITTYPE, SUBACCDEBITID, SUBACCDEBIT, ACCCREDIT, SUBACCCREDITTYPE, SUBACCCREDITID, SUBACCCREDIT
