<?php

function get_header($field) {
    $headers = getallheaders();
    foreach ($headers as $name => $value) {
        if ($name == $field){
            return $value;
        }
    }
}