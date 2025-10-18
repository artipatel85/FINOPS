<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Digital Copy</title>

    </head>
    <script>
        $(document).ready(function () {
            $("#shipperName").autocomplete({
                autoFocus: true,
                source: function (request, response) {
                    $.getJSON("partnerList.fin", {
                        term: request.term

                    }, function (result) {

                        var wordlist = ($.map(result, function (item) {
                            return {value: item.param1, partner: item.param2}

                        }));

                        var re = $.ui.autocomplete.escapeRegex(request.term);
                        var matcher = new RegExp("^" + re, "i");
                        var a = $.grep(wordlist, function (item, index) {
                            return matcher.test(item.value);
                        });
                        response(a);
                        //alert(a);
                    });
                },
                select: function (event, ui) {
                    $("#shipper").val(ui.item.partner);
                }
            });

            $("#consigneeName").autocomplete({
                    autoFocus: true,
                    source: function (request, response) {
                        $.getJSON("partnerList.fin", {
                            term: request.term

                        }, function (result) {

                            var wordlist = ($.map(result, function (item) {
                                return {value: item.param1, partner: item.param2}

                            }));

                            var re = $.ui.autocomplete.escapeRegex(request.term);
                            var matcher = new RegExp("^" + re, "i");
                            var a = $.grep(wordlist, function (item, index) {
                                return matcher.test(item.value);
                            });
                            response(a);
                            //alert(a);
                        });
                    },
                    select: function (event, ui) {
                        $("#consignee").val(ui.item.partner);
                    }
            });

            $("#digitalForm").validate({
                rules: {
                    param11: {required: true},
                    param12: {required: true},
                    param1: {required: true},
                    param13: {required: true}
                },
                messages: {
                    param11: '<label class="tool">* Invalid value.</label>',
                    param12: '<label class="tool">* Invalid value.</label>',
                    param1: '<label class="tool">* Invalid value.</label>',
                    param13: '<label class="tool">* Field is required.</label>'
                }
            });
        });

            function soPopup(src) {
                $(src).autocomplete({
                    autoFocus: true,
                    source: function (request, response) {
                        $.getJSON('soPopup.fin', {
                            term: request.term,
                            seaAir: $("#param11").val(),
                            exportImport: $("#param12").val()
                        }, function (result) {
                            var wordlist = ($.map(result, function (item) {
                                return {value: item.param1, data: item.param2}
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
                            $("#blNo").val(ui.item.data);
                        }
                    }
                });
            }

            function fetch() {
                $('#digitalForm').attr("action", "fetchDigitalCopy.fin");
                $("#digitalForm").submit();
            }




    </script>
    <style>



    </style>
    <body>
        <div class="main">
            <header>
                Digital Copy
            </header>
            <html:form method="post" action="digitalCopyUpload.fin" id="digitalForm" commandName="reportBean" enctype="multipart/form-data">
                <table class="tablec">
                    <col width="35%">
                    <col width="60%">
                    <tr>
                        <td><label>SEA/AIR</label></td>
                        <td><html:select path="param11">
                                <html:option value="">SELECT</html:option>
                                <html:option value="SEA">SEA</html:option>
                                <html:option value="AIR">AIR</html:option>
                            </html:select></td>
                    </tr>
                    <tr>
                        <td><label>EXPORT/IMPORT</label></td>
                        <td><html:select path="param12">
                                <html:option value="">SELECT</html:option>
                                <html:option value="EXPORT">EXPORT</html:option>
                                <html:option value="IMPORT">IMPORT</html:option>
                            </html:select></td>
                    </tr>
                    <tr>
                        <td><label>Report Type</label></td>
                        <td><html:select id="rtype" path="param1">
                                <html:option value="">Select</html:option>
                                <html:option value="SO">SO</html:option>
                                <html:option value="BL">BL</html:option>
                                <html:option value="JOB">JOB</html:option>
                                <html:option value="INVOICE">INVOICE</html:option>
                                <html:option value="EXPENSE">EXPENSE</html:option>
                                <html:option value="JOURNAL">JOURNAL</html:option>
                            </html:select>
                        </td>
                    </tr>
                    <tr>
                        <td><label>Document No</label></td>
                        <td>
                            <html:input type="text" path="param13" id="partywise1" name="party" class="largeXL"/>
                        <button type="button" id="fetchButton" class="finbutton" onclick = "fetch()">Fetch</button></td>
                    </tr>
                    <tr>
                        <td><label>SO NUMBER</label></td>
                        <td>
                            <html:input type="text" path="param2" id="partywise1" name="party" class="largeXL" onkeyup="soPopup(this)"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>BL Number</label></td>
                        <td>
                            <html:input type="text" path="param3" name="party" class="largeXL" id="blNo"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>JOB Number</label></td>
                        <td>
                            <html:input type="text" path="param14" name="party" class="largeXL" id="blNo"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>SHIPPER / PARTY</label></td>
                        <td>
                            <html:input type="text" path="param4" id="shipperName"  class="largeXL"/><html:input type="text" path="param5" id="shipper" class="small"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>CONSIGNEE</label></td>
                        <td>
                            <html:input type="text" path="param6" id="consigneeName"  class="largeXL"/><html:input type="text" path="param7" id="consignee" class="small"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>CONTAINER NO</label></td>
                        <td>
                            <html:input type="text" path="param8" id="consigneeName"  class="largeXL"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="file1">Upload Json file</label>
                        </td>
                        <td>
                            <input type="file" name="param100" id="param100">
                        </td>
                    </tr>

                </table>
                <table class="tb2">
                    <c:if test="${reportBean.intparam1 == 1}">
                    <tr>
                        <td><button type="submit" id="uploadButton" class="finbutton">Upload</button></td>
                    </tr>
                    </c:if>
                </table>
            </html:form>
        </div>

    </body>
</html>
