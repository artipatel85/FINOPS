<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<html>
    <head>
        <title>Home</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    </head>

    <body style="background-color:#dbdada">

        <header>
            Welcome to Shikhar Logistics Pvt Ltd. - ADMIN
        </header>
        <table width="100%">
            <tr>
                <td width="50%">
                    <canvas id="myChart"></canvas>
                </td>
                <td>
                    <canvas id="myChart2"></canvas>
                </td>
            </tr>

        </table>

        <!-- https://gist.github.com/msdzero/d663077bf3fa9d35d133b5dc49942373 -->
        <script language="javascript">
                    var ctx = document.getElementById('myChart').getContext('2d');
                    var ctx2 = document.getElementById('myChart2').getContext('2d');

                    var data = {
                       datasets: [{
                           label: 'Monthly Invoices',
                           backgroundColor: 'rgb(255, 99, 132)',
                           borderColor: 'rgb(255, 99, 132)',
                           pointBorderColor: "rgb(75,192,192)",
                           fill: false,
                       }]

                    };

                    var data2 = {
                       datasets: [{
                           label: 'Monthly Expenses',
                           backgroundColor: 'rgb(255, 99, 132)',
                           borderColor: 'rgb(255, 99, 132)',
                           pointBorderColor: "rgb(75,192,192)",
                           fill: false,
                       }]

                    };

                    // Configuration options

                    var options = {
                       scales: {
                           yAxes: [{
                               ticks: {
                                   beginAtZero:true
                               }
                           }]
                       }
                    };

                    // Creating the chart

                    var myChart = new Chart(ctx, {
                       type: 'bar',
                       data: data,
                       options: options
                    });

                    var myChart2 = new Chart(ctx2, {
                       type: 'bar',
                       data: data2,
                       options: options
                    });

                    $(document).ready(function () {
                        $.ajax({
                            type : "POST",
                            dataType: "json",
                            url : "dashboard.fin?type=INVOICE",
                            contentType: "application/json; charset=utf-8",
                            data : data,
                            success : function(response) {
                                myChart.data.labels = response.labels;
                                myChart.data.datasets[0].data = response.data; // or you can iterate for multiple datasets
                                myChart.update();
                            },
                            error : function() {
                                alert("not working");
                            }
                        });
                    });


                    $(document).ready(function () {
                        $.ajax({
                            type : "POST",
                            dataType: "json",
                            url : "dashboard.fin?type=EXPENSE",
                            contentType: "application/json; charset=utf-8",
                            data : data,
                            success : function(response) {
                                myChart2.data.labels = response.labels;
                                myChart2.data.datasets[0].data = response.data; // or you can iterate for multiple datasets
                                myChart2.update();
                            },
                            error : function() {
                                alert("not working");
                            }
                        });
                    });


                </script>
    </body>

</html>