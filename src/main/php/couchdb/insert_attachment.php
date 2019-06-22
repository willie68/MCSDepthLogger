<?php 

$ch = curl_init();
$database = 'tiles';
$documentID = 'ajzele';
$revision = '5-bf0f2147308d2f3df6f7674267e1cd66';
$attachment = 'DSC01455.JPG';
$repository = 'E:/privat/Dropbox/Photos/Welpen/';
$finfo = finfo_open(FILEINFO_MIME_TYPE);
$contentType = finfo_file($finfo, $repository.$attachment);
$payload = file_get_contents($repository.$attachment);

curl_setopt($ch, CURLOPT_URL, sprintf('http://127.0.0.1:5984/%s/%s/%s?rev=%s', $database, $documentID, $attachment, $revision));
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'PUT');

//curl_setopt($ch, CURLOPT_POST, true);

curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, array(
'Content-type: '.$contentType,
'Accept: */*'
));

curl_setopt($ch, CURLOPT_USERPWD, 'tileuser:tile');
$response = curl_exec($ch);
curl_close($ch);

echo "$response<br/>$_response";

?>