<?php

include('../../util/func.php');

$userName = "admin";

$claims = explode("=.", get_header('X-JWT-Assertion'));
if(isset($claims)){
    if(isset($claims[1])){
        $userClaims = json_decode(base64_decode($claims[1]));
        $userName = $userClaims->Subject;
    }
}else{

}

$message = $_POST['message'];

$file = '../../storage/db.txt';
$current = file_get_contents($file);
$current .= '"' . $message . '", "' . date("F j, Y, g:i a") . '"' . ', "' . $userName . '"' . "\n";
file_put_contents($file, $current);