select 2 as SOURCETYPE, SALESINVOICE.:IDNAME as SOURCEID, SALESINVOICE.ITSDATE as ITSDATE,
null as ACCDEBIT, null as SUBACCDEBITTYPE, null as SUBACCDEBITID, null as SUBACCDEBIT, 0 as DEBIT,
'SalesTaxPay' as ACCCREDIT, 2003 as SUBACCCREDITTYPE, TAX.ITSID as SUBACCCREDITID, TAX.ITSNAME as SUBACCCREDIT, sum(SALESINVOICETAXLINE.ITSTOTAL) as CREDIT
from SALESINVOICETAXLINE
join SALESINVOICE on SALESINVOICE.ITSID = SALESINVOICETAXLINE.ITSOWNER
join TAX on TAX.ITSID = SALESINVOICETAXLINE.TAX
where SALESINVOICETAXLINE.REVERSEDID is null and SALESINVOICE.REVERSEDID is null and HASMADEACCENTRIES = 0 and TAX.ITSTYPE in (1,2) and TAX.DUEMETHOD = 0 :WHEREADD
group by SOURCETYPE, SOURCEID, ITSDATE, ACCDEBIT, SUBACCDEBITTYPE, SUBACCDEBITID, SUBACCDEBIT, ACCCREDIT, SUBACCCREDITTYPE, SUBACCCREDITID, SUBACCCREDIT
