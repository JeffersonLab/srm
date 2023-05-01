var jlab = jlab || {};
jlab.srm = jlab.srm || {};

jlab.editableRowTable.entity = 'Responsibility';
jlab.editableRowTable.dialog.width = 550;
jlab.editableRowTable.dialog.height = 400;

jlab.srm.deleteRow = function () {
    var $selectedRow = $("#responsibility-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    var groupName = $selectedRow.find("td:nth-child(3)").text();

    if (!confirm('Are you sure you want to remove the responsibility for group ' + groupName)) {
        return;
    }

    jlab.requestStart();

    var groupResponsibilityId = $selectedRow.attr("data-group-responsibility-id");

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/delete-group-responsibility",
        type: "POST",
        data: {
            'group-responsibility-id': groupResponsibilityId
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to delete group responsibility: ' + $(".reason", data).html());
        } else {
            /* Success */
            $selectedRow.remove();
            jlab.srm.renumber();
            jlab.srm.setUndoSortState();
            jlab.srm.resizeTable();
            $("#unselect-all-button").click();
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to delete group responsibility: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to delete group responsibility; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
    });
};

jlab.srm.initDialogs = function () {
    $("#create-responsibility-dialog").dialog({
        autoOpen: false,
        width: 640,
        height: 480,
        modal: true
    });
    $("#update-responsibility-dialog").dialog({
        autoOpen: false,
        width: 640,
        height: 480,
        modal: true
    });
};

jlab.srm.renumber = function () {
    $("#responsibility-table tbody tr").each(function () {
        var index = $(this).index() + 1;

        $(this).find("td:nth-child(2)").text(index);
    });
};

jlab.srm.setUndoSortState = function () {
    jlab.srm.tableCloneForUndo = $("#responsibility-table").clone(false);
};

jlab.srm.undoSort = function () {
    $("#responsibility-table").replaceWith(jlab.srm.tableCloneForUndo);
    jlab.srm.initSortAndSelect();
};

jlab.srm.saveSortOrder = function (event, ui) {

    var idArray = [];

    $("#responsibility-table tbody tr").each(function () {
        var id = $(this).attr("data-group-responsibility-id");
        idArray.push(id);
    });

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    /*var i;
     for(i = 0; i < idArray.length; i++) {
     window.console && console.log("Id: " + idArray[i]);        
     }*/

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/order-group-responsibility",
        type: "POST",
        data: {
            'group-responsibility-id[]': idArray
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to order group responsibility: ' + $(".reason", data).html());
            jlab.srm.undoSort();
        } else {
            /* Success */
            jlab.srm.renumber();

            /* update undo cache for next time */
            jlab.srm.setUndoSortState();
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to order group responsibility: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to order group responsibility; server did not handle request');
        jlab.srm.undoSort();
    });

    request.always(function () {
        jlab.requestEnd();
    });
};

jlab.srm.initSortAndSelect = function () {
    $("#responsibility-table tbody").sortable({
        handle: ".drag-handle",
        update: jlab.srm.saveSortOrder
    });

    $("#responsibility-table tbody tr").each(function () {
        $(this).children().each(function () {
            $(this).width($(this).width());
        });
    });

    jlab.srm.setUndoSortState();
};

jlab.srm.validateCreateResponsibilityForm = function () {
    if ($("#group-select").val() === '') {
        alert('Please select a group');
        return false;
    }
    if ($("#create-checklist-required-select").val() === '') {
        alert('Please select a checklist required value');
        return false;
    }

    return true;
};

jlab.srm.resizeTable = function () {
    $("#responsibility-table tbody tr").each(function () {
        $(this).children().each(function () {
            $(this).width('auto');
        });
    });

    $("#responsibility-table tbody tr").each(function () {
        $(this).children().each(function () {
            $(this).width($(this).width());
        });
    });
};

jlab.srm.updateRow = function (groupResponsibilityId, checklistRequired) {
    /*Note: this method actually updates the row to the actual table AND the cached clone version used for sort order undo too!*/

    var $row = $("#responsibility-table tbody tr[data-group-responsibility-id=" + groupResponsibilityId + "]");

    $row.find("td:nth-child(4)").text(checklistRequired);

    jlab.srm.resizeTable();
};

