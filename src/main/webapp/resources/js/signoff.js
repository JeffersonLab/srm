var jlab = jlab || {};
jlab.srm = jlab.srm || {};

jlab.srm.updateSignoffCount = function () {
    var numSelected = $("#signoff-table").find(".ui-selected").length;
    $("#selected-count").text(numSelected);
    if (numSelected === 0) {
        /*$("#open-edit-dialog-button").prop("disabled", true);
         $("#open-request-dialog-button").prop("disabled", true);
         $("#unselect-button").prop("disabled", true);*/

        $("#open-edit-dialog-button").button("option", "disabled", true);
        $("#open-request-dialog-button").button("option", "disabled", true);
        $("#unselect-button").button("option", "disabled", true);
    } else {
        /*$("#open-edit-dialog-button").prop("disabled", false);
         $("#open-request-dialog-button").prop("disabled", false);
         $("#unselect-button").prop("disabled", false);*/

        $("#open-edit-dialog-button").button("option", "disabled", false);
        $("#open-request-dialog-button").button("option", "disabled", false);
        $("#unselect-button").button("option", "disabled", false);
    }
};

jlab.srm.unselect = function () {
    $("#signoff-table .ui-selected").removeClass("ui-selected");
    jlab.srm.updateSignoffCount();
};

jlab.srm.ctrlSelectColumn = function (table, column) {
    $(table).find("tbody tr").each(function () {
        var td = $(this).find("td:nth-child(" + column + ")");
        td.addClass("ui-selected");
    });
};

jlab.srm.selectColumn = function (table, column) {
    $(".ui-selected", table).removeClass("ui-selected");
    jlab.srm.ctrlSelectColumn(table, column);
};

jlab.srm.validateUpdateForm = function () {
    if (Number($("#selected-count").text()) < 1) {
        alert('Please select at least one signoff');
        return false;
    }

    if ($("#update-status-select").val() === '') {
        alert('Please select a status');
        return false;
    }

    return true;
};

jlab.srm.validateRequestMaskingForm = function () {
    if (Number($("#selected-count").text()) < 1) {
        alert('Please select at least one component');
        return false;
    }

    if ($("#request-comment").val() === '') {
        alert('Please provide a reason');
        return false;
    }

    if ($("#mask-expiration").val() === '') {
        alert('Please provide a mask expiration');
        return false;
    }

    return true;
};

jlab.srm.updateSignoffGrid = function () {

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    if (!jlab.srm.validateUpdateForm()) {
        return;
    }

    jlab.requestStart();

    $("#updateButton").html("<span class=\"button-indicator\"></span>");
    $("#updateButton").attr("disabled", "disabled");

    var componentId = null,
        groupId = null,
        count = null,
        th = null,
        componentIdArray = null,
        groupIdArray = null,
        statusId = null,
        comment = null,
        needsAttention,
        readyCascade = $("#readyCascade").val(),
        checkedCascade = $("#checkedCascade").val();

    componentIdArray = [];
    groupIdArray = [];

    $("#signoff-table .ui-selected").each(function () {
        componentId = $(this).closest("tr").find(":first-child").attr("data-component-id");
        count = $(this).index();
        th = $("table").find("thead .group-header:nth-child(" + count + ")");
        groupId = th.attr("data-group-id");
        componentIdArray.push(componentId);
        groupIdArray.push(groupId);
    });

    statusId = $("#update-status-select").val();
    comment = $("#comment").val();

    needsAttention = $("#needs-attention").prop("checked");

    var leaveSpinning = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/batch-signoff",
        type: "POST",
        data: {
            statusId: statusId,
            comment: comment,
            'componentId[]': componentIdArray,
            'groupId[]': groupIdArray,
            needsAttention: needsAttention,
            readyCascade: readyCascade,
            checkedCascade: checkedCascade
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to signoff: ' + $(".reason", data).html());
        } else {
            /* Success */
            leaveSpinning = true;

            var prId = $(".pr-id", data).html();
            if (prId !== 'null') {
                var url = jlab.logbookServerUrl + "/entry/" + prId;
                $("#ops-pr-link").text(prId);
                $("#ops-pr-link").attr("href", url);
                $("#ops-pr-dialog").dialog("open");
            } else {
                var cleanedUrl = window.location.href.replace("pop=true", "pop=false");
                window.location.href = cleanedUrl;
            }
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to signoff: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to signoff; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (!leaveSpinning) {
            $("#updateButton").html("Save");
            $("#updateButton").removeAttr("disabled");
        }
    });
};

