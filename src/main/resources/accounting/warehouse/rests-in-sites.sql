select WAREHOUSE.ITSNAME as WAREHOUSE, WAREHOUSESITE.ITSNAME as WAREHOUSESITE, INVITEM.ITSID as INVITEMID, INVITEM.ITSNAME as INVITEM, UNITOFMEASURE.ITSNAME  as UNITOFMEASURE, THEREST  
from WAREHOUSEREST
join INVITEM on INVITEM.ITSID=WAREHOUSEREST.INVITEM
join UNITOFMEASURE on UNITOFMEASURE.ITSID=WAREHOUSEREST.UNITOFMEASURE
join WAREHOUSESITE on WAREHOUSESITE.ITSID=WAREHOUSEREST.WAREHOUSESITE
join WAREHOUSE on WAREHOUSE.ITSID=WAREHOUSESITE.WAREHOUSE 
where THEREST!=0
order by WAREHOUSE.ITSNAME, WAREHOUSESITE.ITSNAME, INVITEM.ITSNAME;
