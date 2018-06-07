select 1 as SOURCETYPE, PURCHASEINVOICE.:IDNAME as SOURCEID, PURCHASEINVOICE.ITSDATE,
'SalesTaxFromPurchase' as ACCDEBIT, 2003 as SUBACCDEBITTYPE, TAX.ITSID as SUBACCDEBITID, TAX.ITSNAME as SUBACCDEBIT, sum(PURCHASEINVOICETAXLINE.ITSTOTAL) as DEBIT,
'AccPayable' as ACCCREDIT, 2004 as SUBACCCREDITTYPE, DEBTORCREDITOR.ITSID as SUBACCCREDITID, DEBTORCREDITOR.ITSNAME as SUBACCCREDIT, sum(PURCHASEINVOICETAXLINE.ITSTOTAL) as CREDIT
from PURCHASEINVOICETAXLINE
join PURCHASEINVOICE on PURCHASEINVOICE.ITSID=PURCHASEINVOICETAXLINE.ITSOWNER
join TAX on TAX.ITSID=PURCHASEINVOICETAXLINE.TAX
join DEBTORCREDITOR on DEBTORCREDITOR.ITSID=PURCHASEINVOICE.VENDOR
where PURCHASEINVOICETAXLINE.REVERSEDID is null and PURCHASEINVOICE.REVERSEDID is null and HASMADEACCENTRIES=0 and TAX.ITSTYPE in (1,2) and TAX.DUEMETHOD=1 and PURCHASEINVOICE.PAYMENTTOTAL>=PURCHASEINVOICE.ITSTOTAL :WHEREADD
group by SOURCETYPE, SOURCEID, ITSDATE, ACCDEBIT, SUBACCDEBITTYPE, SUBACCDEBITID, SUBACCDEBIT, ACCCREDIT, SUBACCCREDITTYPE, SUBACCCREDITID, SUBACCCREDIT