jlab.srm.requestMasking = function () {

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    if (!jlab.srm.validateRequestMaskingForm()) {
        return;
    }

    jlab.requestStart();

    var $submitButton = $("#request-mask-button");

    $submitButton.height($submitButton.height()).width($submitButton.width());
    $submitButton.html("<span class=\"button-indicator\"></span>");
    $submitButton.prop("disabled", true);

    var componentId = null,
        componentIdArray = null,
        reason = $("#request-comment").val(),
        expirationDate = $("#mask-expiration").val();

    componentIdArray = [];

    $("#signoff-table .ui-selected").each(function () {
        componentId = $(this).closest("tr").find(":first-child").attr("data-component-id");
        componentIdArray.push(componentId);
    });

    var leaveSpinning = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/save-masking-request",
        type: "POST",
        data: {
            reason: reason,
            'componentId[]': componentIdArray,
            'expiration-date': expirationDate
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to create masking request: ' + $(".reason", data).html());
        } else {
            /* Success */
            leaveSpinning = true;
            /*document.location.reload(true);can't do this now that we have pop=true*/
            /*var cleanedUrl = window.location.href.replace("pop=true", "pop=false");*/
            window.location.href = jlab.contextPath + '/masks/requests?status=PENDING&qualified=';
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to create masking request: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to create masking requset; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (!leaveSpinning) {
            $submitButton.html("Save");
            $submitButton.prop("disabled", false);
        }
    });
};


