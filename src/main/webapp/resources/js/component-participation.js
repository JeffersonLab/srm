var jlab = jlab || {};
jlab.srm = jlab.srm || {};

jlab.srm.check = function ($td) {
    $td.html("\u2714");
};

jlab.srm.uncheck = function ($td) {
    $td.empty();
};

jlab.srm.restore = function ($td, checked) {
    if (checked) {
        jlab.srm.check($td);
    } else {
        jlab.srm.uncheck($td);
    }
};

jlab.srm.toggle = function () {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var $td = $(this),
        $tr = $td.closest("tr"),
        componentId = $tr.attr("data-component-id"),
        destinationId = $td.attr("data-destination-id"),
        checked = $td.html() !== null && ($.trim($td.html()) !== '');

    $td.html("<span class=\"button-indicator\"></span>");

    //window.console && console.log("componentId: " + componentId + ", destinationId: " + destinationId);

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/toggle-component-destination",
        type: "POST",
        data: {
            componentId: componentId,
            destinationId: destinationId
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            jlab.srm.restore($td, checked);
            alert('Unable to toggle: ' + $(".reason", data).html());
        } else {
            if (checked) {
                jlab.srm.uncheck($td);
            } else {
                jlab.srm.check($td);
            }
        }

    });

    request.fail(function (xhr, textStatus) {
        jlab.srm.restore($td, checked);
        window.console && console.log('Unable to toggle: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to toggle');
    });

    request.always(function () {
        jlab.requestEnd();
    });
};

jlab.srm.batchCheck = function () {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var $th = $(this).closest("th"),
        $tr = $th.closest("tr"),
        componentId = $tr.attr("data-component-id");

    $th.html("<span class=\"button-indicator\"></span>");

    window.console && console.log("componentId: " + componentId);

    var request = jQuery.ajax({
        url: "grid-batch-check",
        type: "POST",
        data: {
            check: 'all',
            componentId: componentId
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to check all: ' + $(".reason", data).html());
        } else {
            $tr.find("td").each(function () {
                jlab.srm.grid.check($(this));
            });
        }
    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to check all: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to check all');
    });

    request.always(function () {
        jlab.requestEnd();
        $th.find(".check-all").html("All");
    });
};

jlab.srm.batchUncheck = function () {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var $th = $(this).closest("th"),
        $tr = $th.closest("tr"),
        componentId = $tr.attr("data-component-id");

    $th.html("<span class=\"button-indicator\"></span>");

    window.console && console.log("componentId: " + componentId);

    var request = jQuery.ajax({
        url: "grid-batch-check",
        type: "POST",
        data: {
            check: 'none',
            componentId: componentId
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to uncheck all: ' + $(".reason", data).html());
        } else {
            $tr.find("td").each(function () {
                jlab.srm.uncheck($(this));
            });
        }
    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to uncheck all: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to uncheck all');
    });

    request.always(function () {
        jlab.requestEnd();
        $th.find(".check-none").html("None");
    });
};

jlab.srm.copy = function () {

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    var $th = $(this).closest("th"),
        $toTr = $th.closest("tr"),
        $fromTr = $('input[name=copy-source]:checked', '.component-destination-table').closest("tr"),
        $fromTdArray = $fromTr.find("td"),
        toComponentId = $toTr.attr("data-component-id"),
        fromComponentId = $fromTr.attr("data-component-id");

    window.console && console.log("toComponentId: " + toComponentId + ", fromComponentId: " + fromComponentId);

    if (!fromComponentId) {
        alert("Please select a copy source row");
        return;
    }

    if (toComponentId === fromComponentId) {
        return;
    }

    jlab.requestStart();

    $th.html("<span class=\"button-indicator\"></span>");

    var request = jQuery.ajax({
        url: "grid-copy",
        type: "POST",
        data: {
            toComponentId: toComponentId,
            fromComponentId: fromComponentId
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to copy: ' + $(".reason", data).html());
        } else {
            $toTr.find("td").each(function (index) {
                $(this).html($($fromTdArray[index]).html());
            });
        }
    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to copy: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to copy');
    });

    request.always(function () {
        jlab.requestEnd();
        $th.find(".paste-button").html("Paste");
    });
};

