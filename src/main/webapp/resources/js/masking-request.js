jlab = jlab || {};
jlab.hco = jlab.hco || {};

jlab.hco.denyMasking = function () {

    var $selectedRow = $("#request-table tbody tr.selected-row"),
        requestId = $selectedRow.attr("data-request-id"),
        url = jlab.contextPath + "/ajax/deny-mask-request",
        data = {requestId: requestId},
        $dialog = null;

    $("#deny-button")
        .height($("#deny-button").height())
        .width($("#deny-button").width())
        .empty().append('<div class="button-indicator"></div>');

    var promise = jlab.doAjaxJsonPostRequest(url, data, $dialog, true);

    promise.fail(function () {
        $("#deny-button").text("Reject");
    });
};

jlab.hco.acceptMasking = function () {
    var $selectedRow = $("#request-table tbody tr.selected-row"),
        requestId = $selectedRow.attr("data-request-id"),
        reason = $("#mask-request-reason").val(),
        expiration = $("#mask-request-expiration").val(),
        url = jlab.contextPath + "/ajax/accept-mask-request",
        data = {requestId: requestId, reason: reason, expiration: expiration},
        $dialog = $("#approval-dialog");

    $("#approve-save-button")
        .height($("#approve-save-button").height())
        .width($("#approve-save-button").width())
        .empty().append('<div class="button-indicator"></div>');

    var promise = jlab.doAjaxJsonPostRequest(url, data, $dialog, true);

    promise.fail(function () {
        $("#approve-save-button").text("Save");
    });
};

$(document).on("click", ".default-clear-panel", function () {
    $("#destination-select").select2("val", "");
    $("#category-select").val('').trigger('change');
    $("#system-select").val('');
    $("#region-select").val('');
    $("#group-select").val('');
    $("#reason").val('');
    $("#status").val('');
    return false;
});
$(document).on("click", ".default-reset-panel", function () {
    var $select = $("#destination-select");
    $select.select2("val", $select.attr("data-current-run-id-csv").split(","));
    $("#category-select").val('').trigger('change');
    $("#system-select").val('');
    $("#region-select").val('');
    $("#group-select").val('');
    $("#reason").val('');
    $("#status").val("PENDING");
    return false;
});

$(document).on("change", "#category-select", function () {
    var categoryId = $(this).val();
    jlab.hco.filterSystemListByCategory(categoryId);
});

$(document).on("click", "#open-approval-dialog-button", function () {
    var $selectedRow = $("#request-table tbody tr.selected-row"),
        response = $selectedRow.find("td:nth-child(1)").text(),
        component = $selectedRow.find("td:nth-child(2)").text(),
        reason = $selectedRow.find("td:nth-child(4)").text(),
        expiration = $selectedRow.find("td:nth-child(5)").text();

    $("#mask-request-component").text(component);
    $("#mask-request-response").val(response);
    $("#mask-request-reason").val(reason);
    $("#mask-request-expiration").val(expiration);

    $("#approval-dialog").dialog("open");
});

$(document).on("click", "#approve-save-button", function () {
    jlab.hco.acceptMasking();
});

$(document).on("click", "#deny-button", function () {
    var $selectedRow = $("#request-table tbody tr.selected-row"),
        component = $selectedRow.find("td:nth-child(2)").text();

    if (confirm('Are you sure you want to deny masking ' + component + "?")) {
        jlab.hco.denyMasking();
    }
});

$(function () {
    $("#destination-select").select2({
        width: 390
    });

    $("#destination-select").closest(".li-value").css("visibility", "visible");

    $("#approval-dialog").dialog({
        autoOpen: false,
        resizable: false,
        width: 500,
        height: 400
    });
});

