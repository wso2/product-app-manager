/*
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


$('#update-store').click(function () {
    var provider = $("#app-provider").val();
    var appName = $("#app-name").val();
    var version = $("#app-version").val();

    var externalAPIStores = new Array();


    $("input:checkbox[name=externalStore]:checked").each(function () {
        externalAPIStores.push($(this).val());
    });


    var type = $('#meta-asset-type').val();
    var pathname = window.location.pathname;
    var head = "External Stores";
    $('#spinner').show();
    $.ajax({
        url: '/publisher/api/asset/webapp/update/external/stores/' + provider + '/' + appName + '/' + version,
        type: 'POST',
        processData: false,
        contentType: 'application/json',
        data: JSON.stringify(externalAPIStores),
        success: function (response) {
            if (response.success) {
                $('#spinner').hide();
                var msg = "Successfully updated external stores.";
                showMessageModel(msg, head, pathname);
                e.stopPropagation();
            } else {
                $('#spinner').hide();
                showMessageModel(response.message, head, pathname);
                e.stopPropagation();
            }

        },
        error: function (response) {
            showMessageModel(response.message, head, pathname);
            e.stopPropagation();
        }
    });


});


var showMessageModel = function (msg, head, pathname) {
    $('#messageModal2 #commentText').html('');
    $('#messageModal2').html($('#confirmation-data1').html());
    $('#messageModal2 h3.modal-title').html((head));
    $('#messageModal2 #myModalLabel').html((head));
    $('#messageModal2 div.modal-body').html('\n\n' + (msg) + '</b>');
    $('#messageModal2 a.btn-other').html('OK');
    $('#messageModal2').modal();
    $("#messageModal2").on('hidden.bs.modal', function () {
        window.location = pathname;
    });

};

