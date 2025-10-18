
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <title>Sales Register</title>
        <script>
            $(document).ready(function () {
                $("#salesman1").autocomplete({
                    autoFocus: true,
                    source: function (request, response) {

                        $.getJSON("salesman.fin", {
                            term: request.term

                        }, function (result) {
                            //alert();
                            var wordlist = ($.map(result, function (item) {
                                return {value: item.param1}
                            }));
                            var re = $.ui.autocomplete.escapeRegex(request.term);
                            var matcher = new RegExp("^" + re, "i");
                            var a = $.grep(wordlist, function (item, index) {
                                return matcher.test(item.value);
                            });
                            response(a);
                        });
                    }
                });
            });
        </script>
        <script>
            $(document).ready(function () {
                $("#registration").validate({

                    rules: {
                        param1: {required: true, date: true},
                        param2: {required: true, date: true},
                        param3: {required: true},
                        param4: {required: true}
                    },
                    messages: {
                        param1: '<div class="tool">* field is required.</div>',
                        param2: '<div class="tool">* field is required.</div>',
                        param3: '<div class="tool">* field is required.</div>',
                        param4: '<div class="tool">* field is required.</div>'
                    }

                });
                $("#excelButton").on("click", function () {
                    var param = 100;
                    if($('#param4').val() == 'INVOICE'){
                        param=99;
                    }
                    //alert(param);
                    $('#registration').attr("action", "gstinReport.fin?param="+param);
                    $("#registration").submit();
                });
                $("#b2bButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=1");
                    $("#registration").submit();
                });
                $("#b2cButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=2");
                    $("#registration").submit();
                });
                $("#exemptedButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=3");
                    $("#registration").submit();
                });
                $("#exportSEZButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=4");
                    $("#registration").submit();
                });
                $("#localSEZButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=5");
                    $("#registration").submit();
                });
                $("#debitNoteButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=7");
                    $("#registration").submit();
                });
                $("#creditNoteButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=8");
                    $("#registration").submit();
                });
                $("#journalInput").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=6");
                    $("#registration").submit();
                });
                $("#journalVoucher").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=9");
                    $("#registration").submit();
                });
                $("#sacInward").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=10");
                    $("#registration").submit();
                });
                $("#sacOutward").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=11");
                    $("#registration").submit();
                });
                $("#exemptedButtonInv").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=12");
                    $("#registration").submit();
                });
                $("#expenseHeaderGST").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=13");
                    $("#registration").submit();
                });
            });

        </script>
    </head>
    <body>
        <div class="main">
            <header>
                Sales Register
            </header>
            <html:form method="post" action="sales.fin" id="registration" commandName="reportbean">
                <table class="tablec">
                    <col width="10%">
                    <col width="80%">

                    <tr>
                        <td><label>Start Date</label></td>
                        <td><html:input type="text" path="param1" id="date" class="medium"/></td>

                    </tr>
                    <tr>
                        <td><label>End Date</label></td>
                        <td><html:input type="text" path="param2" id="date2" class="medium" /></td>
                    </tr>
                    <tr>
                        <td><label>Branch</label></td>
                        <td><html:select path="param3">
                                <html:option value="">SELECT</html:option>
                                <html:option value="SFP">SFP</html:option>
                                <html:option value="SFPM">SFPM</html:option>
                                <html:option value="SFPC">SFPC</html:option>
                                <html:option value="SFPK">SFPK</html:option>
                                <html:option value="SFPG">SFPG</html:option>
                            </html:select>
                        </td>
                    </tr>
                    <tr>
                        <td><label>Bill Type</label></td>
                        <td><html:select path="param4" id="param4">
                                <html:option value="">SELECT</html:option>
                                <html:option value="INVOICE">INVOICE</html:option>
                                <html:option value="EXPENSE">EXPENSE</html:option>
                            </html:select>
                        </td>
                    </tr>
                    <tr>
                        <td><label>Sales By</label></td>
                        <td><html:input type="text" path="param5" id="salesman1" class="largeXL"/></td>
                    </tr>
                </table>
                <table class="tb2">
                    <tr>
                        <td><button type="button" class="finbutton" id="excelButton">Excel</button></td>
                        <td><button type="button" class="finbutton" id="b2bButton">B2B</button></td>
                        <td><button type="button" class="finbutton" id="b2cButton">B2C</button></td>
                        <td><button type="button" class="finbutton" id="exemptedButton">EXEMPTED</button></td>
                        <td><button type="button" class="finbutton" id="exportSEZButton">EXPORT SEZ</button></td>
                        <td><button type="button" class="finbutton" id="localSEZButton">LOCAL SEZ</button></td>
                        <td><button type="button" class="finbutton" id="debitNoteButton">DEBIT NOTE</button></td>
                        <td><button type="button" class="finbutton" id="creditNoteButton">CREDIT NOTE</button></td>
                        <td><button type="button" class="finbutton" id="journalInput">Journal Input</button></td>
                        <td><button type="button" class="finbutton" id="journalVoucher">Journal Voucher</button></td>
                        <td><button type="button" class="finbutton" id="sacInward">HSN-Inward</button></td>
                        <td><button type="button" class="finbutton" id="sacOutward">HSN-Outward</button></td>
                        <td><button type="button" class="finbutton" id="exemptedButtonInv">EXEMPTED INVOICE</button></td>
                        <td><button type="button" class="finbutton" id="expenseHeaderGST">EXPENSE-GST</button></td>
                    </tr>
                </table>

            </html:form>
        </div>
    </body>
</html>
