<%-- 
    Document   : Salesmanwise
    Created on : Aug 28, 2017, 2:35:48 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Salesmanwise</title>
        <script>
            $(document).ready(function () {
                $("#salesman1").autocomplete({
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
                    },
                    search: function (e, u) {
                        $(this).addClass('loader');
                    },
                    response: function (e, u) {
                        $(this).removeClass('loader');
                    }
                });
            });


        </script>
    </head>
    <body>
        <div class="main">

            <html:form method="post" action="saleswiseform" id="registration-form" commandName="reportbean">
                <table class="tablec">
                    <col width="40%">
                    <col width="50%">

                    <tr>
                        <td><label>Salesman</label></td>
                        <td><html:input type="text" path="param1" id="salesman1" class="largeXL"/></td>

                    </tr>
                    <tr>
                        <td><label>As on date</label></td>
                        <td><html:input type="text" path="param2" id="date2" class="medium" data-date-format='yy-mm-dd'/></td>
                    </tr>
                     <tr>
                        <td><label>Branch</label></td>
                        <td>
                    <html:select path="param6">
                        <html:option value="">SELECT</html:option>
                        <html:option value="SFP">Delhi </html:option>
                        <html:option value="SFPM">Mumbai</html:option>
                        <html:option value="SFPC">Chennai  </html:option>
                        <html:option value="SFPK">Kochi</html:option>
                        <html:option value="SFPG">Gandhidham</html:option>
                    </html:select>
                    </td>  
                    </tr>
                    <tr>
                        <td><label>Bill Type</label></td>
                        <td>
                    <html:radiobutton path="param3" class="rd1" value="DEBTOR" checked="checked"/>DEBTOR
                    <html:radiobutton path="param3" class="rd4" value="CREDITOR"/>CREDITOR
                    </td>
                    </tr>
                    <tr>
                        <td><label>LOCAL / FOREIGN</label></td>
                        <td>
                    <html:radiobutton path="param4" class="rd2" value="LOCAL" checked="checked"/>LOCAL
                    <html:radiobutton path="param4" class="rd2" value="FOREIGN"/>FOREIGN
                    </td>
                    </tr>
                    <tr>
                        <td><label>Format</label></td>
                        <td><html:radiobutton path="param18" class="rd3" value="pdf" checked="checked"/>PDF
                            <html:radiobutton path="param18" class="rd3" value="xls"/>XLS</td>
                    </tr>
                    <TR>
                        <TD colspan="2">
                            <hr class="hr">
                        </TD>
                    </TR>
                    <TR>
                        <TD><label>Ageing Bands</label></TD>                                
                    </TR>
                    <tr>
                        <td><html:input type="text" path="param8" class="small readonly" value="0" readonly="true"/></td>
                    <td><html:input type="text" path="param9" class="small readonly" value="30" readonly="true"/></td>
                    </tr>
                    <tr>
                        <td><html:input type="text" path="param10" class="small readonly" value="31" readonly="true"/></td>
                    <td><html:input type="text" path="param11" class="small readonly" value="60" readonly="true"/></td>
                    </tr>
                    <tr>
                        <td><html:input type="text" path="param12" class="small readonly" value="61" readonly="true"/></td>
                    <td><html:input type="text" path="param13" class="small readonly" value="90" readonly="true"/></td>
                    </tr>
                    <tr>
                        <td><html:input type="text" path="param13" class="small readonly" value="91" readonly="true"/></td>
                    <td><html:input type="text" path="param14" class="small readonly" value="180" readonly="true"/></td>
                    </tr>
                    <tr>
                        <td><html:input type="text" path="param15" class="small readonly" value="181" readonly="true"/></td>
                    <td><html:input type="text" path="param16" class="small readonly" value="999" readonly="true"/></td>
                    </tr>

                </table>
                <table class="tb2">
                    <tr>
                        <td><button type="submit" class="finbutton">View</button></td>
                        <td><button type="submit" class="finbutton">Summary</button></td>
                        <td><button type="submit" class="finbutton">Detail</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                    </tr>
                </table>

            </html:form>
        </div>
    </body>
</html>

