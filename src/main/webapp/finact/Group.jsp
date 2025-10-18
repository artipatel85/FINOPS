<%-- 
    Document   : Group
    Created on : Nov 13, 2017, 4:43:17 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Group</title>
        <script>
            $(document).ready(function () {
                $("#Under1").autocomplete({
                    autoFocus: true,
                    source: function (request, response) {
                        $.getJSON("under.fin", {
                            term: request.term

                        }, function (result) {

                            var wordlist = ($.map(result, function (item) {
                                return {value: item.param1, data: item.param2, data1: item.param3}

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
                        $("#acctid").val(ui.item.data);
                        $("#acctType").val(ui.item.data1);
                    }
                });
            });
            $(document).ready(function () {
                $("#groupform").validate({

                    rules: {
                        acctName: {required: true},
                        parentId: {required: true}
                    },
                    messages: {
                        acctName: '<div class="tool">* field is required.</div>',
                        parentId: '<div class="tool">* field is required.</div>'
                    }

                });
            });
        </script>
    </head>
    <body>
        <header>
            <a href="groupSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
            Group
        </header>
        <div class="main">
           
            <html:form method="post" id="groupform" action="groupSave.fin" command="ledgerBean">
                <table class="tablec">
                    <tr>
                        <td><html:hidden path="prtCodeCombId" class="small readonly" readonly="true"/></td>
                    </tr>
                    <tr>
                        <td><label>GROUP NAME</label></td>
                        <td><html:input path="acctName" class="largeXL"/></td>
                    </tr>
                    <tr>
                        <td><label>UNDER</label></td>
                        <td><html:input path="parentName" id="Under1" class="largeXL"/>
                            <html:input path="parentId" id="acctid" class="small readonly" readonly="true"/>
                            <html:hidden path="acctTypeId" id="acctType" class="small readonly" readonly="true"/></td>
                    </tr>
                    <!--                    <tr>
                                            <td><label>NATURE OF A/C</label></td>
                                            <td><html:input path="acctTypeName" class="large readonly" readonly="true"/></td>
                                        </tr>-->
                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" id="btnsubmit" class="finbutton">Save</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton"><a href="groupSearch.fin" style="color: #FFF">Exit</a></button></td>
                    </tr> 
                </table>
            </html:form>
        </div>
    </body>
</html>
