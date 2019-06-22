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
require_once "lib_onCouch/couch.php";
require_once "lib_onCouch/couchClient.php";
require_once "lib_onCouch/couchDocument.php";

$couch_dsn = "http://tileuser:tile@localhost:5984/";
$couch_db = "tiles";

// Logging class initialization
$log = new Logging ();

// set path and name of log file (optional)
$log->lfile ( 'data/map.log' );

// create the couchdb client
$client = new couchClient ( $couch_dsn, $couch_db );

header ( 'Content-Type: image/png' );
$proxy = true;

$zoom = $_GET ['zoom'];
$x = $_GET ['x'];
$y = $_GET ['y'];
$url = $_GET ['url'];

$path = "data/$url/$zoom/$x/";

$name = "$url/$zoom/$x/$y";
$name = str_replace ( '/', '_', $name );
$name = str_replace ( '.', '_', $name );

$logline = "Request tile \"$url/$zoom/$x/$y\", cache id \"$name\"";

$logline = $logline . "/ tile from cache db: $attachmentUri";
$ch = curl_init ();
curl_setopt ( $ch, CURLOPT_URL, 'http://127.0.0.1:5984/tiles/' . $name . "/" . "tile.png" );
curl_setopt ( $ch, CURLOPT_CUSTOMREQUEST, 'GET' );
curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, true );
curl_setopt ( $ch, CURLOPT_HTTPHEADER, array ('Content-type: application/json','Accept: */*' 
) );

curl_setopt ( $ch, CURLOPT_USERPWD, 'tileuser:tile' );
$response = curl_exec ( $ch );
curl_close ( $ch );
if (! strpos ( $response, '"error"' )) {
  echo $response;
} else {
  
  $logline = $logline . "/ render new tile";
  // Datei wurde noch nicht gerendert, also neu rendern
  $aContext = array ('http' => array ('proxy' => 'tcp://192.168.127.2:8080','request_fulluri' => true 
  ) 
  );
  $cxContext = stream_context_create ( $aContext );
  
  if ($proxy) {
    $contents = file_get_contents ( "http://$url/$zoom/$x/$y", false, $cxContext );
  } else {
    $contents = file_get_contents ( "http://$url/$zoom/$x/$y" );
  }
  
  if ($contents !== false) {
    // creating a new tile document
    $document = new couchDocument ( $client );
    $document->setAutocommit ( false );
    $document->set ( array ('_id' => $name,'type' => "tile",'url' => $url,'zoom' => $zoom,'x' => $x,'y' => $y,'time' => time () 
    ) );
    $document->record ();
    
    $doc = couchDocument::getInstance ( $client, $name );
    try {
      $doc->storeAsAttachment ( $contents, "tile.png", "image/png" );
    } catch ( Exception $e ) {
      $log->lwrite ( "Error: attachment storage failed : " . $e->getMessage () . ' (' . $e->getCode () . ')' );
    }
    
    // Output and free from memory
    $tile = imagecreatefromstring ( $contents );
    imagepng ( $tile );
    imagedestroy ( $tile );
  }
}

$log->lwrite ( $logline );
// close log file
$log->lclose ();
?>
