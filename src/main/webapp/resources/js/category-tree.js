var jlab = jlab || {};
jlab.hco = jlab.hco || {};

jlab.hco.validateNodeForm = function () {
    if ($("#category-parent").val() === '') {
        alert('Please select a parent category');
        return false;
    }

    if ($("#node-name").val() === '') {
        alert('Please specify a name');
        return false;
    }

    return true;
};

jlab.hco.addCategory = function () {

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    if (!jlab.hco.validateNodeForm()) {
        return;
    }

    jlab.requestStart();

    $("#SaveButton").html("<span class=\"button-indicator\"></span>");
    $("#SaveButton").attr("disabled", "disabled");

    var parentId = $("#category-parent").val(),
        name = $("#node-name").val();

    var leaveSpinning = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/add-category",
        type: "POST",
        data: {
            parentId: parentId,
            name: name
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to add category: ' + $(".reason", data).html());
        } else {
            /* Success */
            leaveSpinning = true;
            document.location.reload(true);
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to add category: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to add category; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (!leaveSpinning) {
            $("#SaveButton").html("Save");
            $("#SaveButton").removeAttr("disabled");
        }
    });
};

jlab.hco.editCategory = function () {

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    if (!jlab.hco.validateNodeForm()) {
        return;
    }

    var categoryId = $("#category").val();

    if (categoryId === '') {
        alert('Please select a category to edit');
        return;
    }

    jlab.requestStart();

    $("#SaveButton").html("<span class=\"button-indicator\"></span>");
    $("#SaveButton").attr("disabled", "disabled");

    var parentId = $("#category-parent").val(),
        name = $("#node-name").val();

    var leaveSpinning = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/edit-category",
        type: "POST",
        data: {
            categoryId: categoryId,
            parentId: parentId,
            name: name
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to edit category: ' + $(".reason", data).html());
        } else {
            /* Success */
            leaveSpinning = true;
            document.location.reload(true);
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to edit category: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to edit category; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (!leaveSpinning) {
            $("#SaveButton").html("Save");
            $("#SaveButton").removeAttr("disabled");
        }
    });
};

jlab.hco.removeCategory = function () {

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    var categoryId = $("#category").val();

    if (categoryId === '') {
        alert('Please select a category to remove');
        return;
    }

    if (!confirm('Are you sure? All subsystems and components belonging to this category will be removed as well.')) {
        return;
    }

    jlab.requestStart();

    $("#SaveButton").html("<span class=\"button-indicator\"></span>");
    $("#SaveButton").attr("disabled", "disabled");

    var leaveSpinning = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/remove-category",
        type: "POST",
        data: {
            categoryId: categoryId
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to remove category: ' + $(".reason", data).html());
        } else {
            /* Success */
            leaveSpinning = true;
            document.location.reload(true);
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to remove category: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to remove category; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (!leaveSpinning) {
            $("#SaveButton").html("Save");
            $("#SaveButton").removeAttr("disabled");
        }
    });
};

jlab.hco.addSystem = function () {

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    if (!jlab.hco.validateNodeForm()) {
        return;
    }

    jlab.requestStart();

    $("#SaveButton").html("<span class=\"button-indicator\"></span>");
    $("#SaveButton").attr("disabled", "disabled");

    var parentId = $("#category-parent").val(),
        name = $("#node-name").val();

    var leaveSpinning = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/add-system",
        type: "POST",
        data: {
            parentId: parentId,
            name: name
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to add subsystem: ' + $(".reason", data).html());
        } else {
            /* Success */
            leaveSpinning = true;
            document.location.reload(true);
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to add subsystem: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to add subsystem; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (!leaveSpinning) {
            $("#SaveButton").html("Save");
            $("#SaveButton").removeAttr("disabled");
        }
    });
};

jlab.hco.editSystem = function () {

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    if (!jlab.hco.validateNodeForm()) {
        return;
    }

    var systemId = $("#system").val();

    if (systemId === '') {
        alert('Please select a system to edit');
        return;
    }

    jlab.requestStart();

    $("#SaveButton").html("<span class=\"button-indicator\"></span>");
    $("#SaveButton").attr("disabled", "disabled");

    var parentId = $("#category-parent").val(),
        name = $("#node-name").val();

    var leaveSpinning = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/edit-system",
        type: "POST",
        data: {
            systemId: systemId,
            parentId: parentId,
            name: name
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to edit subsystem: ' + $(".reason", data).html());
        } else {
            /* Success */
            leaveSpinning = true;
            document.location.reload(true);
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to edit subsystem: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to edit subsystem; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (!leaveSpinning) {
            $("#SaveButton").html("Save");
            $("#SaveButton").removeAttr("disabled");
        }
    });
};

jlab.hco.removeSystem = function () {

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    var systemId = $("#system").val();

    if (systemId === '') {
        alert('Please select a system to remove');
        return;
    }

    if (!confirm('Are you sure?  All components belonging to this system will be removed as well.')) {
        return;
    }

    jlab.requestStart();

    $("#SaveButton").html("<span class=\"button-indicator\"></span>");
    $("#SaveButton").attr("disabled", "disabled");

    var leaveSpinning = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/remove-system",
        type: "POST",
        data: {
            systemId: systemId
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to remove subsystem: ' + $(".reason", data).html());
        } else {
            /* Success */
            leaveSpinning = true;
            document.location.reload(true);
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to remove subsystem: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to remove subsystem; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (!leaveSpinning) {
            $("#SaveButton").html("Save");
            $("#SaveButton").removeAttr("disabled");
        }
    });
};

