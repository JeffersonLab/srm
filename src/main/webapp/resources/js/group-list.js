var jlab = jlab || {};
jlab.srm = jlab.srm || {};

jlab.editableRowTable.entity = 'Group';
jlab.editableRowTable.dialog.width = 550;
jlab.editableRowTable.dialog.height = 400;

jlab.srm.deleteRow = function () {
    var $selectedRow = $(".editable-row-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    var groupName = $selectedRow.find("td:nth-child(1)").text();

    if (!confirm('Are you sure you want to remove the group ' + groupName + '?  All of associated signoffs and checklists will be lost.')) {
        return;
    }

    var groupId = $selectedRow.attr("data-group-id"),
        url = jlab.contextPath + "/setup/ajax/delete-group",
        data = {'group-id': groupId},
        $dialog = null;

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
};
jlab.srm.initDialogs = function () {
    $("#add-dialog").dialog({
        autoOpen: false,
        width: 640,
        height: 480,
        modal: true
    });
    $("#edit-dialog").dialog({
        autoOpen: false,
        width: 640,
        height: 480,
        modal: true
    });
};

jlab.srm.validateRowForm = function () {
    if ($("#row-name").val() === '') {
        alert('Please select a name');
        return false;
    }
    if ($("#row-description").val() === '') {
        alert('Please select a description');
        return false;
    }
    if ($("#row-workgroup").val() === '') {
        alert('Please select a leader workgroup');
        return false;
    }

    return true;
};

jlab.srm.edit = function () {
    if (!jlab.srm.validateRowForm()) {
        return;
    }

    var groupId = $(".editable-row-table tbody tr.selected-row").attr("data-group-id"),
        name = $("#row-name").val(),
        description = $("#row-description").val(),
        workgroupId = $("#row-workgroup").val(),
        url = jlab.contextPath + "/setup/ajax/edit-group",
        data = {groupId: groupId, workgroupId: workgroupId, name: name, description: description},
        $dialog = $("#table-row-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
};

jlab.srm.add = function () {
    if (!jlab.srm.validateRowForm()) {
        return;
    }

    var workgroupId = $("#row-workgroup").val(),
        name = $("#row-name").val(),
        description = $("#row-description").val(),
        url = jlab.contextPath + "/setup/ajax/add-group",
        data = {workgroupId: workgroupId, name: name, description: description},
        $dialog = $("#table-row-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
};
$(document).on("click", "#remove-row-button", function () {
    jlab.srm.deleteRow();
});

$(document).on("click", "#open-edit-row-dialog-button", function () {
    var $selectedRow = $(".editable-row-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    /*var groupId = $selectedRow.attr("data-group-id"),*/
    var name = $selectedRow.find("td:nth-child(1)").text(),
        description = $selectedRow.find("td:nth-child(2)").text(),
        workgroupId = $selectedRow.attr("data-workgroup-id");

    $("#row-name").val(name);
    $("#row-description").val(description);
    $("#row-workgroup").val(workgroupId);
});

$(document).on("table-row-add", function () {
    jlab.srm.add();
});

$(document).on("table-row-edit", function () {
    jlab.srm.edit();
});

$(function () {
    jlab.srm.initDialogs();
});