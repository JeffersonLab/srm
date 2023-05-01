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
        systemId = $tr.attr("data-system-id"),
        applicationId = $td.attr("data-application-id"),
        checked = $td.html() !== null && ($.trim($td.html()) !== '');

    $td.html("<span class=\"button-indicator\"></span>");

    window.console && console.log("systemId: " + systemId + ", applicationId: " + applicationId);

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/toggle-system-participation",
        type: "POST",
        data: {
            systemId: systemId,
            applicationId: applicationId
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

$(document).on("click", ".system-participation-table.editable td", function () {
    jlab.srm.toggle.call(this);
});