jlab.hco.renameRoot = function () {

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    var name = $("#root-name").val();

    if (name === '') {
        alert('Please select a name');
        return;
    }

    jlab.requestStart();

    $("#SaveButton").html("<span class=\"button-indicator\"></span>");
    $("#SaveButton").attr("disabled", "disabled");

    var leaveSpinning = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/rename-root",
        type: "POST",
        data: {
            name: name
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to rename root: ' + $(".reason", data).html());
        } else {
            /* Success */
            leaveSpinning = true;
            document.location.reload(true);
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to rename root: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to rename root; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (!leaveSpinning) {
            $("#SaveButton").html("Save");
            $("#SaveButton").removeAttr("disabled");
        }
    });
};

jlab.hco.clearAndOpenNodeDialog = function () {
    $("#node-dialog form").get(0).reset();
    $("#node-dialog").dialog("open");
};

$(document).on("click", "#SaveButton", function () {
    var title = $("#node-dialog").dialog("option", "title");
    if (title === 'Add Subsystem') {
        jlab.hco.addSystem();
    } else if (title === 'Add Category') {
        jlab.hco.addCategory();
    } else if (title === 'Remove Category') {
        jlab.hco.removeCategory();
    } else if (title === 'Remove Subsystem') {
        jlab.hco.removeSystem();
    } else if (title === 'Edit Category') {
        jlab.hco.editCategory();
    } else if (title === 'Edit Subsystem') {
        jlab.hco.editSystem();
    }
});

$(document).on("click", "#open-add-category-dialog-button", function () {
    $("#node-dialog").dialog("option", "title", "Add Category");
    $("#select-node-fieldset").hide();
    $("#new-value-fieldset").show();
    jlab.hco.clearAndOpenNodeDialog();
});

$(document).on("click", "#open-edit-category-dialog-button", function () {
    $("#node-dialog").dialog("option", "title", "Edit Category");
    $("#select-node-fieldset").show();
    $("#category-node-select").show();
    $("#system-node-select").hide();
    $("#new-value-fieldset").show();
    jlab.hco.clearAndOpenNodeDialog();
});

$(document).on("click", "#open-remove-category-dialog-button", function () {
    $("#node-dialog").dialog("option", "title", "Remove Category");
    $("#select-node-fieldset").show();
    $("#category-node-select").show();
    $("#system-node-select").hide();
    $("#new-value-fieldset").hide();
    jlab.hco.clearAndOpenNodeDialog();
});

$(document).on("click", "#open-add-system-dialog-button", function () {
    $("#node-dialog").dialog("option", "title", "Add Subsystem");
    $("#select-node-fieldset").hide();
    $("#new-value-fieldset").show();
    jlab.hco.clearAndOpenNodeDialog();
});

$(document).on("click", "#open-edit-system-dialog-button", function () {
    $("#node-dialog").dialog("option", "title", "Edit Subsystem");
    $("#select-node-fieldset").show();
    $("#category-node-select").hide();
    $("#system-node-select").show();
    $("#new-value-fieldset").show();
    jlab.hco.clearAndOpenNodeDialog();
});

$(document).on("click", "#open-remove-system-dialog-button", function () {
    $("#node-dialog").dialog("option", "title", "Remove Subsystem");
    $("#select-node-fieldset").show();
    $("#category-node-select").hide();
    $("#system-node-select").show();
    $("#new-value-fieldset").hide();
    jlab.hco.clearAndOpenNodeDialog();
});

$(document).on("change", "#category", function () {
    var currentCategoryName = $("#category option:selected").text().trim();

    var currentParentCategoryName = $("#tree li").filter(function () {
        return $(this).find("> a").text().trim() === currentCategoryName;
    }).parent().closest("li").find("> a").text().trim();

    $("#category-parent option:selected").prop("selected", false);

    $("#category-parent option").filter(function () {
        return $(this).text().trim() === currentParentCategoryName;
    }).prop("selected", true);
    $("#node-name").val($("#category option:selected").text().trim());
});

$(document).on("change", "#system", function () {
    var currentParentCategoryName = $("#system option:selected").attr("data-category");
    $("#category-parent option").filter(function () {
        return $(this).text().trim() === currentParentCategoryName;
    }).prop("selected", true);
    $("#node-name").val($("#system option:selected").text().trim());
});

$(document).on("click", "#open-edit-root-dialog-button", function () {
    $("#root-name").val($("#node-CATEGORY-0_anchor").text());
    $("#root-dialog").dialog("open");
});

$(document).on("click", "#root-save-button", function () {
    jlab.hco.renameRoot();
});

$(function () {
    $(".dialog").dialog({
        autoOpen: false,
        width: 600,
        height: 400,
        modal: true
    });


    $("#tree").jstree({
        core: {
            multiple: false,
            themes: {
                theme: "classic",
                dots: true,
                icons: true
            }
        },
        state: {key: 'setup'},
        types: {
            "#": {
                "max_children": 1
            },
            "CATEGORY": {
                "icon": "../resources/img/category.png"
            },
            "SYSTEM": {
                "icon": "../resources/img/system.png"
            },
            "COMPONENT": {
                "icon": "../resources/img/component.png"
            },
            "GROUP": {
                "icon": "../resources/img/group.png"

            },
            "default": {
                "icon": "../resources/img/file.png"
            }
        },
        plugins: ["types", "state", "conditionalselect"],
        conditionalselect: function (node) {
            return false;
        }
    });
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
})(jQuery);