var jlab = jlab || {};
jlab.srm = jlab.srm || {};

jlab.srm.deleteRows = function () {
    var $selectedRows = $("#super-table tbody tr.selected-row");

    if ($selectedRows.length < 1) {
        return;
    }

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    var signoffNameArray = [];
    var savedSignoffIdArray = [];

    $selectedRows.each(function(){
        signoffNameArray.push($(this).find("td:nth-child(1)").text());
        savedSignoffIdArray.push($(this).attr("data-saved-signoff-id"));
    });

    if (!confirm('Are you sure you want to remove the saved signoff(s): \n' + signoffNameArray.join("\n"))) {
        return;
    }

    jlab.requestStart();

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/remove-saved-signoff",
        type: "POST",
        data: {
            'saved-signoff-id-array': savedSignoffIdArray
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to remove saved signoff: ' + $(".reason", data).html());
        } else {
            /* Success */
            location.reload();
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to remove saved signoff: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to remove saved signoff; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
    });
};

jlab.srm.selectRow = function () {
    $(this).addClass("ui-selected").siblings().removeClass("ui-selected");
    $("#open-preview-dialog-button").removeAttr("disabled");
    $("#delete-row-button").removeAttr("disabled");
};

jlab.srm.resetSelection = function () {
    $("#super-table tbody tr").removeClass("ui-selected");
    $("#open-preview-dialog-button").attr("disabled", "disabled");
    $("#delete-row-button").attr("disabled", "disabled");
};

jlab.srm.initDialogs = function () {
    $("#add-dialog").dialog({
        autoOpen: false,
        width: 750,
        height: 650,
        minWidth: 750,
        minHeight: 650,
        modal: true
    });
    $("#edit-dialog").dialog({
        autoOpen: false,
        width: 750,
        height: 650,
        minWidth: 750,
        minHeight: 650,
        modal: true
    });
};

jlab.srm.setUndoSortState = function () {
    jlab.srm.tableCloneForUndo = $("#super-table").clone(false);
};

jlab.srm.undoSort = function () {
    $("#super-table").replaceWith(hco.table.tableCloneForUndo);
    jlab.srm.initSortAndSelect();
};

