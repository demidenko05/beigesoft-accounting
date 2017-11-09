select 13 as SOURCETYPE, PURCHASERETURN.:IDNAME as SOURCEID, PURCHASERETURN.ITSDATE,
null as ACCDEBIT, null as SUBACCDEBITTYPE, null as SUBACCDEBITID, null as SUBACCDEBIT, 0 as DEBIT,
'SalesTaxFromPurchReturns' as ACCCREDIT, 2003 as SUBACCCREDITTYPE, TAX.ITSID as SUBACCCREDITID, TAX.ITSNAME as SUBACCCREDIT, sum(PURCHASERETURNTAXLINE.ITSTOTAL) as CREDIT
from PURCHASERETURNTAXLINE
join PURCHASERETURN on PURCHASERETURN.ITSID=PURCHASERETURNTAXLINE.ITSOWNER
join TAX on TAX.ITSID=PURCHASERETURNTAXLINE.TAX
where PURCHASERETURNTAXLINE.REVERSEDID is null and PURCHASERETURN.REVERSEDID is null and HASMADEACCENTRIES=0 and TAX.ITSTYPE in (1,2) and TAX.DUEMETHOD=0 :WHEREADD
group by SOURCETYPE, SOURCEID, ITSDATE, ACCDEBIT, SUBACCDEBITTYPE, SUBACCDEBITID, SUBACCDEBIT, ACCCREDIT, SUBACCCREDITTYPE, SUBACCCREDITID, SUBACCCREDIT
