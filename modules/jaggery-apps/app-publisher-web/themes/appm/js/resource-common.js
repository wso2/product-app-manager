function updateVisuals() {
    var anonymous = false;

    $(".anonymous_checkbox").each(function (index) {
        if ($(this).is(':checked')) {
            anonymous = true;
        }
    });

    if(anonymous) {
        var control_visibility = $(".controll_visibility");
        control_visibility.prop('disabled', true);
        control_visibility.parent().parent().hide();
    } else {
        var control_visibility = $(".controll_visibility");
        control_visibility.prop('disabled', false);
        control_visibility.parent().parent().show();
    }
}

function updateSubscriptionVisuals() {
    var restricted = $('.controll_visibility').is(":checked");
    var anonymous = $('.anonymous_checkbox').is(":checked");

    if (restricted || anonymous) {
        $('#sub-group').hide();
    } else {
        $("#tenant-list").hide();
        $('#sub-group').show();
    }

    $('#sub-availability').val('current_tenant');
}

$("#sub-availability").change(function () {
    var selected = this.value;
    if (selected == 'specific_tenants') {
        $("#tenant-list").show();
    } else {
        // $("#tenant-list").val("");
        $("#tenant-list").hide();
    }
});

$(".anonymous_checkbox").click(function () {
    updateSubscriptionVisuals();
});

$(".controll_visibility").click(function () {
    updateSubscriptionVisuals();
});

