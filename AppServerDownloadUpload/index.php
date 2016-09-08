<?php

	$dir = "uploads/";
	if(is_dir($dir)){
	    if($dh = opendir($dir)){
	        while(($file = readdir($dh)) != false){
	            if($file != "." and $file != ".."){
	                $files_array[] = 
	                	array('name' => $file,
	                		  'size' => filesize($dir.$file). ' bytes',
	                		  'type' => mime_content_type($dir.$file)
	                		  );

	            } 
	        }
	    }
	    //$return_array =  [];
	    $return_array =  $files_array;
	    exit (json_encode($return_array));
	}

?>