jlab.srm.saveSortOrder = function (event, ui) {

    var idArray = [];

    $("#super-table tbody tr").each(function () {
        var id = $(this).attr("data-downgrade-id");
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
        url: jlab.contextPath + "/setup/ajax/order-saved-signoff",
        type: "POST",
        data: {
            'downgrade-id[]': idArray
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to order downgrades: ' + $(".reason", data).html());
            jlab.srm.undoSort();
        } else {
            /* Success */
            /*hco.table.renumber();*/

            /* update undo cache for next time */
            jlab.srm.setUndoSortState();
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to order downgrade: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to order downgrade; server did not handle request');
        jlab.srm.undoSort();
    });

    request.always(function () {
        jlab.requestEnd();
    });
};

jlab.srm.initSortAndSelect = function () {
    $("#super-table tbody").sortable({
        handle: ".drag-handle",
        update: jlab.srm.saveSortOrder
    });

    $("#super-table tbody tr").each(function () {
        $(this).children().each(function () {
            $(this).width($(this).width());
        });
    });

    jlab.srm.setUndoSortState();
};

jlab.srm.resetAddForm = function () {
    $("#add-type-select").val(2); /*for now default to One Month+*/
    $("#signoff-name").val("");
    $("#signoff-status-select").val("");
    $("#comments").val("");
    $("#add-group-select").val($("#group-select").val());
    $("#add-group-select").trigger("change");
    $("#add-system-select").val($("#system-select").val()); /*Default to what is selected in filter / passed in url param*/
    $("#region-select").val("");
    $("#filter-status-select").val("");
    $("#component").val("");

    $("#autofill-checkbox").prop("checked", true);
    $("#autofill-checkbox").trigger("change");
    $("#downgrade-only-checkbox").prop("checked", true);
    $("#downgrade-only-checkbox").trigger("change");
};

jlab.srm.validateAddForm = function () {
    if ($("#add-type-select").val() === '') {
        alert('Please provide a signoff type');
        return false;
    }
    if ($("#signoff-name").val() === '') {
        alert('Please provide a signoff name');
        return false;
    }
    if ($("#signoff-status-select").val() === '') {
        alert('Please select a signoff status');
        return false;
    }
    if ($("#add-system-select").val() === '') {
        alert('Please select a system');
        return false;
    }
    if ($("#add-group-select").val() === '') {
        alert('Please select a group');
        return false;
    }

    return true;
};

jlab.srm.validateEditForm = function () {
    if ($("#update-checklist-required-select").val() === '') {
        alert('Please select a checklist required value');
        return false;
    }

    return true;
};

jlab.srm.resizeTable = function () {
    $("#super-table tbody tr").each(function () {
        $(this).children().each(function () {
            $(this).width('auto');
        });
    });

    $("#super-table tbody tr").each(function () {
        $(this).children().each(function () {
            $(this).width($(this).width());
        });
    });
};

jlab.srm.updateRow = function (groupResponsibilityId, checklistRequired) {
    /*Note: this method actually updates the row to the actual table AND the cached clone version used for sort order undo too!*/

    var $row = $("#super-table tbody tr[data-group-responsibility-id=" + groupResponsibilityId + "]");

    $row.find("td:nth-child(4)").text(checklistRequired);

    jlab.srm.resizeTable();
};

jlab.srm.addRow = function (savedSignoffId, signoffName, signoffStatusName, comments, systemId, systemName, groupId, groupName, regionId, regionName, filterStatusId, filterStatusName, componentName) {
    /*Note: this method actually adds the row to the actual table AND the cached clone version used for sort order undo too!*/

    var $row = ("<tr data-saved-signoff-id=\"" + savedSignoffId + "\"><td>" + signoffName + "</td><td>" + signoffStatusName + "</td><td>" + comments + "</td><td data-system-id=\"" + systemId + "\">" + systemName + "</td><td data-group-id=\"" + groupId + "\">" + groupName + "</td><td data-region-id=\"" + regionId + "\">" + regionName + "</td><td data-status-id=\"" + filterStatusId + "\">" + filterStatusName + "</td><td>" + componentName + "</td></tr>");
    $("#super-table tbody").append($row);
};

jlab.srm.doEditAction = function () {
    if (!jlab.srm.validateEditForm()) {
        return;
    }

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var groupResponsibilityId = $("#updateGroupResponsibilityId").val(),
        checklistRequiredVal = $("#update-checklist-required-select").val(),
        checklistRequiredName = $("#update-checklist-required-select option:selected").text();

    window.console && console.log("groupResponsibilityId: " + groupResponsibilityId);

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/edit-saved-signoff",
        type: "POST",
        data: {
            groupResponsibilityId: groupResponsibilityId,
            checklistRequired: checklistRequiredVal
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to edit downgrade: ' + $(".reason", data).html());
        } else {
            jlab.srm.updateRow(groupResponsibilityId, checklistRequiredName);
            $("#edit-dialog").dialog("close");
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to edit downgrade: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to edit downgrade; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
    });
};

jlab.srm.doAddAction = function () {
    if (!jlab.srm.validateAddForm()) {
        return;
    }

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    $("#add-button").html("<span class=\"button-indicator\"></span>");
    $("#add-button").attr("disabled", "disabled");

    var leaveSpinning = false,
        typeId = $("#add-type-select").val(),
        signoffName = $("#signoff-name").val(),
        signoffStatusId = $("#signoff-status-select").val(),
        comments = $("#comments").val(),
        systemId = $("#add-system-select").val(),
        groupId = $("#add-group-select").val(),
        regionId = $("#region-select").val(),
        filterStatusId = $("#filter-status-select").val(),
        componentName = $("#component").val();

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/add-saved-signoff",
        type: "POST",
        data: {
            typeId: typeId,
            signoffName: signoffName,
            signoffStatusId: signoffStatusId,
            comments: comments,
            systemId: systemId,
            groupId: groupId,
            regionId: regionId,
            filterStatusId: filterStatusId,
            componentName: componentName
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to add saved signoff: ' + $(".reason", data).html());
        } else {
            /* Success */
            leaveSpinning = true;
            /*document.location.reload(true); causes issues with pop=true*/
            var cleanedUrl = window.location.href.replace("pop=true", "pop=false");
            window.location.href = cleanedUrl;
            location.reload();
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to add saved signoff: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to add saved signoff; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (!leaveSpinning) {
            $("#add-button").html("Save");
            $("#add-button").removeAttr("disabled");
        }
    });
};

