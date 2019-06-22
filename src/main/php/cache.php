<?php
// Installtion:
// copy the cache.php and the logging.php into a directroy on your web server. Ensure that there is enough space for the tiles.
// this script will cache all downloaded and created tiles.
// In the webserver configuration you have to enable the rewrite engine. I have only testet this with an apache webserver.
// the php script will need some parameters
// tile.php?url=<url>&zoom=<z>&x=<x>&y=<y> where x,y,z are the parameter of the traditional tiles servers.
//
// Alias /tileserver "<path to the web directory>"
// <Directory "<path to the web directory>">
// Order Allow,Deny
// Allow from all
// Require all Granted
// RewriteEngine On
// RewriteBase /tileserver/
// RewriteRule ^(.*)/(.*)/(.*)/(.*)$ tile.php?url=$1&zoom=$2&x=$3&y=$4
// </Directory>
require_once "Logging.php";

$couch_user = "tileuser:tile";
$couch_dsn = "http://192.168.178.11:5984/";
$couch_db = "tiles";

// Logging class initialization
$log = new Logging ();

// set path and name of log file (optional)
$log->lfile ( 'data/map.log' );

$proxy = false;

$zoom = $_GET ['zoom'];
$x = $_GET ['x'];
$y = $_GET ['y'];
$url = $_GET ['url'];

$path = "data/$url/$zoom/$x/";

// building couchdb index
$name = "$url/$zoom/$x/$y";
$name = str_replace ( '/', '_', $name );
$name = str_replace ( '.', '_', $name );

$logline = "Request tile \"$url/$zoom/$x/$y\", cache id \"$name\"";
$logline = $logline . "/ tile from cache db, ";

// try to get the desired tile directly from the db
$ch = curl_init ();
curl_setopt ( $ch, CURLOPT_URL, $couch_dsn . $couch_db .'/' . $name . "/" . "tile.png" );
curl_setopt ( $ch, CURLOPT_CUSTOMREQUEST, 'GET' );
curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, true );
curl_setopt ( $ch, CURLOPT_HTTPHEADER, array ('Content-type: application/json','Accept: */*' ) );

curl_setopt ( $ch, CURLOPT_USERPWD, $couch_user );
$response = curl_exec ( $ch );
curl_close ( $ch );
if (! strpos ( $response, '"error"' )) {
  // ok, tile is in couchdb 
  header ( 'Content-Type: image/png' );
  echo $response;
  $logline = $logline . "ok";
} else {
  // tile not in db, trying to get the document
  $ch = curl_init ();
  curl_setopt ( $ch, CURLOPT_URL, $couch_dsn . $couch_db .'/' . $name );
  curl_setopt ( $ch, CURLOPT_CUSTOMREQUEST, 'GET' );
  curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, true );
  curl_setopt ( $ch, CURLOPT_HTTPHEADER, array ('Content-type: application/json','Accept: */*' ) );
  curl_setopt ( $ch, CURLOPT_USERPWD, $couch_user );
  $response = curl_exec ( $ch );
  curl_close ( $ch );

  if (! strpos ( $response, '"error"' )) {
    // document already created, maybe link to other doc?  
    $jsdecode = json_decode ( $response, true );
    $link = $jsdecode ['link'];
    if ($link) {
      // document has a link to another document, than get that tile
      $ch = curl_init ();
      curl_setopt ( $ch, CURLOPT_URL, $couch_dsn . $couch_db .'/' . $link . "/" . "tile.png" );
      curl_setopt ( $ch, CURLOPT_CUSTOMREQUEST, 'GET' );
      curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, true );
      curl_setopt ( $ch, CURLOPT_HTTPHEADER, array ('Content-type: application/json','Accept: */*' ) );
      curl_setopt ( $ch, CURLOPT_USERPWD, $couch_user );
      $response = curl_exec ( $ch );
      curl_close ( $ch );
      if (! strpos ( $response, '"error"' )) {
        // get tile in parent document 
//        header ( 'Content-Type: image/png' );
        echo $response;
        exit ();
      }
    }
  }
  
  // tile is not present, reading from external url
  $logline = $logline . "/ render new tile";
  // Datei wurde noch nicht gerendert, also neu rendern
  
  if ($proxy) {
    $aContext = array ('http' => array ('proxy' => 'tcp://192.168.127.2:8080','request_fulluri' => true));
    $cxContext = stream_context_create ( $aContext );
    $contents = file_get_contents ( "http://$url/$zoom/$x/$y", false, $cxContext );
  } else {
    $contents = file_get_contents ( "http://$url/$zoom/$x/$y" );
  }
  
  if ($contents !== false) {
    
    $bytes = md5($contents, true);
    $md5 = "md5-" . base64_encode($bytes);

    // creating a new tile document
    // getting the tile document
    $ch = curl_init ();
    curl_setopt ( $ch, CURLOPT_URL, $couch_dsn . $couch_db .'/' . $name );
    curl_setopt ( $ch, CURLOPT_CUSTOMREQUEST, 'GET' );
    curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, true );
    curl_setopt ( $ch, CURLOPT_HTTPHEADER, array ('Content-type: application/json','Accept: */*' ) );
    curl_setopt ( $ch, CURLOPT_USERPWD, $couch_user );
    $response = curl_exec ( $ch );
    curl_close ( $ch );
    
    // echo "getDoc:" . $response . "<br/>";
    if (strpos ( $response, '"error"' )) {
      // creating new tile document
      $ch = curl_init ();
      $customer = array ('type' => "tile",'url' => $url,'zoom' => $zoom,'x' => $x,'y' => $y,'time' => time () );
      $payload = json_encode ( $customer );
      curl_setopt ( $ch, CURLOPT_URL, $couch_dsn . $couch_db .'/' . $name );
      curl_setopt ( $ch, CURLOPT_CUSTOMREQUEST, 'PUT' ); /* or PUT */
      curl_setopt ( $ch, CURLOPT_POSTFIELDS, $payload );
      curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, true );
      curl_setopt ( $ch, CURLOPT_HTTPHEADER, array ('Content-type: application/json','Accept: */*' ) );
      curl_setopt ( $ch, CURLOPT_USERPWD, $couch_user );
      $response = curl_exec ( $ch );
      curl_close ( $ch );
      $jsdecode = json_decode ( $response, true );
      $revision = $jsdecode ['rev'];
      // echo "createDoc:" . $response . "<br/>";
    } else {
      $jsdecode = json_decode ( $response, true );
      $revision = $jsdecode ['_rev'];
    }

    
    // adding tile.png to the document
    $ch = curl_init ();
    $database = 'tiles';
    $attachment = 'tile.png';
    $contentType = "image/png";
    
    curl_setopt ( $ch, CURLOPT_URL, sprintf ( $couch_dsn . $couch_db . '/%s/%s?rev=%s', $name, $attachment, $revision ) );
    curl_setopt ( $ch, CURLOPT_CUSTOMREQUEST, 'PUT' );
    curl_setopt ( $ch, CURLOPT_POSTFIELDS, $contents );
    curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, true );
    curl_setopt ( $ch, CURLOPT_HTTPHEADER, array ('Content-type: ' . $contentType,'Accept: */*') );
    curl_setopt ( $ch, CURLOPT_USERPWD, $couch_user );
    $response = curl_exec ( $ch );
    curl_close ( $ch );
    
    // Output and free from memory
    header ( 'Content-Type: image/png' );
    $tile = imagecreatefromstring ( $contents );
    imagepng ( $tile );
    imagedestroy ( $tile );
  }
}

$log->lwrite ( $logline );
// close log file
$log->lclose ();
?>
