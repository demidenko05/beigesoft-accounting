select TAXCATEGORY as TAXCATID, TAX as TAXID, TAX.ITSNAME as TAXNAME, INVITEMTAXCATEGORYLINE.ITSPERCENTAGE as ITSPERCENTAGE, sum(SUBTOTAL) as SUBTOTAL, sum(FOREIGNSUBTOTAL) as FOREIGNSUBTOTAL
from
( select PURCHASEINVOICELINE.TAXCATEGORY, PURCHASERETURNLINE.SUBTOTAL, PURCHASERETURNLINE.FOREIGNSUBTOTAL
  from PURCHASERETURNLINE
  join PURCHASEINVOICELINE on PURCHASEINVOICELINE.ITSID=PURCHASERETURNLINE.PURCHASEINVOICELINE
  where PURCHASEINVOICELINE.TAXCATEGORY is not null and PURCHASERETURNLINE.REVERSEDID is null and PURCHASERETURNLINE.ITSOWNER=:INVOICEID
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID=ALL_LINES.TAXCATEGORY
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER=INVITEMTAXCATEGORY.ITSID
join TAX on INVITEMTAXCATEGORYLINE.TAX=TAX.ITSID
group by TAXCATID, TAXID, TAXNAME, INVITEMTAXCATEGORYLINE.ITSPERCENTAGE
order by TAXID;