jlab.srm.addRow = function (groupResponsibilityId, groupName, checklistRequired, order) {
    /*Note: this method actually adds the row to the actual table AND the cached clone version used for sort order undo too!*/

    var $row = ("<tr data-group-responsibility-id=\"" + groupResponsibilityId + "\"><td class=\"drag-handle\"><span class=\"ui-icon ui-icon-carat-2-n-s\"></span></td><td>" + order + "</td><td>" + groupName + "</td><td>" + checklistRequired + "</td></tr>");
    $("#responsibility-table tbody").append($row);
};

jlab.srm.updateResponsibility = function () {
    if (!jlab.srm.validateCreateResponsibilityForm()) {
        return;
    }

    var groupResponsibilityId = $("#updateGroupResponsibilityId").val(),
        checklistRequiredVal = $("#create-checklist-required-select").val(),
        checklistRequiredName = $("#create-checklist-required-select option:selected").text();
    url = jlab.contextPath + "/setup/ajax/update-group-responsibility",
        data = {groupResponsibilityId: groupResponsibilityId, checklistRequired: checklistRequiredVal},
        $dialog = $("#table-row-dialog");

    var promise = jlab.doAjaxJsonPostRequest(url, data, $dialog, false);

    promise.done(function (json) {
        if (json.stat !== "ok") {
            var errorMsg = json.error || "Unknown Error";
            alert('Unable to update group responsibility: ' + errorMsg);
        } else {
            jlab.srm.updateRow(groupResponsibilityId, checklistRequiredName);
        }

    });
};

jlab.srm.createResponsibility = function () {
    if (!jlab.srm.validateCreateResponsibilityForm()) {
        return;
    }

    var systemId = $("#system-select").val(),
        groupId = $("#group-select").val(),
        groupName = $("#group-select option:selected").text(),
        checklistRequiredVal = $("#create-checklist-required-select").val(),
        checklistRequiredName = $("#create-checklist-required-select option:selected").text(),
        order = $("#responsibility-table tbody tr").length + 1,
        url = jlab.contextPath + "/setup/ajax/create-group-responsibility",
        data = {systemId: systemId, groupId: groupId, checklistRequired: checklistRequiredVal, order: order},
        $dialog = $("#table-row-dialog");

    var promise = jlab.doAjaxJsonPostRequest(url, data, $dialog, false);

    promise.done(function (json) {
        if (json.stat !== "ok") {
            var errorMsg = json.error || "Unknown Error";
            alert('Unable to create new group responsibility: ' + errorMsg);
        } else {
            var groupResponsibilityId = json.id;
            jlab.srm.addRow(groupResponsibilityId, groupName, checklistRequiredName, order); /*Also updates undo cache; no need to reorder since appends to end*/
            jlab.srm.resizeTable(); /*Also sets width of new cells for drag and drop*/
        }
    });
};

jlab.srm.openUpdateDialog = function () {
    var $selectedRow = $("#responsibility-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    var system = $("#system-select option:selected").text();
    var groupId = $selectedRow.attr("data-group-id");
    var checklistRequired = $selectedRow.find("td:nth-child(4)").text();
    var groupResponsibilityId = $selectedRow.attr("data-group-responsibility-id");

    $("#table-row-dialog").find(".system-placeholder").text(system);
    $("#group-select").val(groupId);
    $("#group-select").prop("disabled", true);
    $("#table-row-dialog").find("#create-checklist-required-select").val(checklistRequired === 'Yes' ? 'Y' : 'N');
    $("#table-row-dialog").find("#updateGroupResponsibilityId").val(groupResponsibilityId);
};

$(document).on("click", "#remove-row-button", function () {
    jlab.srm.deleteRow();
});

$(document).on("click", "#open-add-row-dialog-button", function () {
    var system = $("#system-select option:selected").text();

    $("#table-row-dialog").find(".system-placeholder").text(system);

    $("#group-select").prop("disabled", false);
});

$(document).on("click", "#open-edit-row-dialog-button", function () {
    jlab.srm.openUpdateDialog();
});

$(document).on("table-row-add", function () {
    jlab.srm.createResponsibility();
});

$(document).on("table-row-edit", function () {
    jlab.srm.updateResponsibility();
});
$(document).on("click", ".default-clear-panel", function () {
    $("#category-select").val('').trigger('change');
    $("#system-select").val('');
    return false;
});
$(document).on("change", "#category-select", function () {
    var categoryId = $(this).val();
    jlab.srm.filterSystemListByCategory(categoryId, "#system-select", "");
});
$(function () {
    jlab.srm.initSortAndSelect();
    jlab.srm.initDialogs();
});