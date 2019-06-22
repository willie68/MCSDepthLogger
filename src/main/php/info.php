<?php
$stdout = fopen('php://output', 'w');
ob_start();
        echo "echo output\n";
        fwrite($stdout, "FWRITTEN\n");
        echo "Also echo\n";
$out = ob_get_clean();
echo $out;

phpinfo();