jlab.srm.openEditDialog = function () {
    var $selectedRow = $("#super-table tbody tr.ui-selected");

    if ($selectedRow.length < 1) {
        return;
    }

    var system = $("#edit-system-select option:selected").text();
    var group = $selectedRow.find("td:nth-child(3)").text();

    $("#edit-dialog").find(".system-placeholder").text(system);
    $("#edit-dialog").find(".group-placeholder").text(group);

    $("#edit-dialog").dialog("open");
};

jlab.srm.filterSystemList = function (groupId) {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/filter-system-list-by-group",
        type: "GET",
        data: {
            groupId: groupId
        },
        dataType: "json"
    });

    request.done(function (data) {
        if (data.status !== "success") {
            alert('Unable to filter system list : ' + data.errorReason);
        } else {
            /* Success */
            var $select = $("#add-system-select"),
                $selected = $("#add-system-select").val();
            $select.hide();
            $select.empty();
            $select.append('<option> </option>');
            $(data.optionList).each(function () {
                $select.append('<option value="' + this.value + '">' + this.name + '</option>');
            });
            $select.slideDown();
            $select.val($selected);
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to filter system list: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to filter system list; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
    });
};

jlab.srm.doAutofill = function () {
    var typeName = $("#add-type-select option:selected").text(),
        groupName = $("#add-group-select option:selected").text(),
        systemName = $("#add-system-select option:selected").text(),
        $nameInput = $("#signoff-name"),
        $commentsInput = $("#comments");

    if (typeName === ' ') {
        typeName = '<type>';
    }

    if (groupName === ' ') {
        groupName = '<group>';
    }

    if (systemName === ' ') {
        systemName = '<system>';
    }

    var cannedText = groupName + ' ' + systemName + ' ' + typeName;

    $nameInput.val(cannedText);
    $commentsInput.val(typeName);
};

jlab.srm.doSignoffMultiple = function() {
    var $selectedRows = $("#super-table tbody tr.selected-row");

    if ($selectedRows.length < 1) {
        return;
    }

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    var savedSignoffIdArray = [],
        maxModified = $("#max-modified").val();


    $selectedRows.each(function(){
        savedSignoffIdArray.push($(this).attr("data-saved-signoff-id"));
    });

    jlab.requestStart();

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/signoff-multiple-saved",
        type: "POST",
        data: {
            'saved-signoff-id-array': savedSignoffIdArray,
            maxModified: maxModified
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to signoff saved: ' + $(".reason", data).html());
        } else {
            /* Success */
            location.reload();
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to signoff saved: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to remove saved signoff; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
    });
};

$(document).on("click", "#remove-row-button", function () {
    jlab.srm.deleteRows();
});

$(document).on("click", ".preview-button", function () {
    var $selectedRow = $(this).closest("tr");

    if ($selectedRow.length < 1) {
        return;
    }

    var systemId = $selectedRow.attr("data-system-id"),
        groupId = $selectedRow.attr("data-group-id"),
        regionId = $selectedRow.attr("data-region-id"),
        filterStatusId = $selectedRow.attr("data-status-id"),
        componentName = $selectedRow.attr("data-component-name"),
        status = $selectedRow.find("td:nth-child(3)").text(),
        comments = $selectedRow.find("td:nth-child(4)").text(),
        maxModified = $("#max-modified").val();

    window.open('/srm/signoff?groupId=' + groupId + '&systemId=' + systemId + '&regionId=' + regionId + '&statusId=' + filterStatusId + '&component=' + encodeURIComponent(componentName) + '&comments=' + encodeURIComponent(comments) + '&signoffStatus=' + encodeURIComponent(status) + '&maxLastModified=' + encodeURIComponent(maxModified) + '&pop=true&qualified=');
});

