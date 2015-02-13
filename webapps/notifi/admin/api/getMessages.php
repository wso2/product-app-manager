<?php

include('../../util/func.php');

$file = fopen("../../storage/db.txt","r");

$values = array();


$userName = "";

$claims = explode("=.", get_header('X-JWT-Assertion'));
if(isset($claims)){
    if(isset($claims[1])){
        $userClaims = json_decode(base64_decode($claims[1]));
        $userName = $userClaims->Subject;
    }
}else{

}

while(!feof($file)){

    $val = fgetcsv($file);
    if(is_array($val)){
        if($val[2] === $userName){
            $values[] = ($val);
        }

    }
}

echo json_encode(array_reverse($values));


fclose($file);