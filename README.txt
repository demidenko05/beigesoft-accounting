site: https://sites.google.com/site/beigesoftware

Beigesoft ™ Accounting library.

It implements accounting models and services, e.g. AccountingEntry, SalesInvoice, service that fill payroll taxes according tax percentage table, etc.

Version 1.1.6:
*Added "omit sales tax for overseas sales/purchase" - if customer/vendor is marked as "foreigner" then sales taxes will be omitted in the invoice.
*Fixed internationalization - decimal separators...
*Added advanced internationalization for printing overseas sales invoice in buyer's native language for overseas buyers who bought your products for business purposes.
*Fixed mistake of reversing single line in invoices form.
*Fixed sales taxes grouping rounding error (when round(2.244 + 2.244) != round(2.244) + round(2.244)).
*Fixed dis-balance when subaccount name is changed. Added more slow services that is not affected by changing subaccount name.
 To recalculate all intermediate balances - in accounting settings change "Balance store period" from "monthly to weekly", then print report "Trial balance", then change "Balance store period" back.
*Added unit of measure and quantity for services (for purchase/sale).

Version 1.1.5:
Fixed purchase return sales tax non-extracted.
Added JAR signing.
Fixed crossplatform file.separator.

licenses:
GNU General Public License version 2
http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html

3-D PARTY LICENSES:
DejaVu fonts by Bitstream:
https://dejavu-fonts.github.io/License.html
