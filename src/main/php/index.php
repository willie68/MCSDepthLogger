<html>
<head>
</head>
<body>
<h1>Willies Proxy Tileserver</h1>

Dieser kleine Server dient dazu die Tiles von OpenStreetMap mit den Marker Tiles von OpenSeaMap zu kombinieren.

Hier mal ein Beispiel wie das aussieht:
<table borders="0">
<tr><td>OpenStreetMap:</td><td>OpenSeaMapOverlay:</td><td>Kombiniert:</td></tr>
<tr><td><img src="http://tile.openstreetmap.org/16/35282/24038.png" border="1"></td>
    <td><img src="http://t1.openseamap.org/seamark/16/35282/24038.png" border="1"></td>
	<td><img src="http://wkla.dyndns.org/tileserver/16/35282/24038.png" border="1"></td>
</tr>
</table>
Hier das aktuelle Script: tile.php<br/>
<?php
show_source("tile.php");
?>
<br>
und das benutze einfache logging.php script<br/>
<?php
show_source("logging.php");
?>
<br>
</body>
</html>