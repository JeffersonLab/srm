var jlab = jlab || {};

$(document).on("change", "#category-select", function () {
    var categoryId = $(this).val();
    jlab.srm.filterSystemListByCategory(categoryId);
});

$(document).on("click", ".default-clear-panel", function () {
    $("#destination-select").val(null).trigger('change');
    $("#category-select").val('').trigger('change');
    $("#system-select").val('');
    $("#region-select").val('');
    $("#chart").val('table');
    return false;
});

jlab.srm.doBarChart = function () {
    var d1 = [];
    var d2 = [];
    var d3 = [];
    var ms_ticks = [];
    var goals = [];
    var colors = ["green", "yellow", "red"];

    var groupCount = $("#bar-chart-data-table tbody tr").length;

    $("#bar-chart-data-table tbody tr").each(function (index, value) {
        index = groupCount - 1 - index;
        ms_ticks.push([index, $("td:nth-child(1)", value).text()]);
        d1.push([parseInt($("td:nth-child(3)", value).text().replace(/,/g, ''), 10), index]);
        d2.push([parseInt($("td:nth-child(4)", value).text().replace(/,/g, ''), 10), index]);
        d3.push([parseInt($("td:nth-child(5)", value).text().replace(/,/g, ''), 10), index]);
        goals.push(parseInt($("td:nth-child(9)", value).text().replace(/%/g, ''), 10));
    });

    var ds = [];

    ds.push({
        color: colors[0],
        label: "Ready",
        data: d1
    });
    ds.push({
        color: colors[1],
        label: "Checked",
        data: d2
    });
    ds.push({
        color: colors[2],
        label: "Not Ready",
        data: d3
    });

    $.plot($(".chart-placeholder"), ds, {
        series: {
            stackpercent: true,
            bars: {
                horizontal: true,
                show: true,
                barWidth: 0.5,
                align: 'center',
                fillColor: {
                    colors: [{opacity: 1}, {opacity: 0.5}]
                }
            }
        },
        tooltip: true,
        tooltipOpts: {
            content: "%x %s (%p.1%)"
        },
        grid: {
            hoverable: true,
            borderColor: 'gray',
            backgroundColor: {colors: ["#fff", "#eee"]}
        },
        legend: {
            show: false
        },
        yaxis: {
            ticks: ms_ticks,
            font: {
                size: 16,
                color: 'black'
            }
        },
        xaxis: {
            min: 0,
            max: 100
        }
    });
};

$(function () {

    if ($(".chart-placeholder").length > 0) {
        jlab.srm.doBarChart();
    }

    $(".date-field").datepicker({
        dateFormat: 'yy-mm-dd'
    });

    $('.sparkline').sparkline('html', {
        type: 'pie',
        tagValuesAttribute: 'data-graph',
        sliceColors: ['green', 'yellow', 'red']
    });

    $("#destination-select").select2({
        width: 390
    });

    $("#destination-select").closest(".li-value").css("visibility", "visible");
});