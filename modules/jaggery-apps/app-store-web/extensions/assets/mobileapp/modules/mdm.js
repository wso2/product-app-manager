var module = (function () {

    return {

        formatDevicesList: function (devices) {

            var formattedDevices =[];

            devices.forEach(function(device) {
                formattedDevice = {};
                formattedDevice.name = device.name;
                formattedDevice.id = device.deviceIdentifier;
                formattedDevice.platform = device.type;

                device.properties.forEach(function(property) {
                    if(property.name === "osVersion") formattedDevice.platform_version = property.value;
                    if(property.name === "model") formattedDevice.model = property.value;
                    if(property.name === "image") formattedDevice.image = property.value;
                });

                formattedDevices.push(formattedDevice);
            });

            return formattedDevices;
        }

    };

})();