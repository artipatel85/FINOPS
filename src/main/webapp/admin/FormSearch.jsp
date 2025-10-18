<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id="formForm" action="form.fin">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <title>Form Search</title>
        <script>
                $(document).ready(function () {
                    var table = $('#countryview').DataTable({
                        ajax: "formGson.fin",
                        "bJQueryUI": true,
                        deferRender: true,
                        "pagingType": "full_numbers",
                        "bRetrieve": true,
                        "bFilter": true,
                        "bProcessing": true,
                        "dom": 'l<"top">pt',
                        "lengthMenu": [10, 20, 50],
                        "aoColumns": [
                            {"mData":"formId"},
                            {"mData": "name",
                                "render": function (mData, full, row) {
                                     var link = row['formId'];
                                     
                                    return '<a href="retrieveForm.fin?code=' + link + '">' + mData + '</a>';
                                }

                            },
                            {"mData": "status"}
                        ]
                    });

                    $("div.top").append('' +
                            '&nbsp;&nbsp;<input type="button" id="btnnew" class="finbutton" value="NEW"/>' +
                            '&nbsp;&nbsp;<input type="submit" class="finbutton" value="DELETE"/>' +
                            '&nbsp;&nbsp;');

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
                $(function () {
                    $("#btnnew").on('click', function () {
                        $('#formForm').attr("action", "form.fin");
                        $("#formForm").submit();
                    });
                });

            </script>
    </head>
    <body>
            <header>
                FORM SEARCH
            </header>
            <table id="countryview" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>FORM ID</th>
                        <th>FORM NAME</th>
                        <th>STATUS</th>
                    </tr>
                </thead>
                 <thead>
                    <tr>
                        <td><input type='text' value='' class='filter midtext' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                    </tr>
                </thead>
            </table>
    </body>
    </html:form>
</html>
