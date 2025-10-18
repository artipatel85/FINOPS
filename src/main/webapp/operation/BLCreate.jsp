
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<script src="finactjs/so.js"></script>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>BL CREATE</title>

    </head>
     <script>
        $(document).ready(shipper);
        $(document).ready(shipperName);
        $(document).ready(consignee);
        $(document).ready(consigneeName);
        $(document).ready(pol);
        $(document).ready(pod);
        $(document).ready(dest);
        $(document).ready(polName);

        $(document).ready(function () {
            var table = $('#example').DataTable({
                ajax: "blSoSearchGSON.fin?param=${blBean.expImp}",
                "serverSide": true,
                "bAutoWidth": false,
                "bJQueryUI": true,
                "bProcessing": true,
                "lengthMenu": [20, 25, 50],
                "dom": '<"top">',
                "aoColumns": [
                    {
                        "mData": "bkgRefNo",
                        "render": function (mData, full, row) {
                            return '<input type="checkbox" name="ids" value="' + mData + '"/>';
                        }
                    },
                    {
                        "mData": "bkgRefNo",
                        "render": function (mData, full, row) {
                            return '<a href="soRetrieve.fin?bookingRefNumber=' + mData + '">'+mData+'</a>';
                        }
                    },
                    {
                        "mData": "soNumber",
                        "render": function (mData, full, row) {
                            var pod = row['pod'];
                            var la = row['loadingAgentCode'];
                            return la + '/' + pod + '/' + mData;
                        }
                    },
                    {"mData": "shipperName"},
                    {"mData": "consigneeName"},
                    {"mData": "pol"},
                    {"mData": "pod"},


                ]
            });
        });

        function next() {
            $('#blCreateForm').attr("action", "generateBL.fin?param=${blBean.expImp}");
            $("#blCreateForm").submit();
        }

    </script>
    <body>
        <header>
            BL CREATE - ${blBean.expImp}
        </header>
        <div class="main">
            <html:form method="post" id="blCreateForm" action="blSoSearch.fin" command="blBean">
                <table class="tablevou">
                    <tr>
                        <td><label>SHPR</label></td>
                        <td>
                            <html:input path="shipperName" class="large" id="shipperName"/>
                            <html:hidden path="expImp"/>
                        </td>
                        <td>
                            <html:input path="shipper" class="small" id="shipper"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>CNEE</label></td>
                        <td>
                            <html:input path="consigneeName" class="large" id="consigneeName"/>
                        </td>
                        <td>
                            <html:input path="consignee" class="small" id="consignee"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>POL</label></td>
                        <td>
                            <html:input path="polName" class="large" id="polName"/>
                        </td>
                        <td>
                            <html:input path="pol" class="small" id="pol"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>POD</label></td>
                        <td>
                            <html:input path="podName" class="large" id="podName"/>
                        </td>
                        <td>
                            <html:input path="pod" class="small" id="pod"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>DEST</label></td>
                        <td>
                            <html:input path="destName" class="large" id="destName"/>
                        </td>
                        <td>
                            <html:input path="dest" class="small" id="dest"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>BKG TYPE</label></td>
                        <td>
                            <html:select path="bkgType" cssClass="smallplus">
                                <html:option value=""/>
                                <html:option value="L">LCL</html:option>
                                <html:option value="F">FCL</html:option>
                            </html:select>
                        </td>
                    </tr>
                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" id="btnsubmit" class="finbutton">Search</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton">Exit</button></td>
                    </tr>
                    <c:if test="${blBean.errMsg != null}">
                        <tr>
                            <th colspan="6"><c:out value="${blBean.errMsg}"/></th>
                        </tr>
                    </c:if>
                </table>

                <table id="example" class="display" cellspacing="0" width="100%">
                            <thead>
                                <tr>
                                    <th></th>
                                    <th>B/R NO</th>
                                    <th>S/O NO</th>
                                    <th width="200px">SHPR</th>
                                    <th width="200px">CNEE</th>
                                    <th>POL</th>
                                    <th>POD</th>
                                </tr>
                            </thead>

                        </table>
                        <table class="tablefooter">
                            <tr>
                                <td><button type="button" id="btnsubmit" class="finbutton" onclick="next()">NEXT</button></td>
                            </tr>
                        </table>
            </html:form>
        </div>


    </body>
</html>
