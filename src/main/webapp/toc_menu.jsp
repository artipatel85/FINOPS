<%@ page import="com.finops.admin.model.LoginBean"%>
<%@include file="taglibs.jsp" %>


<html:html>
    <head>
        <title>Shikhar FWD Pvt LTD</title>

        <link rel="StyleSheet" href="dtree.css" type="text/css" />
        <script type="text/javascript" src="dtree.js"></script>
        <script lang="javascript">
            function showFrame() {
                if (document.getElementById('treemenu').style.display === "block") {
                    document.getElementById('treemenu').style.display = "none";
                    window.parent.document.body.cols = "2%,*";
                } else {
                    document.getElementById('treemenu').style.display = "block";
                    window.parent.document.body.cols = "16%,*";
                }
            }
        </script>
    </head>

    <body style="background-color:#dbdada">

        <div class="dtree">
            <A onclick=showFrame() href="#"><IMG alt="Show menu" src="images/menu2.gif" border=0></A>
        </div>
        <div class="dtree" id="treemenu" style="display:block">
            <%
                String username = "";
                String name = "";
                String user = "";
                String first = "";
                String rest = "";
                String userRole = "";
                LoginBean loginVoObj = (LoginBean) session.getAttribute("loginLst");
                if (null != loginVoObj) {
                    name = loginVoObj.getUserBean().getUserId();
                    userRole = loginVoObj.getUserBean().getRole();
                }
                first = name.substring(0, 1);

                rest = name.substring(1);
                first = first.toUpperCase();
                username = first + rest;
                String lastAccess = "";
                int acctYear = loginVoObj.getPeriodBean().getAcctYear();

            %>  

            <script type="text/javascript">

                var userID = '<%= name%>';
                d = new dTree('d');

                d.add(0, -1, '<%=username%> / <%=acctYear%>', 'homePage.do?method=showHome', 'Home Page', 'main');
                    d.add(1, 0, 'Administrator');

                <% if ("A".equals(userRole)) {%>

                    //d.add(2, 1, 'Form', 'form.do?invoke=displayForm', 'Form', 'main');
                    d.add(2, 1, 'Form', 'formSearch.fin', 'Form', 'main');
                    d.add(3,1,'User','adminSearch.fin?param=User','user','main');
                    //d.add(3, 1, 'User', 'userDetails.do?invoke=displayUserProfile&formId=1001', 'User', 'main', 'images/user.jpg');
                    d.add(3, 1, 'Role', 'adminSearch.fin?param=Role', 'Role', 'main');

                <%}%>

                    // d.add(4, 1, 'Company ', 'company.do?invoke=firstTimeCompany&formId=1002', 'company', 'main', 'images/company.jpg');
                    d.add(4, 1, 'Company ', 'companySearch.fin', 'company', 'main', 'images/company.jpg');
                    d.add(5, 0, 'Setting');
                    d.add(6, 5, 'General');
                    //d.add(7, 6, 'Partner', 'applyPartner.do?param=firsttimePartner&formId=1003', 'Partner', 'main');
                    d.add(7.1, 6, 'Partner - KYC', 'partnerSearch.fin?param=KYC', 'Partner', 'main');
                    //d.add(8, 6, 'Partner Account - Legacy', 'applyPartner.do?param=firsttimeApartner&formId=1004', 'PartnerAccount', 'main');
                    d.add(8.1, 6, 'Partner Account', 'partnerSearch.fin?param=PartnerAccount', 'PartnerAccount', 'main');
                    d.add(9, 5, 'Operation');
                    d.add(10, 9, 'Zone', 'zones.do?invoke=displayZones&formId=1005', 'Zone', 'main');
                    //d.add(11, 9, 'Country', 'country.do?param=firsttimeCountry&formId=1006', 'Country', 'main');
                    d.add(11, 9, 'Country', 'adminSearch.fin?param=Country', 'Country', 'main');
                    //d.add(12, 9, 'Port', 'port.do?param=firsttimePort&formId=1007', 'Port', 'main');
                    d.add(12, 9, 'Port', 'adminSearch.fin?param=Port', 'Port', 'main');
                    d.add(12, 9, 'Terminal', 'terminalSearch.fin', 'Terminal', 'main');
                    d.add(13, 9, 'Currency', 'adminSearch.fin?param=Currency', 'Currency', 'main', 'images/currency.jpg');
                    //d.add(13, 9, 'Currency', 'currencySearch.fin', 'Currency', 'main', 'images/currency.jpg');
                    //d.add(14, 9, 'Service Term', 'serviceTerm.do?param=firsttimeServiceTerms&formId=1009', 'Service Term', 'main');
                    d.add(15, 9, 'Size', 'adminSearch.fin?param=Size', 'size', 'main');
                    //d.add(16, 9, 'Traffic Mode', 'trafficmode.do?invoke=displayTrafficMode&formId=1011', 'TrafficMode', 'main');
                    d.add(17, 9, 'Unit', 'adminSearch.fin?param=Unit', 'Unit', 'main');

                    d.add(18, 0, 'Freight');
                        d.add(19, 18, 'Sea');
                            d.add(20, 19, 'Sailing Schedule', 'sailingSchedule.fin', 'Sailing Schedule', 'main');
                            d.add(21, 19, 'Export');
                                d.add(22, 21, 'Shipping Order','so.fin?param=EXPORT', 'Shipping Order - Export', 'main');
                                d.add(23, 21, 'S/O Control', 'controlSO.do?method=firstTimeControl&formId=1015', 'S/O Control', 'main');
                                d.add(24, 21, 'Container Load Plan', 'clp.fin', 'Container Load Plan', 'main');
                                d.add(25, 21, 'Bill Of Lading ', 'bl.fin?param=EXPORT', 'Bill Of Lading', 'main');
                                d.add(26, 21, 'Job', 'job.fin?param=EXPORT', 'Job', 'main');
                                //d.add(27, 21, 'Master B/L', 'masterBL.do?invoke=displayMasterBL&formId=1019', 'Master B/L', 'main');
                            d.add(30, 19, 'Import');
                                d.add(59, 30, 'Shipping Order', 'so.fin?param=IMPORT', 'Shipping Order - Import', 'main');
                                d.add(60, 30, 'Bill Of Lading ', 'bl.fin?param=IMPORT', 'Bill Of Lading', 'main');
                                d.add(61, 30, 'Job', 'job.fin?param=IMPORT', 'importJob', 'main');

                    d.add(33, 18, 'Air');
                    d.add(34, 33, 'Export');
                    d.add(35, 34, 'Shipping Instruction', 'si.fin?param=EXPORT', 'Shipper Instruction', 'main');
                    d.add(36, 34, 'House Air Waybill', 'hawb.fin?param=EXPORT', 'House Air Waybill', 'main');
                    d.add(38, 34, 'Job', 'airJob.fin?param=EXPORT', 'Job', 'main');
                    //d.add(39, 34, 'Master Air Waybill', 'masterHawb.do?invoke=fetchMasterHawb', 'Master Air Waybill', 'main');
                    d.add(40, 33, 'Import');
                    d.add(44, 9, 'Serial Number', 'docSerialNo.fin', 'DocSno', 'main');
                    d.add(45, 0, 'E-Tracking');
                    d.add(46, 45, 'Sea');
                    //d.add(47, 46, 'Export', 'tracking.do?method=searchLst&param=first', 'Tracking', 'main');
                    d.add(47, 46, 'Export', 'seaExportTracking.fin?formId=1029', 'Tracking', 'main');
                    //d.add(48, 46, 'Import', 'itracking.do?method=searchLst&param=first', 'Tracking', 'main');
                    d.add(48, 46, 'Import', 'seaExportTracking.fin?formId=1033', 'Tracking', 'main');
                    //d.add(49, 46, 'Cargo At Warehouse', 'cargoAtWarehouse.do?invoke=displayCargoAtWarehouse&formId=1030', 'Cargo At Warehouse', 'main');
                    //d.add(50, 46, 'Pending on Board List', 'pendingList.do?invoke=displayPendingList&formId=1028', 'Pending on Board List', 'main');
                    //d.add(51, 46, 'Analysis', 'analysis.do?invoke=displayAnalysis', 'Analysis', 'main');
                    d.add(52, 45, 'Air');
                    d.add(53, 52, 'Export', 'seaExportTracking.fin?formId=1031', 'Tracking', 'main');
                    d.add(54, 52, 'Import', 'seaExportTracking.fin?formId=1035', 'Tracking', 'main');
                    //d.add(53, 52, 'Export', 'sitracking.do?method=searchLst&param=first', 'Tracking', 'main');
                    //d.add(54, 52, 'Import', 'isitracking.do?method=searchLst&param=first', 'Tracking', 'main');
                    //d.add(55, 0, 'Update User Profile', 'userDetails.do?invoke=retrieveUser&userId=' + userID, 'Update User Profile', 'main');
                    //d.add(57, 46, 'Communication', 'commsearch.jsp', 'Communication', 'main');
                    //d.add(58, 46, 'Pending MBL on Bl', 'pendingMbl.do?invoke=displayPendingMbl&formId=1028', 'Pending MBL on BL', 'main');

                    d.add(62, 40, 'Shipping Instruction', 'si.fin?param=IMPORT', 'Shipper Instruction - Import', 'main');
                    d.add(63, 40, 'House Airway Bill', 'hawb.fin?param=IMPORT', 'House Air Waybill imp***', 'main');
                    d.add(64, 40, 'Job', 'airJob.fin?param=IMPORT', 'Job', 'main');
                    //d.add(65, 46, 'Container Not On Board ', 'contNotOnBord.do?invoke=displayContNotOnBord', 'Container Not On Board', 'main');

                    d.add(80, 0, 'Finance');
                    d.add(81, 80, 'Setting');
                    d.add(82, 81, 'Chart Of Account');
                    //d.add(83, 82, 'Group', 'group.do?invoke=displayGroup', 'Group', 'main');
                    d.add(83, 82, 'Group', 'groupSearch.fin', 'Group', 'main');
                    //d.add(84, 82, 'Ledger', 'ledger.do?invoke=displayLedger', 'Ledger', 'main');
                    d.add(84, 82, 'Ledger', 'ledgerSearch.fin', 'Ledger', 'main');
                    d.add(85, 82, 'Listing', 'acctHier.do?invoke=display', 'Listing', 'main');
                    //d.add(85, 82, 'Create Period', 'period.do?invoke=displayPeriod', 'Listing', 'main');
                    d.add(999, 82, 'Account Year', 'period.fin', 'Listing', 'main');
                    d.add(133, 81, 'Masters');
                    //d.add(134, 133, 'Template', 'billtemplate.do?invoke=view', 'Template', 'main');
                    d.add(134, 133, 'Template', 'billTemplateSearch.fin', 'Template', 'main');
                    //d.add(135, 133, 'Tax Master', 'taxmaster.do?invoke=view', 'Tax Master', 'main');
                    d.add(136, 133, 'Tax Master', 'taxMasterSearch.fin', 'Tax Master', 'main');
                    d.add(86, 80, 'Partner Account');
                    d.add(122, 86, 'Import', 'applyPartner.do?param=importPartner', 'Import Partner', 'main');
                    d.add(123, 86, 'Link', 'ledger.do?invoke=linkPartner', 'Link Partner', 'main');
                    d.add(92, 80, 'Vouchers');
                    d.add(88, 92, 'Receipt', 'voucher.fin?param=RECEIPT', 'Receipt', 'main');
                    d.add(89, 92, 'Payment', 'voucher.fin?param=PAYMENT', 'Payment', 'main');
                    d.add(90, 92, 'Journal', 'voucher.fin?param=JOURNAL', 'Journal', 'main');
                    //d.add(91, 92, 'Contra', 'paymentVoucher.do?invoke=displayPayment&param=CONTRA', 'Contra', 'main');
                    d.add(91.1, 92, 'Contra', 'voucher.fin?param=CONTRA', 'Contra', 'main');
                    d.add(96, 80, 'Reports');
                    d.add(104, 96, 'Trial Balance', 'TrialPrimary.fin?parentId=1&acctName=Trial', 'Trial Balance', 'main');
                    d.add(107, 96, 'Bank/Cash Books', 'bankbook.do?invoke=bankBook&LEVEL=BANKBOOK_MAIN', 'Bank/Cash Books', 'main');
