<?php 

$ch = curl_init();
$documentID = 'ajzele';
curl_setopt($ch, CURLOPT_URL, 'http://127.0.0.1:5984/tiles/'.$documentID."/"."DSC01455.JPG");
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'GET');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, array(
'Content-type: application/json',
'Accept: */*'
));


curl_setopt($ch, CURLOPT_USERPWD, 'tileuser:tile');
$response = curl_exec($ch);
curl_close($ch);

header('Content-type: image/jpg');
echo "$response";

?>