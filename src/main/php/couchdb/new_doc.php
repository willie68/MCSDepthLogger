<?php 

$ch = curl_init();
$customer = array(
    'firstname' => 'Branko',
    'lastname' => 'Ajzele',
    'username' => 'ajzele',
    'email' => 'branko.ajzele@example.com',
    'pass' => md5('myPass123')
);
$payload = json_encode($customer);
curl_setopt($ch, CURLOPT_URL, 'http://127.0.0.1:5984/tiles/'.$customer['username']);
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'PUT'); /* or PUT */
curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, array(
    'Content-type: application/json',
    'Accept: */*'
));

curl_setopt($ch, CURLOPT_USERPWD, 'tileuser:tile');
$response = curl_exec($ch);
curl_close($ch);

echo "$response";

?>