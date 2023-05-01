var jlab = jlab || {};
jlab.hco = jlab.hco || {};

jlab.hco.initDialog = function () {
    $(".dialog").dialog({
        autoOpen: false,
        width: 640,
        height: 480,
        modal: true,
        resizable: false
    });

    $("#mask-dialog").dialog({
        width: 700,
        height: 580
    });

    $("#add-dialog").dialog({
        width: 750,
        height: 400,
        resizable: false
    });

    $("#bulk-add-dialog").dialog({
        width: 750,
        height: 400,
        resizable: false
    });

    $("#unpowered-dialog").dialog({
        width: 450,
        height: 300,
        resizable: false
    });

    $("#source-dialog").dialog({
        width: 450,
        height: 300,
        resizable: false
    });

    $("#rename-dialog").dialog({
        width: 450,
        height: 300,
        resizable: false
    });

    $("#system-dialog").dialog({
        width: 650,
        height: 400,
        resizable: false
    });

    $("#region-dialog").dialog({
        width: 450,
        height: 300,
        resizable: false
    });
};
jlab.hco.validateAddComponentForm = function () {
    if ($("#new-component-name").val() === '') {
        alert('Please specify a name');
        return false;
    }
    if ($("#new-component-system-select").val() === '') {
        alert('Please select a system');
        return false;
    }
    if ($("#new-component-region-select").val() === '') {
        alert('Please select a region');
        return false;
    }
    if ($("#new-component-masked").is(":checked") && $("#new-component-masked-reason").val() === '') {
        alert('Please provide a reason for masking this component');
        return false;
    }

    return true;
};
jlab.hco.validateBulkAddComponentForm = function () {
    if ($("#bulk-component-name").val() === '') {
        alert('Please specify a name');
        return false;
    }
    if ($("#bulk-component-system-select").val() === '') {
        alert('Please select a system');
        return false;
    }
    if ($("#bulk-component-region-select").val() === '') {
        alert('Please select a region');
        return false;
    }

    return true;
};
jlab.hco.validateUnpoweredComponentForm = function () {
    return true;
};
jlab.hco.validateSourceComponentForm = function () {
    return true;
};
jlab.hco.validateRenameComponentForm = function () {
    return true;
};
jlab.hco.validateSystemComponentForm = function () {
    return true;
};
jlab.hco.validateRegionComponentForm = function () {
    return true;
};
jlab.hco.validateEditComponentForm = function () {
    if ($("#edit-system-select").val() === '') {
        alert('Please specify a subsystem');
        return false;
    }
    if ($("#edit-component-name").val() === '') {
        alert('Please specify a name');
        return false;
    }
    if ($("#edit-component-region-select").val() === '') {
        alert('Please select a region');
        return false;
    }

    return true;
};
jlab.hco.validateMaskComponentForm = function () {
    if ($("#mask-component-masked").is(":checked") && $("#mask-component-masked-reason").val() === '') {
        alert('Please provide a reason for masking this component');
        return false;
    }

    if ($("#mask-component-masked").is(":checked") && $("#mask-component-mask-expiration").val() === '') {
        alert('Please provide an expiration date');
        return false;
    }

    return true;
};
jlab.hco.maskComponent = function () {
    if (!jlab.hco.validateMaskComponentForm()) {
        return;
    }

    var componentId = $("#mask-component-id").val(),
        masked = $("#mask-component-masked").is(":checked"),
        maskedReason = $("#mask-component-masked-reason").val(),
        expiration = $("#mask-component-mask-expiration").val(),
        url = jlab.contextPath + "/setup/ajax/mask-component",
        data = {
            componentId: componentId,
            masked: masked ? 'Y' : 'N',
            maskedReason: maskedReason,
            expiration: expiration
        },
        $dialog = $("#mask-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
};
jlab.hco.editComponent = function () {
    if (!jlab.hco.validateEditComponentForm()) {
        return;
    }

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    $("#edit-button").html("<span class=\"button-indicator\"></span>");
    $("#edit-button").attr("disabled", "disabled");

    var componentId = $("#edit-component-id").val(),
        name = $("#edit-component-name").val(),
        systemId = $("#edit-system-select").val(),
        regionId = $("#edit-component-region-select").val(),
        force = $("#force-checkbox").is(":checked") ? 'Y' : 'N',
        doReload = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/edit-component",
        type: "POST",
        data: {
            systemId: systemId,
            componentId: componentId,
            name: name,
            regionId: regionId,
            force: force
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to edit component: ' + $(".reason", data).html());
        } else {
            doReload = true;
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to edit component: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to edit component; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (doReload) {
            document.location.reload(true);
        } else {
            $("#edit-button").html("Save");
            $("#edit-button").removeAttr("disabled");
        }
    });
};
jlab.hco.deleteRow = function () {
    var $selectedRow = $("#component-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    var componentId = $selectedRow.attr("data-component-id"),
        componentName = $selectedRow.find(".component-name").text();

    if (!confirm('Are you sure you want to remove ' + componentName)) {
        return;
    }

    jlab.requestStart();

    var doReload = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/delete-component",
        type: "POST",
        data: {
            'component-id': componentId
        },
        dataType: "json"
    });

    request.done(function (json) {
        if (json.stat !== "ok") {
            alert('Unable to delete component: ' + json.error);
        } else {
            doReload = true;
        }
    });

    request.fail(function (xhr, textStatus) {
        //window.console && console.log('Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        var json;

        try {
            json = $.parseJSON(xhr.responseText);
        } catch (err) {
            window.console && console.log('Repsonse is not JSON: ' + xhr.responseText);
            json = {};
        }

        var message = json.error || 'Server did not handle request';
        alert('Unable to delete component: ' + message);
    });

    request.always(function () {
        jlab.requestEnd();
        if (doReload) {
            document.location.reload(true);
        }
    });
};
jlab.hco.openMaskDialog = function () {
    var $selectedRow = $("#component-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    var componentId = $selectedRow.attr("data-component-id");
    var name = $selectedRow.find(".component-name").text();
    var masked = $selectedRow.attr("data-masked");
    var maskedReason = $selectedRow.find(".masked-reason").text();
    var maskExpirationDate = $selectedRow.attr("data-expiration");

    $("#mask-component-id").val(componentId);
    $("#mask-component-name").text(name);
    if (masked === 'true') {
        $("#mask-component-masked").prop("checked", true);
    } else {
        $("#mask-component-masked").prop("checked", false);
    }
    $("#mask-component-masked-reason").val(maskedReason);
    $("#mask-component-mask-expiration").val(maskExpirationDate);

    $("#mask-dialog").dialog("open");
};
$(document).on("click", "#next-button, #previous-button", function () {
    $("#offset-input").val($(this).attr("data-offset"));
    $("#filter-form").submit();
});
$(document).on("click", "#show-add-dialog-button", function () {
    $("#new-component-name").val('');
    $("#new-component-category-select").val('').change();
    $("#new-component-system-select").val('').change();
    $("#new-component-region-select").val('');
    $("#new-component-masked").prop("checked", false);
    $("#new-component-masked-reason").val('');

    $("#add-dialog").dialog("open");
});
$(document).on("click", "#show-bulk-add-dialog-button", function () {
    $("#bulk-component-name").val('');
    $("#bulk-component-category-select").val('').change();
    $("#bulk-component-system-select").val('').change();
    $("#bulk-component-region-select").val('');

    $("#bulk-add-dialog").dialog("open");
});
$(document).on("click", "#add-button", function () {
    if (!jlab.hco.validateAddComponentForm()) {
        return;
    }

    var id = $("#new-component-id").val(),
        name = $("#new-component-name").val(),
        systemId = $("#new-component-system-select").val(),
        regionId = $("#new-component-region-select").val(),
        url = jlab.contextPath + "/setup/ajax/add-component",
        data = {'component-id': id, 'name': name, 'system-id': systemId, 'region-id': regionId},
        $dialog = $("#add-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});
$(document).on("click", "#bulk-add-button", function () {
    if (!jlab.hco.validateBulkAddComponentForm()) {
        return;
    }

    var names = $("#bulk-component-name").val(),
        systemId = $("#bulk-component-system-select").val(),
        regionId = $("#bulk-component-region-select").val(),
        url = jlab.contextPath + "/setup/ajax/bulk-add-component",
        data = {'names': names, 'system-id': systemId, 'region-id': regionId},
        $dialog = $("#bulk-add-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});
$(document).on("click", "#mask-button", function () {
    jlab.hco.maskComponent();
});
$(document).on("click", "#unpowered-button", function () {
    if (!jlab.hco.validateUnpoweredComponentForm()) {
        return;
    }

    var id = $("#unpowered-component-id").val(),
        unpoweredYn = $("#unpowered-component-state").prop("checked") ? "Y" : "N",
        url = jlab.contextPath + "/setup/ajax/edit-component-unpowered",
        data = {'component-id': id, 'unpowered-yn': unpoweredYn},
        $dialog = $("#unpowered-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});
$(document).on("click", "#source-button", function () {
    if (!jlab.hco.validateSourceComponentForm()) {
        return;
    }

    var id = $("#source-component-id").val(),
        source = $("#source-component-source").val(),
        sourceId = $("#source-component-source-id").val(),
        url = jlab.contextPath + "/setup/ajax/edit-component-source",
        data = {'component-id': id, source: source, 'source-id': sourceId},
        $dialog = $("#source-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});
$(document).on("click", "#rename-button", function () {
    if (!jlab.hco.validateRenameComponentForm()) {
        return;
    }

    var id = $("#rename-component-id").val(),
        name = $("#rename-component-name").val(),
        url = jlab.contextPath + "/setup/ajax/rename-component",
        data = {'component-id': id, 'name': name},
        $dialog = $("#rename-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});
$(document).on("click", "#system-button", function () {
    if (!jlab.hco.validateSystemComponentForm()) {
        return;
    }

    var id = $("#system-component-id").val(),
        systemId = $("#system-component-system").val(),
        url = jlab.contextPath + "/setup/ajax/edit-component-system",
        data = {'component-id': id, 'system-id': systemId},
        $dialog = $("#system-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});
$(document).on("click", "#region-button", function () {
    if (!jlab.hco.validateRegionComponentForm()) {
        return;
    }

    var id = $("#region-component-id").val(),
        regionId = $("#region-component-region").val(),
        url = jlab.contextPath + "/setup/ajax/edit-component-region",
        data = {'component-id': id, 'region-id': regionId},
        $dialog = $("#region-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});
$(document).on("click", "#edit-alias-button", function () {
    var id = $("#alias-component-id").val(),
        alias = $("#edit-component-alias").val(),
        url = jlab.contextPath + "/setup/ajax/edit-component-alias",
        data = {'component-id': id, 'alias': alias},
        $dialog = $("#alias-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});
$(document).on("click", "#edit-button", function () {
    jlab.hco.editComponent();
});
$(document).on("click", "#open-mask-component-button", function () {
    jlab.hco.openMaskDialog();
});
$(document).on("click", "#open-unpowered-component-button", function () {
    var $selectedRow = $("#component-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    var componentId = $selectedRow.attr("data-component-id");
    var name = $selectedRow.find(".component-name").text();
    var unpowered = $selectedRow.attr("data-unpowered");

    $("#unpowered-component-id").val(componentId);
    $("#unpowered-component-name").text(name);
    if (unpowered === 'true') {
        $("#unpowered-component-state").prop("checked", true);
    } else {
        $("#unpowered-component-state").prop("checked", false);
    }

    $("#unpowered-dialog").dialog("open");
});
$(document).on("click", "#open-source-component-button", function () {
    var $selectedRow = $("#component-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    var componentId = $selectedRow.attr("data-component-id"),
        name = $selectedRow.find(".component-name").text(),
        source = $selectedRow.attr("data-source"),
        sourceId = $selectedRow.attr("data-source-id");

    $("#source-component-id").val(componentId);
    $("#source-component-name").text(name);
    $("#source-component-source").val(source);
    $("#source-component-source-id").val(sourceId);

    $("#source-dialog").dialog("open");
});
$(document).on("click", "#open-alias-component-button", function () {
    var $selectedRow = $("#component-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    var componentId = $selectedRow.attr("data-component-id"),
        alias = $selectedRow.attr("data-alias");

    $("#alias-component-id").val(componentId);
    $("#edit-component-alias").val(alias);

    $("#alias-dialog").dialog("open");
});
$(document).on("click", "#show-rename-dialog-button", function () {
    var $selectedRow = $("#component-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    var componentId = $selectedRow.attr("data-component-id"),
        name = $selectedRow.find(".component-name").text(),
        internalSource = $selectedRow.attr("data-source") === 'INTERNAL';

    if (internalSource) {
        $("#rename-dialog .warning-banner").hide();
    } else {
        $("#rename-dialog .warning-banner").show();
    }

    $("#rename-component-id").val(componentId);
    $("#rename-component-old-name").text(name);
    $("#rename-component-name").val(name);

    $("#rename-dialog").dialog("open");
});
$(document).on("click", "#show-system-dialog-button", function () {
    var $selectedRow = $("#component-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    var componentId = $selectedRow.attr("data-component-id"),
        name = $selectedRow.find(".component-name").text(),
        systemId = $selectedRow.attr("data-system-id"),
        internalSource = $selectedRow.attr("data-source") === 'INTERNAL';

    if (internalSource) {
        $("#system-dialog .warning-banner").hide();
    } else {
        $("#system-dialog .warning-banner").show();
    }

    $("#system-component-id").val(componentId);
    $("#system-component-name").text(name);
    $("#system-component-system").val(systemId);

    $("#system-dialog").dialog("open");
});
$(document).on("click", "#show-region-dialog-button", function () {
    var $selectedRow = $("#component-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    var componentId = $selectedRow.attr("data-component-id"),
        name = $selectedRow.find(".component-name").text(),
        regionId = $selectedRow.attr("data-region-id");

    $("#region-component-id").val(componentId);
    $("#region-component-name").text(name);
    $("#region-component-region").val(regionId);

    $("#region-dialog").dialog("open");
});
$(document).on("click", "#delete-component-button", function () {
    jlab.hco.deleteRow();
});
$(document).on("click", ".default-clear-panel", function () {
    $("#destination-select").val('');
    $("#category-select").val('').trigger('change');
    $("#system-select").val('');
    $("#region-select").val('');
    $("#group-select").val('');
    $("#source-select").val('');
    $("#masked-select").val('');
    $("#unpowered-select").val('');
    $("#component").val('');
    return false;
});
$(document).on("change", "#category-select", function () {
    var categoryId = $(this).val();
    jlab.hco.filterSystemListByCategory(categoryId, "#system-select", "");
});
$(document).on("change", "#new-component-category-select", function () {
    var categoryId = $(this).val();
    jlab.hco.filterSystemListByCategory(categoryId, "#new-component-system-select", "");
});
$(document).on("change", "#bulk-component-category-select", function () {
    var categoryId = $(this).val();
    jlab.hco.filterSystemListByCategory(categoryId, "#bulk-component-system-select", "");
});
$(document).on("click", ".close-bubble", function () {
    $(".flyout-handle").hide(); /*We don't remove because of weird FF behavior on form*/
    return false;
});
$(document).on("click", ".flyout-link", function () {
    $(".flyout-handle").remove();
    var flyout = $("." + $(this).attr("data-flyout-type") + " .flyout-panel").clone();
    $(this).parent().append('<div class="flyout-handle"></div>');
    $(".flyout-handle").append(flyout);
    return false;
});
$(function () {
    jlab.hco.initDialog();
});