<?php

$aContext = array ('http' => array ('proxy' => 'tcp://192.168.127.2:8080','request_fulluri' => true
)
);
$cxContext = stream_context_create ( $aContext );

$contents = file_get_contents ( "http://127.0.0.1:5984/tiles/osm1_wtnet_de_tiles_base_18_136292_87284_png/tile.png");

$bytes = md5($contents, true);
$out = 'md5-'. base64_encode($bytes);
echo $out;
?>
