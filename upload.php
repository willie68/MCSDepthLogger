<?php
  $myfilename = $_FILES['userfile']['name'];
  $myfiletype =$_FILES['userfile']['type'];
  $myfiletemp = $_FILES['userfile']['tmp_name'];
  $section = $_REQUEST["section"];
  
  echo "The file $myfilename has been uploaded.<br>";

  if ($section == "mcsdepthlogger") {
    $myscriptpath = "D:/IN-Server/HTTP/MCSDepthLog/data";
    echo "Path: <a href=\"http://wkla.no-ip.biz/mcslog/data/$myfilename\">http://wkla.no-ip.biz/mcslog/data/$myfilename</a>"; 
	move_uploaded_file($myfiletemp, "$myscriptpath/$myfilename");
  }  
?>