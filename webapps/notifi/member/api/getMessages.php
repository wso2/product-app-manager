<?php

$file = fopen("../../storage/db.txt","r");

$values = array();

while(!feof($file)){

    $val = fgetcsv($file);
    if(is_array($val)){
        $values[] = ($val);
    }
}

echo json_encode(array_reverse($values));


fclose($file);