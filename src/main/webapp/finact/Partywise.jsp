
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/finance.js"></script>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Ageing</title>

    </head>
    <script>
        $(function () {
            $("#partyview").dialog({
                autoOpen: false,
                resizable: false,
                height: "700",
                width: "1200",
                modal: true,
                show: {
                    effect: "fold",
                    duration: 400
                },
                close: function (e) {
                    window.location = "Outstanding.fin";
                }
            });
            $("#viewbutton").on("click", function () {
                var params = 'p1=' + $("#billToName").val() + '&p2=' + $("#date").val() + '&p3=' + $('input[name=param3]:checked').val() +
                        '&p4=' + $('input[name=param4]:checked').val() + '&p5=' + $('input[name=param5]:checked').val() + '&p6=' + $("#param6").val() +
                        '&p7=' + $("#salesman1").val() + '&p8=' + $("#partyAcctCode").val()+ '&p9=' + $("#rtype").val();
                
                if($("#rtype").val() == '2'){
                    $("#partyview").load(encodeURI('outstandingSalesView.fin?' + params)).dialog("open");
                }
                else{
                    $("#partyview").load(encodeURI('outstandingPartyView.fin?' + params)).dialog("open");
                }                
            });
            $("#viewbuttonnew").on("click", function () {
                            var params = 'p1=' + $("#billToName").val() + '&p2=' + $("#date").val() + '&p3=' + $('input[name=param3]:checked').val() +
                                    '&p4=' + $('input[name=param4]:checked').val() + '&p5=' + $('input[name=param5]:checked').val() + '&p6=' + $("#param6").val() +
                                    '&p7=' + $("#salesman1").val() + '&p8=' + $("#partyAcctCode").val()+ '&p9=' + $("#rtype").val();

                            if($("#rtype").val() == '2'){
                                $("#partyview").load(encodeURI('outstandingSalesView.fin?' + params)).dialog("open");
                            }
                            else{
                                $("#partyview").load(encodeURI('outstandingPartyViewNew.fin?' + params)).dialog("open");
                            }
                        });
            $("#partySummary").on("click", function () {
                $('#partyform').attr("action", "partywiseSummary.fin");
                $("#partyform").submit();
            });
            $("#partyDetail").on("click", function () {
                $('#partyform').attr("action", "partywiseDetail.fin");
                $("#partyform").submit();
            });
            $("#auditDetail").on("click", function () {
                $('#partyform').attr("action", "partywiseAuditSummary.fin");
                $("#partyform").submit();
            });
            $("#bankAuditDetail").on("click", function () {
                $('#partyform').attr("action", "partywiseAuditSummary2.fin");
                $("#partyform").submit();
            });
            $("#paymentData").on("click", function () {
                $('#partyform').attr("action", "partyPaymentDataXLSReport.fin");
                $("#partyform").submit();
            });
        });
        $(document).ready(billToName);
        $(document).ready(function () {

            $('#rtype').change(function () {
                var value = $(this).val();
                if (value == '1') {
                    $('#salesman1').attr('readonly', true);
                    $('#partywise1').removeAttr('readonly');
                    $("#salesman1").addClass("readonly");
                    $("#partywise1").removeClass("readonly");
                    $("#salesman1").val('');
                } else if (value == '2') {
                    $('#partywise1').attr('readonly', true);
                    $('#salesman1').removeAttr('readonly');
                    $("#partywise1").addClass("readonly");
                    $("#salesman1").removeClass("readonly");
                    $('#partywise1').val('');
                    $('#codeid').val('');
                }

            });
        });

    </script>
    <style>

    </style>
    <body>
        <div class="main">
            <header>
                Outstanding Report
            </header>
            <html:form method="post" action="partyform" id="partyform" commandName="reportbean">
                <table class="tablec">
                    <col width="35%">
                    <col width="60%">
                    <tr>
                        <td><label>Report Type</label></td>
                        <td><html:select id="rtype" path="param9">
                                <html:option value="1">PartyWise</html:option>
                                <html:option value="2">SalesmanWise </html:option>
                            </html:select>
                        </td>  
                    </tr>
                    <tr>
                        <td><label>Party</label></td>
                        <td>
                            <html:input type="text" path="param1" id="billToName" name="party" class="largeXL" onchange="billToName()"/>
                            <html:input type="text" path="param8" id="partyAcctCode" class="small readonly" readonly="true"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>Salesman</label></td>
                        <td><html:input type="text" path="param7" id="salesman1" readonly="true" class="largeXL readonly"/></td>

                    </tr>
                    <tr>
                        <td><label>As on date</label></td>
                        <td><html:input type="text" path="param2" id="date" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>Branch</label></td>
                        <td><html:select path="param6">
                                <html:option value="">SELECT</html:option>
                                <html:option value="SFP">Delhi </html:option>
                                <html:option value="SFPM">Mumbai</html:option>
                                <html:option value="SFPC">Chennai  </html:option>
                                <html:option value="SFPK">Kochi</html:option>
                                <html:option value="SFPG">Gandhidham</html:option>
                            </html:select>
                        </td>  
                    </tr>
                    <table class="tablec">
                        <col width="57%">
                        <tr>
                            <td><label>Bill Type</label></td>
                            <td><html:radiobutton path="param3" class="rd1" value="DEBTOR" checked="checked"/>DEBTOR </td>
                            <td><html:radiobutton path="param3" class="rd4" value="CREDITOR"/>CREDITOR</td>

                        </tr>
                        <tr>
                            <td><label>LOCAL / FOREIGN</label></td>
                            <td><html:radiobutton path="param4" class="rd2" value="LOCAL" checked="checked"/>LOCAL</td>
                            <td><html:radiobutton path="param4" class="rd2" value="FOREIGN"/>FOREIGN</td>
                        </tr>
                        <tr>
                            <td><label>Format</label></td>
                            <td><html:radiobutton path="param5" class="rd3" value="PDF" checked="checked"/>PDF</td>
                            <td><html:radiobutton path="param5" class="rd3" value="XLS"/>XLS</td>
                        </tr>
                    </table>
                    
                </table>
                <table class="tb2">
                    <tr>
                        <td><button type="button" id="viewbutton" class="finbutton">View</button></td>
                        <td><button type="button" id="partySummary" class="finbutton">Summary</button></td>
                        <td><button type="submit" id="partyDetail" class="finbutton">Detail</button></td>
                        <td><button type="submit" id="auditDetail" class="finbutton">Audit</button></td>
                        <td><button type="submit" id="bankAuditDetail" class="finbutton">Bank Audit</button></td>
                        <td><button type="button" class="finbutton" id="paymentData">PAYMENT-DATA</button></td>
                        <td><button type="reset" class="finbutton" id="resetb">Reset</button></td>
                        <td><button type="button" id="viewbuttonnew" class="finbutton">New</button></td>
                    </tr>
                </table>
            </html:form>
        </div>
        <div id="partyview" title="OutstandingPartyView">
        </div>

    </body>
</html>
