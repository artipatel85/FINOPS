
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form method="post" command="trackingBean" id="soTrackingForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>SO Tracking</title>
            <script type="text/javascript">
                function getMe() {
                    var internal = '[';
                    var arr = '${trackingBean.headers}'.split(',');
                    var len = ${trackingBean.columnLength};
                    for (i = 1; i <= len; i++) {
                        internal += '{"mData": "column' + i + '"}';
                        if (i < len) {
                            internal += ',';
                        }
                    }
                    return JSON.parse(internal + ']');
                }

                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "seaExportTrackingGson.fin?param=1029",
                        "serverSide": true,
                        "bJQueryUI": true,
                        "bRetrieve": true,
                        "bProcessing": true,
                        "dom": 'l<"top">pt',
                        "lengthMenu": [20, 30, 50],
                        "aoColumns": getMe()
                    });
                    //table.columns([${trackingBean.hiddenColumns}]).visible(false);
                    $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" id="btnnew" class="finbutton" value="Excel" onClick="excel()"/>' +
                            '<html:hidden path="formId"/>&nbsp;&nbsp;');

                    table.buttons().container()
                            .insertBefore('#example_filter');
                    $('.filter').on('keyup change', function () {
                        table.search('');
                        table.column($(this).data('columnIndex')).search(this.value).draw();
                    });
                    $(".dataTables_filter input").on('keyup change', function () {
                        table.columns().search('');
                        $('.filter').val('');
                    });
                });
                function excel() {
                    $('#soTrackingForm').attr("action", "seaExportTrackingXLSReport.fin");
                    $("#soTrackingForm").submit();
                }
                
                </script> 
            </head>
            <body>
                <header>
                    ETracking - SO
                </header>
                <table id="example" class="display" cellspacing="0" width="100%">
                    <thead>
                    <tr>
                        <c:forEach items="${trackingBean.headerData}" varStatus="status" var="trackingVo">

                            <th width="${trackingVo.column3}">${trackingVo.column2}</th>

                        </c:forEach>
                        </tr>
                    </thead>
        </table>
    </body>
</html:form>
</html>
