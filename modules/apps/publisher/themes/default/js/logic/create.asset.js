$(function() {

	/*
	Creates a new asset
	*/

	//var id=$('#meta-asset-id').html();
	var type = $('#meta-asset-type').val();

	var TAG_API_URL = '/publisher/api/tag/';
	var tagType = $('#meta-asset-type').val() + 's';

	var tagUrl = TAG_API_URL + tagType;
	var THEME = 'facebook';
	var TAG_CONTAINER = '#tag-container';
	var CHARS_REM = 'chars-rem';
	var DESC_MAX_CHARS = 1000;

	$('#overview_description').after('<span class="span8 ' + CHARS_REM + '"></span>');

	//Obtain all of the tags for the given asset type
	$.ajax({
		url : tagUrl,
		type : 'GET',
		success : function(response) {
			var tags = JSON.parse(response);
			$(TAG_CONTAINER).tokenInput(tags, {
				theme : THEME,
				allowFreeTagging : true
			});

		},
		error : function() {
			console.log('unable to fetch tag cloud for ' + type);
		}
	});

	$('#overview_name').on('blur', function() {
		var $this = $(this), flag = $('.icon-check-appname'), btnCreate = $('#btn-create-asset'), assetName = $this.val();

		if (!flag.length) {
			$this.after('<i class="icon-check-appname"></i>');
			flag = $('.icon-check-appname');
		}

		//check if the asset name available as user types in
		$.ajax({
			url : '/publisher/api/validations/assets/' + type + '/' + assetName,
			type : 'GET',
			success : function(response) {

				var result = JSON.parse(response);

				//Check if the asset was added
				if (result.ok) {
					flag.removeClass().addClass('icon-ok icon-check-appname').show();
					btnCreate.removeAttr('disabled');
				} else {
					flag.removeClass().addClass('icon-ban-circle icon-check-appname').show();
					btnCreate.attr('disabled', 'disabled');
				}

			},
			error : function(response) {
				showAlert('Unable to auto check Asset name availability', 'error');
			}
		});

	});

	$('#btn-create-asset').on('click', function(e) {
		e.preventDefault();

		var fields = $('#form-asset-create :input');
		var data = {};
		var formData = new FormData();
		fields.each(function() {
			if (this.type != 'button') {
				//console.log(this.value);
				data[this.id] = this.value;
				formData = fillForm(this, formData);
			}
		});

		//Append the tags to the form data
		formData.append('tags', obtainTags());

		$.ajax({
			url : '/publisher/asset/' + type,
			type : 'POST',
			data : formData,
			cache : false,
			contentType : false,
			processData : false,
			success : function(response) {

				var result = JSON.parse(response);

				//Check if the asset was added
				if (result.ok) {
					showAlert('Asset added successfully.', 'success');
					window.location = '/publisher/assets/' + type + '/';
				} else {
					var msg = processErrorReport(result.report);
					showAlert(msg, 'error');
				}

			},
			error : function(response) {
				showAlert('Failed to add asset.', 'error');
			}
		});

		//$.post('/publisher/asset/'+type, data);

	});

	$('#overview_description').keyup(function() {
		var self = $(this), length = self.val().length, left = DESC_MAX_CHARS - length, temp;

		if (length > DESC_MAX_CHARS) {
			temp = self.val();
			$(this).val(temp.substring(0, DESC_MAX_CHARS));
			console.log("Max chars reached");
			return;
		}
		$('.' + CHARS_REM).text('Characters left: ' + left);
	});

	/*
	 The function is used to build a report message indicating the errors in the form
	 @report: The report to be processed
	 @return: An html string containing the validation issues
	 */
	function processErrorReport(report) {
		var msg = '';
		for (var index in report) {

			for (var item in report[index]) {
				msg += report[index][item] + "<br>";
			}
		}

		return msg;
	}

	/*
	 The function is used to add a given field to a FormData element
	 @field: The field to be added to the formData
	 @formData: The FormDara object used to store the field
	 @return: A FormData object with the added field
	 */
	function fillForm(field, formData) {

		var fieldType = field.type;

		if (fieldType == 'file') {
			console.log('added ' + field.id + ' file.');
			formData.append(field.id, field.files[0]);
		} else {
			formData.append(field.id, field.value);
		}

		return formData;
	}

	/*
	 The function is used to obtain tags selected by the user
	 @returns: An array containing the tags selected by the user
	 */
	function obtainTags() {

		var tagArray = [];

		try {
			var tags = $(TAG_CONTAINER).tokenInput('get');

			for (var index in tags) {
				tagArray.push(tags[index].name);
			}

			return tagArray;
		} catch(e) {
			return tagArray;
		}

	}


	$('.selectpicker').selectpicker();
});
