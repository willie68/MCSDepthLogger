<?php
// Installtion:
// copy the tile.php and the logging.php into a directroy on your web server. Ensure that there is enough space for the tiles.
// this script will cache all downloaded and created tiles.
// In the webserver configuration you have to enable the rewrite engine. I have only testet this with an apache webserver.
// the php script will need some parameters 
// tile.php?zoom=<z>&x=<x>&y=<y> where x,y,z are the parameter of the traditional tiles servers.
// 
//Alias /tileserver "<path to the web directory>"
//<Directory "<path to the web directory>">
//    Order Allow,Deny
//    Allow from all
//    Require all Granted
//    RewriteEngine On
//    RewriteBase /tileserver/
//    RewriteRule ^(.*)/(.*)/(.*)$  tile.php?zoom=$1&x=$2&y=$3
//</Directory>
include "Logging.php";
// Logging class initialization
$log = new Logging();
 
// set path and name of log file (optional)
$log->lfile('data/map.log');
  
header('Content-Type: image/png');
$proxy = false;

$zoom = $_GET['zoom'];
$x = $_GET['x'];
$y = $_GET['y'];

$osmpath = "data/openstreetmap/$zoom/$x/";
$markerpath = "data/openseamap/marker/$zoom/$x/";
$path = "data/output/marker/$zoom/$x/";

if (!file_exists($path)) {
  mkdir($path, 0777, true);
}

if (!file_exists($osmpath)) {
  mkdir($osmpath, 0777, true);
}

if (!file_exists($markerpath)) {
  mkdir($markerpath, 0777, true);
}

$ovlname = $path . "$y";
$logline = $logline . "Request tile \"$zoom/$x/$y\", filename \"$ovlname\"";

if (file_exists($ovlname)) {
  $diff = time() - fileatime($ovlname); // fileatime liefert den Unixtimestamp des letzten Zugriffs auf eine Datei
  if($diff > 2592000) { // 2592000 = 30 Tage * 24 Stunden * 60 Minuten * 60 sec
    $logline = $logline . " /tile older than 30 days: \"$ovlname\"";
	unlink($ovlname);
  }
}

if (file_exists($ovlname)) {
  $ovlTile = imagecreatefrompng($ovlname);
  $logline = $logline . " /tile from cache";
  imagepng($ovlTile);
  imagedestroy($ovlTile);
} else {
  $logline = $logline . " /render new tile";
  // Datei wurde noch nicht gerendert, also neu rendern
  $aContext = array(
      'http' => array(
          'proxy' => 'tcp://192.168.127.2:8080',
          'request_fulluri' => true,
      ),
  );
  $cxContext = stream_context_create($aContext);

  $savename = $osmpath . "$y";
  if (file_exists($savename)) {
    $diff = time() - fileatime($savename); // fileatime liefert den Unixtimestamp des letzten Zugriffs auf eine Datei
    if($diff > 2592000) { // 2592000 = 30 Tage * 24 Stunden * 60 Minuten * 60 sec
      $logline = $logline . " /tile older than 30 days: \"$savename\"";
      unlink($savename);
    }
  }
  if (!file_exists($savename)) {
    if ($proxy) {
      $contents= file_get_contents("http://tile.openstreetmap.org/$zoom/$x/$y", false, $cxContext);
    } else {
	//      $contents= file_get_contents("http://osm2.wtnet.de/tiles/base/$zoom/$x/$y");
      $contents= file_get_contents("http://tile.openstreetmap.org/$zoom/$x/$y");
    }
    if ($contents !== false) {
      $savefile = fopen($savename, "w");
      fwrite($savefile, $contents);
      fclose($savefile);
    }
  }

  $baseTile = imagecreatefrompng($savename);

  error_reporting(E_ERROR);

  $oversavename = $markerpath . "$y";
  if (file_exists($oversavename)) {
    $diff = time() - fileatime($oversavename); // fileatime liefert den Unixtimestamp des letzten Zugriffs auf eine Datei
    if($diff > 2592000) { // 2592000 = 30 Tage * 24 Stunden * 60 Minuten * 60 sec
      $logline = $logline . " /tile older than 30 days: \"$oversavename\"";
      unlink($oversavename);
    }
  }
  if (!file_exists($oversavename)) {
    if ($proxy) {
      $overcontents= file_get_contents("http://t1.openseamap.org/seamark/$zoom/$x/$y", false, $cxContext);
    } else {
      $overcontents= file_get_contents("http://t1.openseamap.org/seamark/$zoom/$x/$y");
    }
    if ($overcontents !== false) {
      $overfile = fopen($oversavename, "w");
      fwrite($overfile, $overcontents);
      fclose($overfile);
    }  
  }
  if (file_exists($oversavename)) {
    $overTile = imagecreatefrompng($oversavename);
    imagealphablending($overTile, true);

    $image = imagecreatetruecolor(256, 256);
    imagecopyresampled($image, $baseTile, 0, 0, 0, 0, 256, 256, 256, 256);   
    imagealphablending($image,true); //allows us to apply a 24-bit watermark over $image

    //$image = image_overlap($image, $overTile);
    imagecopy($image, $overTile, 0, 0, 0, 0, 256, 256);
	// output to stream
    imagepng($image);

    // and into the cache...
	
    $logline = $logline . " /saving tile to \"$ovlname\"";
    imagepng($image, $ovlname);
  
    imagedestroy($image);
    imagedestroy($overTile);
    imagedestroy($baseTile);
  } else {
    // Output and free from memory
    imagepng($baseTile);
    $logline = $logline . " /saving tile to \"$ovlname\"";
    imagepng($baseTile, $ovlname);
    imagedestroy($baseTile);
  }
}

$log->lwrite($logline);

// close log file
$log->lclose();
?>