//                    d.add(107, 96, 'Reconciliation', 'vouchersummary.do?invoke=reconcillationHome', 'Bank/Cash Books', 'main');
                    d.add(108, 96, 'Reconciliation', 'reconciliation.fin', 'Reconciliation', 'main');
                    //d.add(108, 96, 'Ledger');
                    d.add(109, 96, 'Single Account', 'Trial.fin', 'Single Account', 'main');
                    //d.add(110, 108, 'All Accounts', 'ledgersummary.do?invoke=ledgerBook', 'All Accounts', 'main');
                    //d.add(111, 108, 'Group', 'trialbalance.do?invoke=trialPrimary&LEVEL=GROUPS', 'Group', 'main');
                    //d.add(112, 96, 'Journal Register', 'monthlysummary.do?invoke=monthlyRegister&LEVEL=JOURNAL', 'Journal Register', 'main');
                    //d.add(115, 96, 'Debit Note Register', 'monthlysummary.do?invoke=monthlyRegister&LEVEL=DEBIT', 'Debit Register', 'main');
                    //d.add(116, 96, 'Service Tax', 'servicetax.do?invoke=serviceTax', 'Service Tax', 'main');
                    d.add(117, 96, 'Outstanding', 'Outstanding.fin', 'Aging Receivable', 'main');
                    d.add(121, 96, 'Profitability', 'profitabilitySearch.fin', 'Profitability', 'main');
                    d.add(122, 96, 'TDS Register', 'TDS.fin?id=1', 'TDS', 'main');
                    d.add(123, 96, 'Sales Register', 'SalesRegister.fin', 'Sales Register', 'main');
                    d.add(118, 96, 'GST Json', 'gstJson.fin', 'Fixed Assets', 'main');
                    d.add(118, 96, 'GST Credit Match', 'gstincredit.fin', 'GSTIN', 'main');

                    d.add(119,96,'Audit Trail','auditTrail.fin','Audit Trail','main');
