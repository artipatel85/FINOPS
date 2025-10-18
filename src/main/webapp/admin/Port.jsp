
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/partner.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@include file="/finact/Bootstrap_jquery.jsp" %>
        <title>JSP Page</title>
        <script>
            $(document).ready(function () {
                $("#country1").autocomplete({
                    autoFocus: true,
                    source: function (request, response) {
                        $.getJSON("countrycode.fin", {
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
                        $("#code").val(ui.item.data);
                    }
                });
            });

            $(document).ready(function () {
                $("#portForm").validate({

                    rules: {
                        portCode: {required: true},
                        description: {required: true},
                        countryCode: {required: true}

                    },
                    messages: {
                        portCode: '<div class="tool">* field is required.</div>',
                        description: '<div class="tool">* field is required.</div>',
                        countryCode: '<div class="tool">* field is required.</div>'
                    }

                });
            });
        </script>
    </head>
    <body>
        <header>
            <a href="portSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
            PORT
        </header>
        <html:form method="post" id="portForm" action="portSave.fin" command="adminBean">
            <div class="comdiv">
                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="350PX">
                    <tr>
                        <td><label>PORT CODE</label></td>
                        <td><html:input path="portCode" class="medium"/><html:hidden path="action" class="small"/></td>
                        <td><label>PORT NAME</label></td>
                        <td><html:input path="description" class="largeXL"/></td>
                    </tr>
                    <tr>
                        <td><label>DESCRIPTION2</label></td>
                        <td><html:input path="description2" class="medium"/></td>
                        <td><label>COUNTRY NAME</label></td>
                        <td>
                            <html:input path="countryName" class="medium" onkeyup="partnerPopup('COUNTRY_0','#countryName', '#countryCode')"/>
                            <html:input path="countryCode" class="small" id="countryCode"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>STATUS</label></td>
                        <td><html:select path="status" class="select2 medium">
                                <html:option value="A">Active</html:option>
                                <html:option value="N">Suspend</html:option>
                            </html:select> </td>
                        <td><label>SHOW ON SCREEN</label></td>
                        <td><html:checkbox path="display" value="Y"/></td>
                    </tr>
                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" id="btnsubmit" class="finbutton">Save</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton"><a href="portSearch.fin" style="color: #FFF">Exit</a></button></td>
                    </tr> 
                </table>
            </html:form>
        </div>
    </body>
</html>
