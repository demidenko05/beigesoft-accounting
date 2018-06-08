select 13 as SOURCETYPE, PURCHASERETURN.:IDNAME as SOURCEID, PURCHASERETURN.ITSDATE,
'ReturnsReceivable' as ACCDEBIT, 2004 as SUBACCDEBITTYPE, DEBTORCREDITOR.ITSID as SUBACCDEBITID, DEBTORCREDITOR.ITSNAME as SUBACCDEBIT, sum(PURCHASERETURNTAXLINE.ITSTOTAL) as DEBIT,
'SalesTaxFromPurchReturns' as ACCCREDIT, 2003 as SUBACCCREDITTYPE, TAX.ITSID as SUBACCCREDITID, TAX.ITSNAME as SUBACCCREDIT, sum(PURCHASERETURNTAXLINE.ITSTOTAL) as CREDIT
from PURCHASERETURNTAXLINE
join PURCHASERETURN on PURCHASERETURN.ITSID=PURCHASERETURNTAXLINE.ITSOWNER
join TAX on TAX.ITSID=PURCHASERETURNTAXLINE.TAX
join PURCHASEINVOICE  on PURCHASEINVOICE.ITSID=PURCHASERETURN.PURCHASEINVOICE
join DEBTORCREDITOR on DEBTORCREDITOR.ITSID=PURCHASEINVOICE.VENDOR
where PURCHASERETURNTAXLINE.REVERSEDID is null and PURCHASERETURN.REVERSEDID is null and PURCHASERETURN.HASMADEACCENTRIES=0 and TAX.ITSTYPE in (1,2) and TAX.DUEMETHOD=1 and PURCHASEINVOICE.PAYMENTTOTAL>=PURCHASEINVOICE.ITSTOTAL :WHEREADD
group by SOURCETYPE, SOURCEID, PURCHASERETURN.ITSDATE, ACCDEBIT, SUBACCDEBITTYPE, SUBACCDEBITID, SUBACCDEBIT, ACCCREDIT, SUBACCCREDITTYPE, SUBACCCREDITID, SUBACCCREDIT