//                                d.add(120,96,'Cash Flow','fixedAssets.do?invoke=fixedAssets','Fixed Assets','main');
//                                d.add(121,96,'Fund Flow','fixedAssets.do?invoke=fixedAssets','Fixed Assets','main');
                    d.add(125, 80, 'Billing');
                    //d.add(128, 125, 'Invoice');
                    //d.add(129, 125, 'Billing', 'billing.do?invoke=view&PARAM=REVENUE', 'Billing', 'main');
                    d.add(128, 125, 'Invoice', 'viewBillList.fin?PARAM=REVENUE', 'Billing', 'main');
                    //d.add(130, 125, 'Expense');
                    //d.add(131, 125, 'Expense', 'billing.do?invoke=view&PARAM=EXPENSE', 'Expense', 'main');
                    d.add(129, 125, 'Expense', 'viewBillList.fin?PARAM=EXPENSE', 'Expense', 'main');
                    d.add(130, 125, 'Proforma', 'viewBillList.fin?PARAM=PROFORMA', 'Proforma', 'main');
                    d.add(131, 125, 'Bill Of Supply', 'viewBillList.fin?PARAM=BOS', 'Bill Of Supply', 'main');
                    d.add(132, 125, 'Bulk IRN', 'bulkCreateIRN.fin', 'Bulk IRN', 'main');
                    d.add(140, 125, 'Pending BL', 'pendingBL.fin', 'Pending BL', 'main');
                    d.add(141, 125, 'Utilities', 'retrieveFinactProps.fin', 'Util', 'main');
                    
                    d.add(151, 9, 'Salesman', 'adminSearch.fin?param=Salesman', 'SalesMan', 'main');
                    d.add(152, 9, 'Digital Copy', 'digitalCopy.fin', 'Digital Copy', 'main');
                    //d.add(9997, 0, 'Finance Utilities', 'retrieveFinactProps.fin', 'Util', 'main');

                    d.add(9998, 0, 'Help', 'finacc/help.html', 'Help', '_blank');
                    d.add(9999, 0, 'Logout', 'logout.fin', 'Logout', '_parent');
                    document.write(d);


            </script>

        </div>

    </body>

</html:html>