
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/finance.js"></script>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="<c:url value="/finactcss/rowForm.css"/>" rel="stylesheet">
        <title>JournalVoucher</title>

        <script lang="javascript">
            function removeRow(src)
            {
                var oRow = src.parentElement.parentElement;
                $('#voucherForm').attr('action', "removeJournalRow.fin?index=" + (oRow.rowIndex - 1)).submit();
            }
            function printMe()
            {   
                if (confirm("DO U Wish To Print The Record??"))
                {
                    window.open("voucherPDFReport.fin?voucherType=JOURNAL&trxId=${voucherBean.jeHdrId}");
                }
            }
            
            function printReimbursement()
            {   
                if (confirm("DO U Wish To Print The Record??"))
                {
                    window.open("reimbursementPDFReport.fin?jeHdrId=${voucherBean.jeHdrId}");
                }
            }
            $(document).ready(currency);
            
            function ShowLoading(e) {
                calculate();
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
                var debitSum = 0;
                var creditSum = 0;
                $('.rowDebitAmount').each(function() {
                    debitSum += Number($(this).val());
                });
                $('.rowCreditAmount').each(function() {
                    creditSum += Number($(this).val());
                });
                $('input[name="lineTotalAmount"]').val(creditSum.toFixed(2));
                $('input[name="hdrTotalAmount"]').val(debitSum.toFixed(2));
            }

            function addRow()
            {
                $('#voucherForm').attr('action', "addJournalRow.fin").submit();
            }

            function soPopup(src, idx) {
                $(src).autocomplete({
                    autoFocus: true,
                    source: function (request, response) {
                        $.getJSON('soExpensePopup.fin', {
                            term: request.term,
                            blNo: '',
                            seaAir: $("#seaAir").val(),
                            exportImport: $("#expImp").val()
                        }, function (result) {
                            var wordlist = ($.map(result, function (item) {
                                return {value: item.param1, data: item.param5}
                            }));
                            var re = $.ui.autocomplete.escapeRegex(request.term);
                            var matcher = new RegExp("^" + re, "i");
                            var a = $.grep(wordlist, function (item) {
                                return matcher.test(item.value);
                            });
                            response(a);
                        });
                    },
                    select: function (event, ui) {
                        if(ui.item.data !== undefined){
                            $('input[id="blNo"]').get(idx).value = ui.item.data;
                        }
                    }
                });
            }
            
            function openSOPopup(src, idx) {
                var soNumber = $('input[id="soNumber"]').get(idx).value;
                window.open(encodeURI('soPopupExpense.fin?soNumber='+soNumber));
            }
            
            $(function () {
                $("#date11").datepicker({
                    dateFormat: 'yy-mm-dd',
                    defaultDate: new Date()
                });
                $("#date11").datepicker('setDate', new Date());
            });

            $(function () {
                $("#invDate").datepicker({
                    dateFormat: 'yy-mm-dd'
                });
            });
            
            $(document).ready(function () {

                
                $("#voucherForm").validate({
                        rules: {
                            currencyName: {required: true,minlength:3},
                            currencyId: {min: 1},
                            exchangeRate:{required:true},
                            hdrTotalAmount:{required: true},
                            invDate: {
                                required: function () {
                                    if ($("#invNo").val() != '') {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            }
                        },
                        messages: {
                            currencyName: {
                                required:'<label class="tool">* field is required.</label>',
                                minlength:'<label class="tool">* field value is invalid.</label>'
                            },
                            currencyId: '<label class="tool">* Invalid value 0.</label>',
                            exchangeRate: '<label class="tool">* field is required.</label>',
                            hdrTotalAmount: {
                                required:'<label class="tool">* field is required.</label>'
                            },
                            invDate: '<label class="tool">* field is required</label>'
                        }                    
                    });
                });
                
            function setNarration(){
                    var narration = '';
                    $('input[id="soNumber"]').each(function() {
                        if(this.value !== undefined && this.value !== ''){
                            narration = narration + this.value + ',';
                        }                            
                    });
                    $('#remarks').val(narration);
                }
        </script>
    </head>
    <body>
        <div class="comdiv">
            <html:form autocomplete="off" method="post" action="saveJournalVoucher.fin" id="voucherForm" onSubmit="ShowLoading()">
                <header>
                    JOURNAL VOUCHER
                </header>
                <table class="tablec" width="100%">
                    <tr>
                        <th colspan="4"><c:out value="${voucherBean.errMsg}"/></th>
                    </tr>
                    <tr>
                        <td width="10%"><label>JOURNAL NO</label></td>
                        <td width="50%">
                            <html:input path="voucherNo" class="medium" readonly="true"/>
                            <html:hidden path="jeHdrId" />
                            <html:hidden path="voucherType" value="JOURNAL"/>
                            <html:hidden path="seqNo"/>
                        </td>
                        <td width="10%"><label>DATE</label></td>
                        <td><html:input path="jeDate" id="date" class="medium rightAlign"/></td>
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
                    <tr>
                        <td><label>SEA/AIR</label></td>
                        <td><html:select path="seaAir">
                                <html:option value="">SELECT</html:option>
                                <html:option value="SEA">SEA</html:option>
                                <html:option value="AIR">AIR</html:option>
                            </html:select></td>
                        <td><label>EXPORT/IMPORT</label></td>    
                        <td><html:select path="expImp">
                                <html:option value="">SELECT</html:option>
                                <html:option value="EXPORT">EXPORT</html:option>
                                <html:option value="IMPORT">IMPORT</html:option>
                            </html:select></td> 
                    </tr>
                    <tr>
                        <td><label>INV NO & DATE </label></td>
                        <td>
                            <html:input path="invNo" id="invNo" class="medium"/>
                            <html:input path="invDate" id="invDate" class="medium rightAlign"/>
                        </td>
                        <td><label>REMARKS</label></td>
                        <td><html:textarea path="remarks" id="remarks" cssClass="textareaL"/></td>
                    </tr>
                </table>
            </div> 
            <div>
                <table class="tablevou" id="tbl5" >
                    <thead>
                        <tr>
                            <th><a href="#" onclick="addRow();"><div class="logoplus"></div></a></th>
                            <th></th>
                            <th>ACCOUNT</th>
                            <th>SO</th>
                            <th>BL</th>
                            <th>SAC</th>
                            <th>DEBIT</th>
                            <th>CREDIT</th>
                            <th></th>
                        </tr>
                    </thead>

                    <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>

                        <td></td>
                        <td><label>VOUCHER TOTAL</label></td>
                        <td><html:input path="hdrTotalAmount" class="smallplus rightAlign readonly" readonly="true"/></td>
                        <td><html:input path="lineTotalAmount" class="smallplus rightAlign readonly" readonly="true"/></td>
                        <td></td>
                    </tr>
                    
                    <c:forEach items="${command.voucherRows}" var="voucherRow" varStatus="status">
                        <tr>
                            <td><a href="#" onclick="removeRow(this);"><div class="logominus"></div></a></td>
                            <td>
                                <html:select path="voucherRows[${status.index}].rowDrCr" cssClass="small">
                                    <html:option value="DR">DR</html:option>
                                    <html:option value="CR">CR</html:option>
                                </html:select>
                            </td>
                            <td>
                                <html:input path="voucherRows[${status.index}].rowAcct" value="${voucherRow.rowAcct}" cssClass="largeXL" onkeyup="lineAccounts(this,'${status.index}')"/>
                                <html:input path="voucherRows[${status.index}].rowAcctId" value="${voucherRow.rowAcctId}" cssClass="small readonly" id="lineAcctId" readonly="true"/>
                                <html:input path="voucherRows[${status.index}].rowParentId" value="${voucherRow.rowParentId}" cssClass="small readonly" id="lineParentId" readonly="true"/>
                            </td>
                            <td>
                                <html:input path="voucherRows[${status.index}].so" value="${voucherRow.so}" cssClass="smallplus" onblur="setNarration()" onkeyup="soPopup(this,'${status.index}')" id="soNumber"/>
                            </td>
                            <td><html:input path="voucherRows[${status.index}].bl" value="${voucherRow.bl}" cssClass="medium readonly" id="blNo" readonly="true"/></td>
                            <td><html:input path="voucherRows[${status.index}].sac" value="${voucherRow.sac}" cssClass="smallplus"/></td>
                            <td><html:input path="voucherRows[${status.index}].debit" value="${voucherRow.debit}" cssClass="smallplus rowDebitAmount rightAlign" onblur="calculate()"/></td>
                            <td><html:input path="voucherRows[${status.index}].credit" value="${voucherRow.credit}" cssClass="smallplus rowCreditAmount rightAlign" onblur="calculate()"/></td>
                            <td class="finErrMsg"><c:out value="${voucherRow.errMsg}"/></td>
                        </tr>
                    </c:forEach>

                </table>
            </div>  
            <div class="comdivfoot">

                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" class="finbutton">SAVE</button></td>
                        <td><input type="button" class="finbutton" value="Print" onclick="printMe()"></td>
                        <td><input type="button" class="finbutton" value="Reimbursement" onclick="printReimbursement()"></td>
                        <td><button type="submit" class="finbutton">RESET</button></td>
                    </tr>
                </table>
            </div>   
        </html:form>
    </body>
</html>
