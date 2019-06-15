<!DOCTYPE html>
<html lang="en-US">
<body>

<br/>
<h1>General Election Trends & Result 2019 (INDIA) </h1>

<div id="barChart"></div>
<br/>
<div id="table_div"></div>

<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>

<script type="text/javascript">
// Load google charts
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

// Draw the chart and set the chart values
function drawChart() {
  var barChartData = [
  ['Alliance', 'Leading', 'Won'],
  ['NDA', 263, 249],
  ['UPA', 157, 135],
  ['Others', 101, 87]
];
  var data = google.visualization.arrayToDataTable(barChartData);

  // Optional; add a title and set the width and height of the chart
  var options = {'title':'Trends', chartArea: {width: '60%'}, hAxis: {title: 'Number Of Seats (Won/Leading)', minValue: 0}, vAxis: {title: 'Alliance'}};

  // Display the chart inside the <div> element with id="piechart"
  var chart = new google.visualization.BarChart(document.getElementById('barChart'));
  chart.draw(data, options);
}

google.charts.load('current', {'packages':['table']});
      google.charts.setOnLoadCallback(drawTable);

      var tableData = [
          ['NDA', 263, 249],
          ['UPA', 157, 135],
          ['Others', 101, 87]
        ];
      function drawTable() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Alliance');
        data.addColumn('number', 'Leading');
        data.addColumn('number', 'Won');
        data.addRows(tableData);

        var table = new google.visualization.Table(document.getElementById('table_div'));

        table.draw(data, {showRowNumber: true, width: '100%', height: '100%'});
      }

</script>

</body>
</html>