jlab.srm.deleteRow = function () {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    var $th = $(this).closest("th"),
        $tr = $th.closest("tr"),
        componentId = $tr.attr("data-component-id"),
        componentName = $tr.find("th").filter(":first").html();
    if (confirm("Are you sure you want to delete the component: " + componentName)) {
        window.console && console.log("componentId: " + componentId);

        jlab.requestStart();

        $th.html("<span class=\"button-indicator\"></span>");

        var request = jQuery.ajax({
            url: "grid-delete",
            type: "POST",
            data: {
                componentId: componentId
            },
            dataType: "html"
        });

        request.done(function (data) {
            if ($(".status", data).html() !== "Success") {
                alert('Unable to delete: ' + $(".reason", data).html());
            } else {
                $tr.remove();
            }
        });

        request.fail(function (xhr, textStatus) {
            window.console && console.log('Unable to delete: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
            alert('Unable to delete');
        });

        request.always(function () {
            jlab.requestEnd();
            $th.find(".delete-button").html("Delete");
        });
    }
};

jlab.srm.addComponent = function () {
    var $form = $("#add-component-form");
    $form.append($('<input type="hidden" name="systemId"/>').attr("value", $("#system-select option:selected").val()));
    $form.submit();
};

jlab.srm.paginator = function (offsetParam, maxPerPageParam, totalRecordsParam) {
    var offset = offsetParam,
        maxPerPage = maxPerPageParam,
        totalRecords = totalRecordsParam;

    this.getOffset = function () {
        return offset;
    };

    this.getMaxPerPage = function () {
        return maxPerPage;
    };

    this.getTotalRecords = function () {
        return totalRecords;
    };

    this.getStartNumber = function () {
        var startNumber = offset + 1;

        if (startNumber > totalRecords) {
            startNumber = totalRecords;
        }

        return startNumber;
    };

    this.getEndNumber = function () {
        var endNumber = offset + maxPerPage;

        if (endNumber > totalRecords) {
            endNumber = totalRecords;
        }

        return endNumber;
    };

    this.isPrevious = function () {
        var previous = false;

        if (offset > 0) {
            previous = true;
        }

        return previous;
    };

    this.isNext = function () {
        var next = false;

        if (totalRecords > offset + maxPerPage) {
            next = true;
        }

        return next;
    };

    this.getPreviousOffset = function () {
        var previousOffset = offset - maxPerPage;

        if (previousOffset < 0) {
            previousOffset = 0;
        }

        return previousOffset;
    };

    this.getNextOffset = function () {
        var nextOffset = offset + maxPerPage;

        if (nextOffset > (totalRecords - 1)) {
            nextOffset = totalRecords - 1;
        }

        return nextOffset;
    };

    this.hideAll = function () {
        $(".component-destination-table td, .component-destination-table th").hide();
    };

    this.showFirstColumn = function () {
        $(".component-destination-table th:nth-child(1)").show();
    };

    this.updateDisplay = function () {
        this.hideAll();
        this.showFirstColumn();

        var start = offset + 2, end = this.getEndNumber() + 2;

        for (var i = start; i < end; i++) {
            $(".component-destination-table td:nth-child(" + i + "), .component-destination-table th:nth-child(" + i + ")").show();
        }

        $("#destinationSelectionMessage").text("Showing Beam Destinations " + this.getStartNumber() + " - " + this.getEndNumber() + " of " + this.getTotalRecords());
    };

    this.next = function () {
        offset = this.getNextOffset();
        this.updateDisplay();
    };

    this.previous = function () {
        offset = this.getPreviousOffset();
        this.updateDisplay();
    };

    this.updateButtonState = function () {
        if (jlab.srm.horizontalPaginator.isNext()) {
            $("#horizontal-next-button").removeAttr("disabled");
        } else {
            $("#horizontal-next-button").attr("disabled", "disabled");
        }

        if (jlab.srm.horizontalPaginator.isPrevious()) {
            $("#horizontal-previous-button").removeAttr("disabled");
        } else {
            $("#horizontal-previous-button").attr("disabled", "disabled");
        }
    };
};

$(document).on("click", "#horizontal-next-button", function () {
    jlab.srm.horizontalPaginator.next();

    jlab.srm.horizontalPaginator.updateButtonState();
});

$(document).on("click", "#horizontal-previous-button", function () {
    jlab.srm.horizontalPaginator.previous();

    jlab.srm.horizontalPaginator.updateButtonState();
});

$(document).on("click", ".component-destination-table.editable td", function () {
    jlab.srm.toggle.call(this);
});

$(document).on("change", "#category-select", function () {
    $("#category-form").submit();
});

$(document).on("click", ".check-all", function () {
    jlab.srm.batchCheck.call(this);
});

$(document).on("click", ".check-none", function () {
    jlab.srm.batchUncheck.call(this);
});

$(document).on("click", ".paste-button", function () {
    jlab.srm.copy.call(this);
});

$(document).on("click", ".delete-button", function () {
    jlab.srm.deleteRow.call(this);
});

$(document).on("click", ".add-button", function () {
    jlab.srm.addComponent();
});

$(document).on("change", "#vertical-record-max-selector", function () {
    $("#max-input").val($(this).val());
    $(".filter-form").submit();
});
$(document).on("click", ".default-clear-panel", function () {
    $("#destination-select").val('');
    $("#category-select").val('').trigger('change');
    $("#system-select").val('');
    $("#region-select").val('');
    $("#component").val('');
    $("#col-destination-select").val(null).trigger('change');
    return false;
});
$(document).on("change", "#category-select", function () {
    var categoryId = $(this).val();
    jlab.srm.filterSystemListByCategory(categoryId, "#system-select", "");
});
$(function () {
    $("#col-destination-select").select2({
        width: 300
    });

    var offset = 0,
        maxPerPage = 5,
        totalRecords = $(".component-destination-table thead th").length - 1;

    jlab.srm.horizontalPaginator = new jlab.srm.paginator(offset, maxPerPage, totalRecords);

    jlab.srm.horizontalPaginator.updateDisplay();

    jlab.srm.horizontalPaginator.updateButtonState();
});