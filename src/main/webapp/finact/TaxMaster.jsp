<%-- 
    Document   : TaxMaster
    Created on : Nov 13, 2017, 4:45:40 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>TaxMaster</title>
        <script>
             $(document).ready(function () {
            $("#acctName1").autocomplete({
                autoFocus: true,
                source: function (request, response) {
                    $.getJSON("taxHead.fin", {
                        term: request.term

                    }, function (result) {

                        var wordlist = ($.map(result, function (item) {
                            return {value: item.param1, data: item.param2}

                        }));
                        var re = $.ui.autocomplete.escapeRegex(request.term);
                        var matcher = new RegExp("^" + re, "i");
                        var a = $.grep(wordlist, function (item, index) {
                            return matcher.test(item.value);
                        });
                        response(a);
                        //response($.ui.autocomplete.filter(wordlist, request.term));
                    });
                },
                select: function (event, ui) {
                    $("#codeid").val(ui.item.data);
                }
            });
        });
        $(document).ready(function () {
                $("#groupform").validate({

                    rules: {
                        description: {required: true},
                        percentage: {required: true,min:0.01},
                        prtCodeCombId: {required: true,minlength:2}
                    },
                    messages: {
                        description: '<div class="tool">* field is required.</div>',
                        percentage: '<div class="tool">* field is required.</div>',
                        prtCodeCombId: '<div class="tool">* field is required.</div>'
                    }

                });
            });
             $(document).ready(function () {
            $(".required").keypress(function (event) {
                if (event.which !== 46 && (event.which < 48 || event.which > 57))
                {
                    event.preventDefault();
                }
            });
        });
        </script>
    </head>
    <body>
        <header>
            <a href="taxMasterSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
            Group
        </header>
        <div class="main">
            <html:form method="post" id="groupform" action="taxMasterSave.fin" command="ledgerBean">
                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="350PX">
                    <tr>
                        <td><label>DESCRIPTION</label></td>
                        <td><html:input path="description" class="largeXL"/>
                        <html:hidden path="taxid" class="small"/></td>
                        <td><label>PERCENTAGE</label></td>
                        <td><html:input path="percentage" class="medium required"/></td>
                    </tr>
                    <tr>
                        <td><label>SPLIT?</label></td>
                        <td><html:checkbox path="isPartition" value="Y"/></td>
                        <td><label>REMARKS</label></td>
                        <td><html:textarea path="remarks" rows="3" cols="30"/></td>
                    </tr>
                    <tr>
                        <td><label>SUB HEAD1</label></td>
                        <td><html:input path="sub1Desc" class="medium"/></td>
                        <td><label>SUB HEAD1 PER(%)</label></td>
                        <td><html:input path="sub1Per" class="medium required"/></td>
                    </tr>
                    <tr>
                        <td><label>SUB HEAD2</label></td>
                        <td><html:input path="sub2Desc" class="medium"/></td>
                        <td><label>SUB HEAD2 PER(%)</label></td>
                        <td><html:input path="sub2Per" class="medium required"/></td>
                    </tr>
                    <tr>
                        <td><label>SUB HEAD3</label></td>
                        <td><html:input path="sub3Desc" class="medium"/></td>
                        <td><label>SUB HEAD3 PER(%)</label></td>
                        <td><html:input path="sub3Per" class="medium required"/></td>
                    </tr>
                    <tr>
                        <td><label>TAX LEDGER HEAD</label></td>
                        <td><html:input path="taxHead" class="largeXL" id="acctName1"/>
                         <html:input path="prtCodeCombId" id="codeid" class="small readonly" readonly="true"/></td>
                        <td><label>DEFAULT PERCENTAGE</label></td>
                        <td><html:input path="defaultPercentage" id="Under1" class="largeXL required"/></td>
                    </tr>

                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" id="btnsubmit" class="finbutton">Save</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton"><a href="taxMasterSearch.fin" style="color: #FFF">Exit</a></button></td>
                    </tr> 
                </table>
            </html:form>
        </div>
    </body>
</html>
