$postFields = array();

//files
 $postFields['file'] = "@$filePath";

//get the extension of the image file
$tumbnailExtention = preg_replace('/^.*\.([^.]+)$/D', '$1', $thumbnailPath);
$postFields['thumbnail'] = "@$thumbnailPath;type=image/$tumbnailExtention";

//metaData
$postFields['title'] = "$title";
$postFields['description'] = "$description";
$postFields['tags'] = "$tags";
$postFields['licenseinfo'] = "$licenseinfo";
$postFields['token'] = "$userToken";

$curl_handle = curl_init();

curl_setopt($curl_handle, CURLOPT_URL, $api_url);
curl_setopt($curl_handle, CURLOPT_RETURNTRANSFER, true);
curl_setopt($curl_handle, CURLOPT_POST, true);
curl_setopt($curl_handle, CURLOPT_POSTFIELDS, $http_post_fields);

//execute the API Call
$returned_data = curl_exec($curl_handle);