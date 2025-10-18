
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/finance.js"></script>
<html>
    <head>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>PaymentVoucher</title>
        <style>
            .rows{
                margin-top: 5px;
                border-radius: 0px;
                height: 500px;
                width: 100%;
                border: 1px solid #ddd;
                overflow-y: scroll;
            }
            .comdiv{
                border-radius: 0px;
                border: 1px solid #23527c;
                width: 100%;
                height: 100%;
                padding-bottom:10px;
            }
            .comdivfoot{
                border: 1px solid #ddd;
                margin-top: 5px;
                width: 100%;
                height: 100%;
            }
            .tablevou{
                border: 1px solid #ddd;
                border-bottom: 0px;
                width: 100%;
            }
            th {
                text-align: left;
                padding: 5px;
                font-size:11px; 
                border: 1px solid #ddd;
                background-color: #e9e9e9;
                font-family: Verdana, Arial;
            }
            .tablevou td{
                padding: 5px;
                border-bottom: 1px solid #ddd;

            }
            .tablevou tr{
                border: 0px solid #ddd;
            }
            .logoplus{
                display: inline-block;
                width:16px;
                height:15px;
                background-size: auto 15px;
                background-image: url(<c:url value="/finactImages/plus.png"/>);
                background-repeat: no-repeat;
            }
            .logominus{
                display: inline-block;
                width:16px;
                height:15px;
                background-size: auto 15px;
                background-image: url(<c:url value="/finactImages/minus.png"/>);
                background-repeat: no-repeat;
            }
            .tablefooter{
                padding: 10px;
            }

        </style>
        <script>

            function removeRow(src)
            {
                var oRow = src.parentElement.parentElement;
                $('#voucherForm').attr('action', "removeVoucherRow.fin?index=" + (oRow.rowIndex - 1)).submit();
                //document.all("tbl").deleteRow(oRow.rowIndex);

            }

            function addRow()
            {
                $('#voucherForm').attr('action', "addVoucherRow.fin").submit();
            }
            
            function printMe()
            {   
                if (confirm("DO U Wish To Print The Record??"))
                {
                    window.open("voucherPDFReport.fin?voucherType=CONTRA&trxId=${voucherBean.jeHdrId}");
                }
            }
            
            function chqDate(src){
                $(src).datepicker({
                    dateFormat: 'yy-mm-dd',
                    defaultDate: new Date()
                });
                //$(src).datepicker('setDate', new Date());
            }

            $(function () {
                $("#date11").datepicker({
                    dateFormat: 'yy-mm-dd',
                    defaultDate: new Date()
                });
                $("#date11").datepicker('setDate', new Date());
            });

            $(document).ready(hdrAccount);
            $(document).ready(currency);
            $(document).ready(function () {

                
                $("#voucherForm").validate({

                    rules: {
                        hdrAcctName: {required: true,minlength:3},
                        hdrAcctId: {min: 1},
                        currencyName: {required: true,minlength:3},
                        currencyId: {min: 1},
                        exchangeRate:{required:true},
                        hdrTotalAmount:{equalTo:'#lineTotalAmount'}
                    },
                    messages: {
                        hdrAcctName: {
                            required:'<label class="tool">* field is required.</label>',
                            minlength:'<label class="tool">* field value is invalid.</label>'
                        },
                        hdrAcctId: '<label class="tool">* Invalid value 0.</label>',
                        currencyName: {
                            required:'<label class="tool">* field is required.</label>',
                            minlength:'<label class="tool">* field value is invalid.</label>'
                        },
                        currencyId: '<label class="tool">* Invalid value 0.</label>',
                        exchangeRate: '<label class="tool">* field is required.</label>',
                        hdrTotalAmount: {
                            equalTo:'<label class="tool">* Total credit and debit should match.</label>'
                        }
                    }

                });
            
            });
            
            function ShowLoading(e) {
                if($("#voucherForm").valid()){
                    $("#submitButton").attr("disabled",true);
                    var div = document.createElement('div');
                    var img = document.createElement('img');
                    img.src = 'loading_bar.GIF';
                    div.innerHTML = "<b><u><i><label>Please wait while transaction is being processed...</label></i></u></b><br />";
                    div.style.cssText = 'position: fixed; top: 75%; left: 2%; z-index: 5000; width: 922px;height:800px; text-align: left;';
                    div.appendChild(img);
                    document.body.appendChild(div);
                    return true;
                }
                
            }
            
            function calculate(){
                var sum = 0;
                $('.rowAmount').each(function() {
                    sum += Number($(this).val());
                });
                $('input[name="lineTotalAmount"]').val(sum);
                $('input[name="hdrTotalAmount"]').val(sum);
            }
            

        </script>
    </head>
    <body>
        <div class="comdiv">
            <html:form autocomplete="off" method="post" action="saveVoucher.fin" id="voucherForm" onSubmit="ShowLoading()">
                <header>
                    CONTRA VOUCHER
                </header>
                <table class="tablec" width="100%">
                    <tr>
                        <th colspan="4"><c:out value="${voucherBean.errMsg}"/></th>
                    </tr>
                    <tr>
                        <td width="10%"><label>CONTRA NO</label></td>
                        <td width="50%">
                            <html:input path="voucherNo" class="medium"/>
                            <html:hidden path="jeHdrId" />
                            <html:hidden path="voucherType" value="CONTRA"/>
                            <html:hidden path="seqNo"/>
                        </td>
                        <td width="10%"><label>DATE</label></td>
                        <td><html:input path="jeDate" id="date" class="medium rightAlign"/></td>
                    </tr>
                    <tr>
                        <td><label>ACCOUNT</label></td>
                        <td>
                            <html:input path="hdrAcctName" id="hdrAcctName" class="large"/>
                            <html:input path="codeCombinationId" class="small readonly" readonly="true"/>
                        </td>
                        <td><label>TOTAL DEBIT</label></td>
                        <td><html:input path="hdrTotalAmount" class="medium rightAlign readonly" readonly="true"/></td>
                    </tr>
                    <tr>
                        <td><label>CURRENCY</label></td>
                        <td>
                            <html:input path="currencyName" id="currencyName" class="medium"/>
                            <html:input path="currencyId" id="currencyId" class="small readonly" readonly="true"/>
                        </td>
                        <td><label>EXCHANGE RATE</label></td>
                        <td><html:input path="exchangeRate" id="rate" type="text" class="medium rightAlign"/></td>
                    </tr>                    
                </table>
            </div> 

            <div class="rows">
                <iframe frameborder="0" style="position:absolute;width:1px;"></iframe>
                <table class="tablevou" id="tbl" >
                    <tr>
                        <td></td>
                        <td></textarea></td>
                        <td></td>                        
                        <td><label>VOUCHER TOTAL</label></td>
                        <td><html:input path="lineTotalAmount" class="smallplus rightAlign readonly" readonly="true"/></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <thead>
                        <tr>
                            <th></th>
                            <th style="width:32%">ACCOUNT</th>                            
                            <th>CHEQUE NO</th>
                            <th>CHEQUE DATE</th>
                            <th>AMOUNT</th>
                            <th>REMARKS</th>
                            <th></th>
                        </tr>
                    </thead>
                    <c:forEach items="${command.voucherRows}" var="voucherRow" varStatus="status">
                        <tr>
                            <td>
                                <html:select path="voucherRows[${status.index}].rowDrCr" cssClass="small">
                                    <html:option value="DR">DR</html:option>
                                </html:select>
                            </td>
                            <td>
                                <html:input path="voucherRows[${status.index}].rowAcct" value="${voucherRow.rowAcct}" cssClass="largeXL" onkeyup="lineAccounts(this,'${status.index}')"/>
                                <html:input path="voucherRows[${status.index}].rowAcctId" value="${voucherRow.rowAcctId}" cssClass="small readonly" id="lineAcctId" readonly="true"/>
                                <html:input path="voucherRows[${status.index}].rowParentId" value="${voucherRow.rowParentId}" cssClass="small readonly" id="lineParentId" readonly="true"/>
                            </td>
                            <td><html:input path="voucherRows[${status.index}].rowChq" value="${voucherRow.rowChq}" cssClass="smallplus"/></td>
                            <td><html:input path="voucherRows[${status.index}].rowChqDt" value="${voucherRow.rowChqDt}" cssClass="smallplus" onmousedown="chqDate(this)" onfocus="chqDate(this)"/></td>
                            <td><html:input path="voucherRows[${status.index}].rowAmt" value="${voucherRow.rowAmt}" cssClass="smallplus rightAlign rowAmount" onblur="calculate()"/></td>
                            <td><html:textarea path="voucherRows[${status.index}].rowRem" value="${voucherRow.rowRem}" cssClass="textareaL"/></td>
                            <td class="finErrMsg"><c:out value="${voucherRow.errMsg}"/></td>
                        </tr>
                    </c:forEach>
                        
                </table>
            </div>  
            <div class="comdivfoot">
                
                <table class="tablefooter">
                    <tr>
                        <td><input type="submit" id="submitButton" class="finbutton" value="Save"/></td>
                        <td><input type="button" class="finbutton" value="Print" onclick="printMe()"></td>
                        <td><input type="reset" class="finbutton" value="Reset"></td>
                    </tr>
                </table>
            </div> 
        </html:form>

    </body>
</html>
