<%-- 
    Document   : ImportPartnerAccount
    Created on : Sep 11, 2017, 2:53:13 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ImportPartnerAccount</title>
        <style>
            .ui-widget-header {
                border: 1px solid #dddddd;
                background: #23527c;
                color: white;
                font-weight: bold;
                height: 20px;
            }   
        </style>
        <script>
            $(document).ready(function () {
                $("#state1").autocomplete({
                    autoFocus: true,
                    source: function (request, response) {
                        $.getJSON("statecode.fin", {
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

                        });
                    },
                    select: function (event, ui) {

                        $("#scode").val(ui.item.data);
                    }
                });
            });
            $(document).ready(function () {
                $("#salesman2").autocomplete({
                    autoFocus: true,
                    source: function (request, response) {

                        $.getJSON("salesman.fin", {
                            term: request.term

                        }, function (result) {
                            //alert();
                            var wordlist = ($.map(result, function (item) {
                                //alert(item.param2);
                                return {value: item.param1, data: item.param2}
                            }));
                            var re = $.ui.autocomplete.escapeRegex(request.term);
                            var matcher = new RegExp("^" + re, "i");
                            var a = $.grep(wordlist, function (item, index) {
                                return matcher.test(item.value);
                            });
                            response(a);
                        });
                    },
                    select: function (event, ui) {

                        $("#salecode2").val(ui.item.data);
                    }
                });
            });


        </script>
    </head>
    <body>
        <div class="main">
            <html:form method="post" id="addAccount">
                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="300PX">
                    <td colspan="3">
                        <input type="checkbox" path="shipper">
                        <label>Shipper</label>
                        <input type="checkbox" path="consignee">
                        <label>Consignee</label>
                        <input type="checkbox" path="coLoader">
                        <label>Co-Loader</label>
                        <input type="checkbox" path="carrier">
                        <label>Carrier</label>
                        <input type="checkbox" path="agent">
                        <label>Agent</label>

                    </td>
                    <tr>
                        <td><label>Address</label></td>
                        <td><html:textarea  path="address1" rows="4" cols="30"></html:textarea></td>
                            <td><label>City</label></td>
                            <td><html:input path="city" class="medium"/></td>    
                    </tr>
                    <tr>                        
                        <td><label>State</label></td>
                        <td><html:input path="zipCode" class="medium"/></td>
                        <td><label>Zip Code</label></td>
                        <td><html:input path="zipCode" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>Tel No</label></td>
                        <td><html:input path="telCc" class="small"/>
                            <html:input path="telAc" class="small"/>
                            <html:input path="telNo" class="medium"/></td>
                        <td><label>Fax No</label></td>
                        <td><html:input path="faxNo" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>CT.Person</label></td>
                        <td><html:input path="contactPerson" class="medium"/></td>
                        <td><label>Email Id</label></td>
                        <td><html:input path="email" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>Salesman</label></td>
                        <td><html:input path="salesManName" class="large" id="salesman2"/>
                            <html:input path="salesManCode" class="smallplus readonly" id="salecode2" readonly="true"/>
                        </td>
                        <td><label>GSTIN NO</label></td>
                        <td><html:input path="gstinNo" class="large"/></td>
                    </tr>

                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" class="finbutton">UPDATE</button></td>
                        <td><button type="submit" class="finbutton">RESET</button></td>
                    </tr>
                </table>
            </html:form>
        </div>          
    </body>
</html>
