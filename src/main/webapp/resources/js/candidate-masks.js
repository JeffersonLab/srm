var jlab = jlab || {};
jlab.srm = jlab.srm || {};

jlab.srm.initDialog = function () {
    $(".dialog").dialog({
        autoOpen: false,
        width: 700,
        height: 600,
        modal: true,
        resizable: false
    });
};
jlab.srm.editComponentException = function () {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var componentIdArray = $.parseJSON($("#selected-row-list").attr('data-id-json')),
        maskedReason = $("#mask-component-masked-reason").val(),
        doReload = false,
        expirationDate = $("#mask-expiration").val();

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/edit-component-exception",
        type: "POST",
        data: {
            'componentId[]': componentIdArray,
            exceptionReason: maskedReason,
            'expiration-date': expirationDate
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to edit component exception: ' + $(".reason", data).html());
        } else {
            doReload = true;
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to edit component exception: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to edit component exception; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (doReload) {
            document.location.reload(true);
        } else {
            $(".dialog-submit-button").empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
};
jlab.srm.openExceptionDialog = function () {
    var componentNameArray = [],
        componentIdArray = [],
        componentReasonArray = [];

    if ($("#component-table .inner-table .selected-row").length < 1) {
        window.console && console.log('No rows selected');
        return;
    }

    $("#component-table .inner-table .selected-row").each(function () {
        var componentName = $(this).closest("tr").find(":nth-child(1) a").text(),
            componentId = $(this).closest("tr").attr("data-component-id"),
            reason = '';

        componentNameArray.push(componentName);
        componentIdArray.push(componentId);
        componentReasonArray.push(reason);
    });

    var $selectedList = $("#selected-row-list");

    $selectedList.attr("data-id-json", JSON.stringify(componentIdArray));

    $selectedList.empty();

    for (var i = 0; i < componentNameArray.length; i++) {
        $selectedList.append('<li>' + componentNameArray[i] + '</li>');
    }

    var count = $("#selected-count").text() * 1;
    var componentStr = (count === 1) ? ' Component' : ' Components';
    $("#exception-dialog-component-count").text(count + componentStr);


    $("#mask-component-masked-reason").val(componentReasonArray[0]);

    var reasonsDiffer = false;

    for (var i = 1; i < componentReasonArray.length; i++) {
        if (componentReasonArray[0] !== componentReasonArray[i]) {
            reasonsDiffer = true;
            break;
        }
    }

    if (reasonsDiffer) {
        $(".rows-differ-message").show();
    } else {
        $(".rows-differ-message").hide();
    }

    $("#exception-dialog").dialog("open");
};
$(document).on("click", "#mask-button", function () {
    jlab.srm.editComponentException();
});
$(document).on("click", "#open-edit-exception-button", function () {
    jlab.srm.openExceptionDialog();
});
$(document).on("click", ".default-clear-panel", function () {
    $("#destination-select").select2("val", "");
    $("#category-select").val('').trigger('change');
    $("#system-select").val('');
    $("#region-select").val('');
    $("#group-select").val('');
    $("#masked-select").val('');
    $("#exception-select").val('');
    $("#unpowered-select").val('');
    $("#component").val('');
    $("#status-select").select2("val", "");
    return false;
});
$(document).on("click", ".default-reset-panel", function () {
    $select = $("#destination-select");
    $select.select2("val", $select.attr("data-current-run-id-csv").split(","));
    $("#category-select").val('').trigger('change');
    $("#system-select").val('');
    $("#region-select").val('');
    $("#group-select").val('');
    $("#masked-select").val('N');
    $("#exception-select").val('');
    $("#unpowered-select").val('');
    $("#component").val('');
    $("#status-select").select2("val", ["50", "100"]);
    return false;
});
$(document).on("click", "#candidate-link", function () {
    $("#status-select").select2("val", ["50", "100"]);
    return false;
});
$(document).on("change", "#category-select", function () {
    var categoryId = $(this).val();
    jlab.srm.filterSystemListByCategory(categoryId, "#system-select", "");
});
$(document).on("change", "#new-component-category-select", function () {
    var categoryId = $(this).val();
    jlab.srm.filterSystemListByCategory(categoryId, "#new-component-system-select", "");
});

$(function () {
    jlab.srm.initDialog();

    $("#destination-select").select2({
        width: 390
    });

    $("#status-select").select2({
        width: 390
    });

    $("#destination-select").closest(".li-value").css("visibility", "visible");

    $("#status-select").closest(".li-value").css("visibility", "visible");
});