var jlab = jlab || {};

jlab.mousePosition = {x: 0, y: 0};

$(document).on("mousemove", ".chart-placeholder", function (e) {
    jlab.mousePosition.x = e.clientX || e.pageX;
    jlab.mousePosition.y = e.clientY || e.pageY;
});

jlab.addPieTooltips = function () {
    $("<div id='tooltip'></div>").css({
        position: "absolute",
        display: "none",
        border: "1px solid #fdd",
        padding: "2px",
        "background-color": "#fee",
        opacity: 0.80
    }).appendTo("body");

    $(".chart-placeholder").bind("plothover", function (event, pos, item) {

        if (item) {
            var x = item.datapoint[0].toFixed(1) * 1,
                y = item.datapoint[1],
                label = item.series.label;

            $("#tooltip").html(label + " [" + jlab.integerWithCommas(y[0][1]) + "] (" + x + "%)")
                .css({top: jlab.mousePosition.y - 30, left: jlab.mousePosition.x + 5})
                .fadeIn(200);
        } else {
            $("#tooltip").stop().hide();
        }
    });
};

$(document).on("change", "#category-select", function () {
    var categoryId = $(this).val();
    jlab.srm.filterSystemListByCategory(categoryId);
});

$(document).on("click", ".default-clear-panel", function () {
    $("#destination-select").val(null).trigger('change');
    $("#category-select").val('').trigger('change');
    $("#system-select").val('');
    $("#region-select").val('');
    $("#group-select").val('');
    return false;
});

doPieChart = function () {
    var ds = [];
    var colors = ['green', 'yellow', 'red'];

    $(".chart-legend tbody tr.series").each(function (index, value) {

        $(this).find("th:first-child .color-box").css("background-color", colors[index]);

        var program = $("th:nth-child(2)", value).text().trim();
        program = program.substring(0, program.length - 1);
        var duration = parseFloat($("td:nth-child(3)", value).text().replace(/,/g, ''));

        ds.push({
            label: program,
            data: duration,
            color: colors[index]
        });
    });

    if ($(".chart-placeholder").length > 0) {

        $(".chart-wrap").addClass("chart-wrap-backdrop");

        jlab.flotplot = $.plot($(".chart-placeholder"), ds, {
            series: {
                pie: {
                    show: true,
                    label: {
                        show: false
                    }
                }
            },
            grid: {
                hoverable: true
            },
            legend: {
                show: false
            }
        });

        jlab.addPieTooltips();
    }
};

$(function () {
    doPieChart();

    $("#destination-select").select2({
        width: 390
    });

    $("#destination-select").closest(".li-value").css("visibility", "visible");
});
