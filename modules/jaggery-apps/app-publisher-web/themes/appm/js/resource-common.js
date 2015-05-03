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
