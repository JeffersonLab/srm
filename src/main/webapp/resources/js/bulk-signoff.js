var jlab = jlab || {};
jlab.hco = jlab.hco || {};
jlab.hco.bulkSignoff = function (systemsArray, statusId, comment) {
    $("#filter-form").hide();
    $("#required-span").text("");

    var $table = $('<table class="data-table"><thead><tr><th>Subsystem</th><th>Progress</th></tr></thead><tbody></tbody></table>'),
        $div = $("#bulk-div");

    $div.append($table);

    $.each(systemsArray, function (index, $obj) {
        var tr = $('<tr><td data-id="' + $obj.val() + '">' + $obj.text() + '</td><td>Queued</td></tr>');
        $div.find("tbody").append(tr);
    });

    var rows = $div.find("tbody tr").toArray();

    jlab.hco.bulkRequest(rows, statusId, comment);
};
jlab.hco.bulkRequest = function (rows, statusId, comment) {
    if (rows.length > 0) {
        var $tr = $(rows.shift()),
            $td1 = $tr.find("td:first-child"),
            systemId = $td1.attr("data-id"),
            $td2 = $tr.find("td:last-child");

        $td2.text("Working...");

        var url = jlab.contextPath + "/setup/ajax/bulk-signoff",
            data = {systemId: systemId, statusId: statusId, comment: comment};

        var promise = jlab.doAjaxJsonPostRequest(url, data, null, false);

        promise.done(function () {
            $td2.text('Done');
        });

        promise.fail(function () {
            $td2.text('Failed');
        });

        promise.always(function () {
            jlab.hco.bulkRequest(rows, statusId, comment);
        });
    }
};
$(document).on("change", "#category-select", function () {
    var categoryId = $(this).val();
    $("#system-select").select2("val", "");
    jlab.hco.filterSystemListByCategory(categoryId, "#system-select", "", true, true);
    $("system-select").select2("destroy").select2({
        width: 450
    });
});
$(document).on("click", "#select-all-link", function () {

    var systemsArray = [];

    $("#system-select option").each(function (index, obj) {
        var val = $(obj).val();
        systemsArray.push(val);
    });

    $("#system-select").select2("val", systemsArray);

    return false;
});
$(document).on("click", "#select-none-link", function () {
    $("#system-select").select2("val", "");

    return false;
});
$(document).on("click", "#submit-button", function () {
    var comment = $("#comment").val(),
        statusId = $("#status-select").val(),
        systemsArray = [];

    $("#system-select option:selected").each(function (index, obj) {
        systemsArray.push($(obj));
    });

    if (systemsArray.length === 0) {
        alert('Please select at least one subsystem');
        return false;
    }

    if (statusId === '') {
        alert('Please select a status');
        return false;
    }

    if (comment === '') {
        alert('Please provide a comment');
        return false;
    }

    var go = confirm('Are you sure?');

    if (go) {
        jlab.hco.bulkSignoff(systemsArray, statusId, comment);
    }

    return false;
});
$(function () {
    $("#system-select").select2({
        width: 450
    });

    $("#system-select").closest(".li-value").css("visibility", "visible");
});
