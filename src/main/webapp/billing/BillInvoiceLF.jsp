
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form method="post" action="loadBillingData1.fin" command="billingBean" id="billingForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta http-equiv="Pragma" content="no-cache">
            <meta http-equiv="Expires" content="-1">
            <title>BillTemplate</title>

            <script>
                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "billListGson.fin?blNo=${command.blNo}&revExp=${command.revExp}&proforma=${command.proforma}&billType=${command.billType}",
                        "serverSide": true,
                        "bJQueryUI": true,
                        "bRetrieve": true,
                        "bProcessing": true,
                        "bFilter": false,
                        "dom": '<"top">t',
                        "lengthMenu": [20, 30, 50],
                        "aoColumns": [
                            {
                                "mData": "billNo",
                                "render": function (mData, full, row) {
                                    var link = row['billId'];
                                    return '<input type="checkbox" name="hdrIds" value="' + link + '"/>';
                                }
                            },
                            {
                                "mData": "billId",
                                "render": function (mData, full, row) {
                                    var type = row['revExp'];
                                    var cond = row['irn'];
                                    if(cond === null){
                                        return '<a href="billingPDFReport.fin?PARAM='+type+'&irn=&trxid=' + mData + '" target="_blank"><img src="finactImages/icon_pdf.png" class="logopdf"></a>';
                                    }
                                    else{
                                        return '<a href="billingPDFReport.fin?PARAM='+type+'&irn='+cond+'&trxid=' + mData + '" target="_blank"><img src="finactImages/icon_pdf.png" class="logopdf"></a>';
                                    }
                                }
                            },
                            {
                                "mData": "billNo",
                                "render": function (mData, full, row) {
                                    var link = row['billId'];
                                    var type = row['revExp'];
                                    return '<a href="retrieveBill.fin?param=' + type + '&billId=' + link + '">' + mData + '</a>';
                                }
                            },
                            {"mData": "billDate"},
                            {"mData": "billtoName"},
                            {"mData": "seaAir"},
                            {"mData": "expImp"},
                            {"mData": "localForeign"},
                            {"mData": "billType"},
                            {"mData": "invNo"},
                            {"mData": "trxTotal", "className": "numberTextbox"}

                        ]
                    });


                    $("#tn").on('change', function () {
                        if (this.value == 'T' || this.value == 'N') {
                            $("#foreignBtn").removeAttr('disabled');
                            $("#foreignBtn").removeClass("readonly");
                        } else {
                            $("#foreignBtn").attr('disabled', true);
                            $("#foreignBtn").addClass("readonly");
                        }
                    });
                });


            </script>
        </head>
        <body>
            <div class="comdiv">

                <header>
                    <a href="billTemplateSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
                        <c:out value="${billingBean.revExp}"/>
                </header>
                <table class="tablec">    

                    <tr>
                        <td><label>TAXABLE / NON-TAXABLE</label></td>
                        <td><html:select path="taxableNonTaxable" id="tn">
                                <html:option value="">SELECT</html:option>
                                <html:option value="T">Taxable</html:option>
                                <html:option value="N">Non-Taxable</html:option>
                            </html:select>
                        </td>
                    </tr>

                </table>
            </div>

            <table class="tb2">
                <tr>
                    <td><input type="submit" name="action" class="finbutton" id="localBtn" value="LOCAL"></td>
                    <td><input type="submit" name="action" class="finbutton readonly" id="foreignBtn" value="FOREIGN" disabled="true"></td>
                </tr>
            </table>   

            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th></th>
                        <th>PDF</th>
                        <th>BILL NO</th>
                        <th>BILL DATE</th>
                        <th>BILL TO</th>
                        <th>SEA/AIR</th>
                        <th>EXP/IMP</th>
                        <th>LOCAL/FOREIGN</th>
                        <th>TYPE</th>
                        <th>INV NO</th>
                        <th>GRAND TOTAL</th>
                    </tr>
                </thead>

            </table>    
        </html:form>
    </body>
</html>