$(document).on("click", "#open-add-dialog-button", function () {
    jlab.srm.resetAddForm();
    $("#add-dialog").dialog("open");
});

$(document).on("click", "#open-edit-dialog-button", function () {
    jlab.srm.openEditDialog();
});

$(document).on("click", "#add-button", function () {
    jlab.srm.doAddAction();
});

$(document).on("click", "#edit-button", function () {
    jlab.srm.doEditAction();
});

$(document).on("click", "#signoff-button", function () {
    if (confirm('Are you sure you want to signoff all selected?')) {
        jlab.srm.doSignoffMultiple();
    }
});

$(document).on("change", "#add-group-select", function () {
    var groupId = $(this).val();
    jlab.srm.filterSystemList(groupId);
});

$(document).on("change", "#signoff-status-select", function () {
    var downgradeOnly = $("#downgrade-only-checkbox").is(":checked"),
        statusId = $(this).val(),
        $select = $("#filter-status-select"),
        $filterSelected = $("#filter-status-select").val();
    if (downgradeOnly && statusId === "50") {
        $select.hide();
        $select.empty();
        $select.append('<option value="1">Ready</option>');
        $select.slideDown();
    } else {
        $select.hide();
        $select.empty();
        $select.append('<option></option>');
        $select.append('<option value="1">Ready</option>');
        $select.append('<option value="50">Checked</option>');
        $select.append('<option value="100">Not Ready</option>');
        $select.slideDown();
        $select.val($filterSelected);
    }
});

$(document).on("change", "#downgrade-only-checkbox", function () {
    var downgradeOnly = $("#downgrade-only-checkbox").is(":checked"),
        $select = $("#signoff-status-select");
    if (downgradeOnly) {
        $select.hide();
        $select.empty();
        $select.append('<option></option>');
        $select.append('<option value="50">Checked</option>');
        $select.append('<option value="100">Not Ready</option>');
        $select.slideDown();
    } else {
        $select.hide();
        $select.empty();
        $select.append('<option></option>');
        $select.append('<option value="1">Ready</option>');
        $select.append('<option value="50">Checked</option>');
        $select.append('<option value="100">Not Ready</option>');
        $select.slideDown();
    }
    $select.trigger("change");
});

$(document).on("change", "#autofill-checkbox", function () {
    var autofill = $("#autofill-checkbox").is(":checked");

    if (autofill) {
        $("#comments").attr("disabled", "disabled");
        $("#signoff-name").attr("disabled", "disabled");
        jlab.srm.doAutofill();
    } else {
        $("#comments").removeAttr("disabled");
        $("#signoff-name").removeAttr("disabled");
        $("#comments").val("");
        $("#signoff-name").val("");
    }
});

$(document).on("change", "#add-type-select", function () {
    var autofill = $("#autofill-checkbox").is(":checked");

    if (autofill) {
        jlab.srm.doAutofill();
    }
});

$(document).on("change", "#add-group-select", function () {
    var autofill = $("#autofill-checkbox").is(":checked");

    if (autofill) {
        jlab.srm.doAutofill();
    }
});

$(document).on("change", "#add-system-select", function () {
    var autofill = $("#autofill-checkbox").is(":checked");

    if (autofill) {
        jlab.srm.doAutofill();
    }
});

$(document).on("click", ".default-clear-panel", function () {
    $("#type-select").val('');
    $("#system-select").val('');
    $("#group-select").val('');
    return false;
});

$(document).on("click", "#select-all-button", function () {
    jlab.editableRowTable.lastSelectedRow = $(".editable-row-table tbody tr").length - 1;

    $(".editable-row-table tbody tr").addClass("selected-row");
    $(".no-selection-row-action").prop("disabled", true);
    $(".selected-row-action").prop("disabled", false);

    jlab.editableRowTable.updateSelectionCount();
});

$(function () {
    jlab.srm.initSortAndSelect();
    jlab.srm.initDialogs();
});