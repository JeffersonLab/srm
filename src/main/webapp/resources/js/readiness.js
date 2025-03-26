var jlab = jlab || {};
jlab.srm = jlab.srm || {};

jlab.srm.initTrees = function () {

    var mydata = {
        categoryId: $("#category-select option:selected").val(),
        systemId: $("#system-select option:selected").val(),
        regionId: $("#region-select option:selected").val(),
        groupId: $("#group-select option:selected").val()
    };

    var destinationIdArray = [];

    $("#destination-select option:selected").each(function () {
        destinationIdArray.push($(this).val());
    });

    mydata['destinationId'] = destinationIdArray;

    var statusIdArray = [];

    $("#status-select option:selected").each(function () {
        statusIdArray.push($(this).val());
    });

    mydata['statusId'] = statusIdArray;

    $(".tree").jstree({
        core: {
            themes: {
                theme: "classic",
                dots: true,
                icons: true
            },
            data: {
                url: "get-children",
                traditional: true,
                data: function (node) {
                    mydata['id'] = node.id;
                    return mydata;
                }
            }
        },
        types: {
            "#": {
                "max_children": 1
            },
            "CATEGORY": {
                "icon": "resources/img/category.png"
            },
            "SYSTEM": {
                "icon": "resources/img/system.png"
            },
            "COMPONENT": {
                "icon": "resources/img/component.png"
            },
            "GROUP": {
                "icon": "resources/img/group.png"

            },
            "default": {
                "icon": "resources/img/file.png"
            }
        },
        plugins: ["types", "state", "conditionalselect"],
        conditionalselect: function (node) {
            return false;
        }
    });
};

jlab.srm.initDialogs = function () {
    $("#group-dialog").dialog({
        autoOpen: false,
        modal: true,
        resizable: false,
        draggable: false,
        width: 640,
        height: 480
    });
    $("#ops-pr-dialog").dialog({
        autoOpen: false,
        width: 300,
        height: 200,
        modal: true,
        resizable: false,
        close: function () {
            document.location.reload(true);
        }
    });
};

jlab.srm.init = function () {
    jlab.srm.initTrees();
    jlab.srm.initDialogs();
};

jlab.srm.handleGroupNodeClick = function () {
    if ($("#username-container").length < 1) {
        document.location.href = $("#auth").find("a").attr("href");
        return;
    }

    $("#group-dialog").find(".component-name").text($.trim($(this).closest('li[data-node-type="COMPONENT"]').children("a").text()));
    $("#group-dialog").find(".group-name").text($.trim($(this).text()));
    $("#group-dialog").attr("data-component-id", $(this).closest('li[data-node-type="COMPONENT"]').attr("data-node-id"));
    $("#group-dialog").attr("data-group-id", $(this).closest('li[data-node-type="GROUP"]').attr("data-node-id"));
    $("#group-dialog").dialog("open");
};

jlab.srm.validateUpdateForm = function () {
    if ($("#update-status-select").val() === '') {
        alert('Please select a status');
        return false;
    }

    return true;
};

jlab.srm.updateSignoff = function () {

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
        componentIdArray = null,
        groupIdArray = null,
        statusId = null,
        comment = null,
        needsAttention;

    componentIdArray = [];
    groupIdArray = [];

    componentId = $("#group-dialog").attr("data-component-id");
    groupId = $("#group-dialog").attr("data-group-id");
    componentIdArray.push(componentId);
    groupIdArray.push(groupId);

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
            needsAttention: needsAttention
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
                document.location.reload(true);
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

$(document).on("click", "#updateButton", function () {
    jlab.srm.updateSignoff();
});

$(document).on("click", '.jstree-node[data-node-type="GROUP"] > a', function () {
    jlab.srm.handleGroupNodeClick.call(this);

    return false; /*In jQuery this prevents default and stops propagation*/
});


$(document).on("click", ".default-clear-panel", function () {
    $("#destination-select").val(null).trigger('change');
    $("#category-select").val('');
    $("#system-select").val('');
    $("#region-select").val('');
    $("#group-select").val('');
    $("#status-select").val(null).trigger('change');
    return false;
});

$(".tree").on("open_node.jstree", function (node) {
    $('.jstree-node[data-node-type="COMPONENT"] > a').addClass("dialog-opener").attr("data-dialog-type", "component").each(function () {
        var nodeId = $(this).closest("li").attr("data-node-id");
        $(this).attr("href", "reports/component/detail?componentId=" + parseInt(nodeId));
        $(this).attr("data-dialog-title", "Component Information: " + String($(this).text()).encodeXml());
    });
});

expandAll = function () {
    /*$(".tree").jstree('open_all', '.jstree-node[data-node-type="CATEGORY"]');*/
    $('.jstree-closed[data-node-type="CATEGORY"], .jstree-closed[data-node-type="SYSTEM"]').each(function () {
        var $node = $(this),
            nodeId = $node[0].id;
        $(".tree").jstree('open_node', $node, function () {
            expandCatNSys(nodeId);
        });
    });
};

expandCatNSys = function (nodeId) {
    var $node = $("#" + nodeId);
    $('.jstree-closed[data-node-type="CATEGORY"], .jstree-closed[data-node-type="SYSTEM"]', $node).each(function () {
        var $n = $(this),
            nId = $n[0].id;
        $(".tree").jstree('open_node', $n, function () {
            expandCatNSys(nId);
        });
    });
};

$(document).on("click", "#expand-all-button", function () {
    expandAll();
});

$(document).on("click", "#collapse-all-button", function () {
    $(".tree").jstree('close_all');
});

$(document).on("change", "#category-select", function () {
    var categoryId = $(this).val();
    jlab.srm.filterSystemListByCategory(categoryId);
});

$(".tree").on("state_ready.jstree", function (e, data) {
    if ($("#expand-all-button").attr("data-count") <= 100) {
        expandAll();
    }
});

$(function () {
    jlab.srm.init();
});

$(document).on("click", "#system-excel-menu-item", function () {
    $("#excel").click();
});

$(document).on("click", "#ops-pr-ok-button", function () {
    $("#ops-pr-dialog").dialog("close");
});

// conditional select
(function ($, undefined) {
    "use strict";
    $.jstree.defaults.conditionalselect = function () {
        return true;
    };
    $.jstree.plugins.conditionalselect = function (options, parent) {
// own function
        this.activate_node = function (obj, e) {
            if (this.settings.conditionalselect.call(this, this.get_node(obj))) {
                parent.activate_node.call(this, obj, e);
            }
        };
    };
    $("#destination-select").select2({
        width: 390
    });

    $("#status-select").select2({
        width: 390
    });

    $("#destination-select").closest(".li-value").css("visibility", "visible");

    $("#status-select").closest(".li-value").css("visibility", "visible");
})(jQuery);

