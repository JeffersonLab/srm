var jlab = jlab || {};
jlab.hco = jlab.hco || {};

jlab.hco.filterSystemListByCategory = function (categoryId, systemSelectSelector, applicationId, keephidden, multiselect) {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    systemSelectSelector = typeof systemSelectSelector !== 'undefined' ? systemSelectSelector : '#system-select';
    applicationId = typeof applicationId !== 'undefined' ? applicationId : 1;

    jlab.requestStart();

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/filter-system-list-by-category",
        type: "GET",
        data: {
            categoryId: categoryId,
            applicationId: applicationId
        },
        dataType: "json"
    });

    request.done(function (data) {
        if (data.status !== "success") {
            alert('Unable to filter system list : ' + data.errorReason);
        } else {
            /* Success */
            var $select = $(systemSelectSelector),
                $selectedId = $select.val();
            $select.hide();
            $select.empty();
            if (!multiselect) {
                $select.append('<option></option>');
            }
            $(data.optionList).each(function () {
                $select.append('<option value="' + this.value + '">' + this.name + '</option>');
            });
            if (!keephidden) {
                $select.slideDown();
            }
            $select.val($selectedId);
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to filter system list: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to filter system list; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
    });
};

//Override from smoothness.js so we can set min size
jlab.pageDialog.minWidth = 840;
jlab.pageDialog.minHeight = 590;

jlab.dateTimeToJLabString = function (x) {
    var year = x.getFullYear(),
        month = x.getMonth(),
        day = x.getDate(),
        hour = x.getHours(),
        minute = x.getMinutes();

    return jlab.pad(day, 2) + '-' + jlab.triCharMonthNames[month] + '-' + year + ' ' + jlab.pad(hour, 2) + ':' + jlab.pad(minute, 2);
};

jlab.updateDateRange = function (start, end) {
    $("#custom-date-range-list").hide();
    $("#start").val(jlab.dateTimeToJLabString(start));
    $("#end").val(jlab.dateTimeToJLabString(end));
};

$(document).on("click", "#current-run-link", function () {
    var $select = $("#destination-select");
    $select.select2("val", $select.attr("data-current-run-id-csv").split(","));
    return false;
});

$(document).on("change", "#range", function () {
    var selected = $("#range option:selected").val();

    switch (selected) {
        case '7days':
            var start = new Date(),
                end = new Date();

            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(7);

            start.setTime(end.getTime());
            start.setDate(start.getDate() - 7);

            jlab.updateDateRange(start, end);
            break;
        case '3days':
            var start = new Date(),
                end = new Date();

            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(7);

            start.setTime(end.getTime());
            start.setDate(start.getDate() - 3);

            jlab.updateDateRange(start, end);
            break;
        case '1day':
            var start = new Date(),
                end = new Date();

            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(7);

            start.setTime(end.getTime());
            start.setDate(start.getDate() - 1);

            jlab.updateDateRange(start, end);
            break;
        case 'custom':
            $("#custom-date-range-list").show();
            break;
    }
});

$(function () {
    $(".username-autocomplete").autocomplete({
        minLength: 2,
        source: function (request, response) {
            $.ajax({
                data: {
                    term: request.term,
                    max: 10
                },
                url: jlab.contextPath + '/ajax/search-user',
                success: function (json) {
                    response($.map(json.records, function (item) {
                        return {
                            id: item.id,
                            label: item.last + ', ' + item.first + ' (' + item.username + ')',
                            value: item.value,
                            first: item.first,
                            last: item.last
                        };
                    }));

                    if (json.total_records > 10) {
                        $(".ui-autocomplete").append($("<li class=\"plus-more\">Plus " + jlab.integerWithCommas(json.total_records - 10) + " more...</li>"));
                    }
                }
            });
        },
        select: function (event, ui) {
            $(".username-autocomplete").attr("data-user-id", ui.item.id);
            $(".username-autocomplete").attr("data-first", ui.item.first);
            $(".username-autocomplete").attr("data-last", ui.item.last);
        }
    });

    $(".component-autocomplete").each(function () {
        var $input = $(this);

        $input.autocomplete({
            minLength: 2,
            source: function (request, response) {
                $.ajax({
                    data: {
                        q: request.term,
                        max: 10,
                        application_id: $input.attr("data-application-id").trim() || ''
                    },
                    url: jlab.contextPath + '/data/components',
                    success: function (json) {
                        response($.map(json.data, function (item) {

                            var label = item.name;

                            if(item.alias) {
                                label = label + ' (' + item.alias + ')';
                            }

                            return {
                                id: item.id,
                                label: label,
                                value: item.name
                            };
                        }));

                        if (json.total_records > 10) {
                            $(".ui-autocomplete").append($("<li class=\"plus-more\">Plus " + jlab.integerWithCommas(json.total_records - 10) + " more...</li>"));
                        }
                    }
                });
            },
            select: function (event, ui) {
                $(".component-autocomplete").attr("data-component-id", ui.item.id);
            }
        });
    });

    $(".quick-autocomplete").autocomplete({
        select: function (event, ui) {
            $(event.target).val(ui.item.value);
            $(this).closest("form").submit();
        }
    });

    /*Custom time picker*/
    var myControl = {
        create: function (tp_inst, obj, unit, val, min, max, step) {
            $('<input class="ui-timepicker-input" value="' + val + '" style="width:50%">')
                .appendTo(obj)
                .spinner({
                    min: min,
                    max: max,
                    step: step,
                    change: function (e, ui) { // key events
                        // don't call if api was used and not key press
                        if (e.originalEvent !== undefined)
                            tp_inst._onTimeChange();
                        tp_inst._onSelectHandler();
                    },
                    spin: function (e, ui) { // spin events
                        tp_inst.control.value(tp_inst, obj, unit, ui.value);
                        tp_inst._onTimeChange();
                        tp_inst._onSelectHandler();
                    }
                });
            return obj;
        },
        options: function (tp_inst, obj, unit, opts, val) {
            if (typeof (opts) === 'string' && val !== undefined)
                return obj.find('.ui-timepicker-input').spinner(opts, val);
            return obj.find('.ui-timepicker-input').spinner(opts);
        },
        value: function (tp_inst, obj, unit, val) {
            if (val !== undefined)
                return obj.find('.ui-timepicker-input').spinner('value', val);
            return obj.find('.ui-timepicker-input').spinner('value');
        }
    };

    $(".date-time-field").datetimepicker({
        dateFormat: 'dd-M-yy',
        controlType: myControl,
        timeFormat: 'HH:mm'
    }).mask("99-aaa-9999 99:99", {placeholder: " "});
});