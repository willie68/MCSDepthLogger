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

// Logging class initialization
$log = new Logging ();

// set path and name of log file (optional)
$log->lfile ( 'data/map.log' );

header ( 'Content-Type: image/png' );
$proxy = false;

$zoom = $_GET ['zoom'];
$x = $_GET ['x'];
$y = $_GET ['y'];
$url = $_GET ['url'];

$path = "data/$url/$zoom/$x/";

$name = $path . "$y";

if (!file_exists($path)) {
  mkdir($path, 0777, true);
}

$logline = "Request tile \"$url/$zoom/$x/$y\", name \"$name\"";

// autoupdate tile cache
if (file_exists($name)) {
  $diff = time () - fileatime ( $name ); // fileatime liefert den Unixtimestamp des letzten Zugriffs auf eine Datei
  if ($diff > 2592000) { // 2592000 = 30 Tage * 24 Stunden * 60 Minuten * 60 sec
    $logline = $logline . " /tile older than 30 days: \"$ovlname\"";
    unlink ( $name );
  }
}

if (!file_exists($name)) {
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
    $savefile = fopen($name, "w");
    fwrite($savefile, $contents);
    fclose($savefile);
    
    // Output and free from memory
    $tile = imagecreatefromstring ( $contents );
    imagepng ( $tile );
    imagedestroy ( $tile );
  }
} else {
  $logline = $logline . "/ tile from cache";
  $tile = imagecreatefrompng ( $name );
  imagepng ( $tile );
  imagedestroy ( $tile );
}

$log->lwrite ( $logline );

// close log file
$log->lclose ();
?>
