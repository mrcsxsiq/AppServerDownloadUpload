<?php


	$file = $_GET["arquivo"];

	if ($file != NULL){
		$dir = $_SERVER['DOCUMENT_ROOT']."/AppServerDownloadUpload"."/uploads/";
		if (file_exists($dir.$file)) {
		    unlink($dir.$file);
		    exit (json_encode("Sucesso"));
		} else {
		    exit (json_encode("Erro"));
		}
	} else {
		exit (json_encode("Erro"));
	}


?>