jlab.srm.filterSystemListByGroup = function (groupId) {
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
            var $select = $("#system-select");
            $select.hide();
            $select.empty();
            $select.append('<option></option>');
            $(data.optionList).each(function () {
                $select.append('<option value="' + String(this.value).encodeXml() + '">' + String(this.name).encodeXml() + '</option>');
            });
            $select.slideDown();
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

jlab.srm.filterGroupList = function (systemId) {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/filter-group-list-by-system",
        type: "GET",
        data: {
            systemId: systemId
        },
        dataType: "json"
    });

    request.done(function (data) {
        if (data.status !== "success") {
            alert('Unable to filter group list : ' + data.errorReason);
        } else {
            /* Success */
            var $select = $("#group-select");
            $select.hide();
            $select.empty();
            $select.append('<option></option>');
            $(data.optionList).each(function () {
                $select.append('<option value="' + String(this.value).encodeXml() + '">' + String(this.name).encodeXml() + '</option>');
            });

            if ($(data.optionList).length === 1) {
                $select.prop('selectedIndex', 1);
            }

            $select.slideDown();
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

jlab.srm.initDialogs = function () {
    $("#signoff-dialog").dialog({
        autoOpen: false,
        width: 550,
        height: 550,
        modal: true,
        resizable: false
    });
    $("#request-dialog").dialog({
        autoOpen: false,
        width: 550,
        height: 520,
        modal: true,
        resizable: false
    });
    $("#signoff-options-dialog").dialog({
        autoOpen: false,
        width: 500,
        height: 400,
        modal: false,
        close: function () {
            $("#filter-form")[0].reset(); /*TODO: use defaultSelected on option instead of resetting entire form*/
        }
    });
    $("#ops-pr-dialog").dialog({
        autoOpen: false,
        width: 300,
        height: 200,
        modal: true,
        resizable: false,
        close: function () {
            var cleanedUrl = window.location.href.replace("pop=true", "pop=false");
            window.location.href = cleanedUrl;
        }
    });
};

jlab.srm.checkForDuplicates = function (a) {
    a.sort();

    var last = a[0],
        dups = 0;

    for (var i = 1; i < a.length; i++) {
        if (a[i] === last) {
            dups++;
        }
        last = a[i];
    }

    return dups;
};

jlab.srm.prepareEditDialog = function () {
    var componentName = null,
        groupName = null,
        count = null,
        th = null,
        componentNameArray = null,
        groupNameArray = null;

    componentNameArray = [];
    groupNameArray = [];

    $("#signoff-table .ui-selected").each(function () {
        componentName = $(this).closest("tr").find(":first-child a").text();
        count = $(this).index();
        th = $("table").find("thead .group-header:nth-child(" + count + ")");
        groupName = th.find("a").text();
        componentNameArray.push(componentName);
        groupNameArray.push(groupName);
    });

    var dups = jlab.srm.checkForDuplicates(componentNameArray);

    if (dups > 0) {
        alert('Please signoff only one group at a time');
        return;
    }

    dups = jlab.srm.checkForDuplicates(groupNameArray);

    if (dups !== groupNameArray.length - 1) {
        alert('Please signoff only one group at a time');
        return;
    }

    var $selectedList = $(".selected-component-list");

    $selectedList.empty();

    for (var i = 0; i < componentNameArray.length; i++) {
        $selectedList.append('<li>' + String(componentNameArray[i]).encodeXml() + '</li>');
    }

    $(".selected-group").text(groupNameArray[0]);

    var count = $("#selected-count").text() * 1;
    var componentStr = count === 1 ? ' Component' : ' Components';
    $(".edit-dialog-component-count").text(count + componentStr);
};

$(document).on("click", "#ops-pr-ok-button", function () {
    $("#ops-pr-dialog").dialog("close");
});
$(document).on("click", "#updateButton", function () {
    jlab.srm.updateSignoffGrid();
});

$(document).on("click", "#request-mask-button", function () {
    jlab.srm.requestMasking();
});

$(document).on("click", "#signoff-table thead th .select-column-icon", function (e) {
    var $th = $(this).closest("th");
    var index = $th.index() + 1; //rowspan hides first column
    var count = index + 1; //nth-child starts counting at 1, not 0
    var ctrlPressed = e.ctrlKey;

    if (ctrlPressed) {
        jlab.srm.ctrlSelectColumn($("#signoff-table"), count);
    } else {
        jlab.srm.selectColumn($("#signoff-table"), count);
    }

    jlab.srm.updateSignoffCount();

    return false;
});

$(document).on("click", "#unselect-button", function () {
    jlab.srm.unselect();
});

$(document).on("change", "#system-select", function () {
    if (jlab.srm.systemFirst === true) {
        var systemId = $(this).val();
        jlab.srm.filterGroupList(systemId);
    }
});

$(document).on("change", "#category-select", function () {
    if (jlab.srm.systemFirst === true) {
        var categoryId = $(this).val();
        jlab.srm.filterSystemListByCategory(categoryId);
    }
});

$(document).on("change", "#group-select", function () {
    if (jlab.srm.systemFirst !== true) {
        var groupId = $(this).val();
        jlab.srm.filterSystemListByGroup(groupId);
    }
});

$(document).on("click", "#open-request-dialog-button", function () {
    $("#request-dialog form")[0].reset();

    jlab.srm.prepareEditDialog();

    $("#request-dialog").dialog("open");
});

$(document).on("click", "#open-edit-dialog-button", function () {
    jlab.srm.prepareEditDialog();

    $("#signoff-dialog").dialog("open");
});

$(document).on("click", "#signoff-options-link", function () {
    $("#signoff-options-dialog").dialog("open");
    return false;
});

$(document).on("click", "#save-signoff-button", function () {
    var systemId = $("#system-select").val(),
        groupId = $("#group-select").val(),
        regionId = $("#region-select").val(),
        statusId = $("#status-select").val(),
        component = $("#component").val();
    window.open('/srm/setup/saved-signoff?systemId=' + systemId + '&groupId=' + groupId + '&regionId=' + regionId + '&statusId=' + statusId + '&component=' + encodeURIComponent(component) + '&pop=true');
});

$(document).on("change", "#show-comments-checkbox", function () {
    $("#filter-form").submit();
});

$(document).on("click", "#expand-icon", function () {
    $("#signoff-table").toggleClass("expanded-table");
});

$(document).on("click", ".flyout-link", function () {
    $(".flyout-handle").remove();
    var flyout = $("." + $(this).attr("data-flyout-type") + " .flyout-panel").clone();
    $(this).parent().append('<div class="flyout-handle"></div>');
    $(".flyout-handle").append(flyout);
    return false;
});
$(document).on("click", ".close-bubble", function () {
    $(".flyout-handle").hide(); /*We don't remove because of weird FF behavior on form*/
    return false;
});
$(document).on("click", "#all-systems-link-list a", function () {
    var url = $(this).attr('href'),
        groupId = $("#group-select").val(),
        url = url + '?' + 'groupId=' + parseInt(groupId);
    window.location.href = url;
    return false;
});
$(document).on("click", ".default-clear-panel", function () {
    $("#destination-select").val(null).trigger('change');
    $("#category-select").val('').trigger('change');
    $("#system-select").val('');
    $("#group-select").val('');
    $("#ready-turn-select").val('');
    $("#user").val('');
    $("#component").val('');
    $("#min-modified").val('');
    $("#max-modified").val('');
    $("#region-select").val(null).trigger('change');
    $("#status-select").val(null).trigger('change');
    return false;
});
$(document).on("click", "#exclude-na-link", function () {
    $("#status-select").val(("1,50,100").split(",")).trigger('change');
    return false;
});
$(function () {
    jlab.srm.initDialogs();

    $("#signoff-table.editable #nested-table tbody").selectable({
        filter: "td.selected-group-cell",
        cancel: "a, .select-column-icon",
        stop: function () {
            jlab.srm.updateSignoffCount();
        }
    });

    $(document).tooltip({
        items: "#signoff-table td .tooltip-icon",
        content: function () {
            var $element = $(this);
            return "<ul class=\"key-value-list\"><li><div class=\"li-key\">Comment:</div><div class=\"li-value\">" + $element.attr("data-comment").encodeXml() + "</div></li><li><div class=\"li-key\">Modified By:</div><div class=\"li-value\">" + $element.attr("data-modified-by") + "</div></li><li><div class=\"li-key\">Modified Date:</div><div class=\"li-value\">" + $element.attr("data-modified-date") + "</div></li><li><div class=\"li-key\">Change:</div><div class=\"li-value\">" + $element.attr("data-change") + "</div></li></ul>";
        }
    });

    $("#open-edit-dialog-button").button({
        icons: {
            primary: "icon-edit-button"
        }
    });

    $("#open-request-dialog-button").button({
        icons: {
            primary: "icon-masking-button"
        }
    });

    $("#unselect-button").button({
        icons: {
            primary: "icon-unselect-button"
        }
    });

    $("#region-select, #status-select").select2({
        width: 450
    });

    $("#region-select, #status-select").closest(".li-value").css("visibility", "visible");

    $("#destination-select").select2({
        width: 390
    });

    $("#destination-select").closest(".li-value").css("visibility", "visible");
});