var jlab = jlab || {};
jlab.srm = jlab.srm || {};

jlab.srm.ondemand = function (type) {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var groupId = $(this).attr("data-group-id");

    window.console && console.log("groupId: " + groupId);

    var request = jQuery.ajax({
        url: jlab.contextPath + "/setup/ajax/email-on-demand",
        type: "POST",
        data: {
            groupId: groupId,
            type: type
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to send email: ' + $(".reason", data).html());
        } else {
            alert('Email sent');
        }

    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to send email: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to send email');
    });

    request.always(function () {
        jlab.requestEnd();
    });
};

$(document).on("click", ".on-demand-button", function () {
    var name = $(this).closest("tr").find("td:nth-child(1)").text();
    if (confirm('Are you sure you want to send email to group ' + name + '?')) {
        jlab.srm.ondemand.call(this, 'group');
    }
});

$(document).on("click", "#send-all-button", function () {
    if (confirm('Are you sure you want to send email to all groups?')) {
        jlab.srm.ondemand.call(this, 'all');
    }
});

$(document).on("click", "#activity-report-on-demand-button", function () {
    if (confirm('Are you sure you want to send activity email?')) {
        jlab.srm.ondemand.call(this, 'activity');
    }
});