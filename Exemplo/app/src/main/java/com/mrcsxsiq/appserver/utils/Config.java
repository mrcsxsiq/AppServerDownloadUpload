package com.mrcsxsiq.appserver.utils;

import java.net.URLEncoder;

public class Config {

    public static final String ENDERECO = "10.206.10.91";

    public static final String SERVER = "http://"+ENDERECO+"/AppServerDownloadUpload/index.php";
    public static final String SERVER_DELETE = "http://"+ENDERECO+"/AppServerDownloadUpload/delete.php?arquivo=";


    public static final String SERVER_UPLOADS_FOLDER = "http://"+ENDERECO+"/AppServerDownloadUpload/uploads/";

    public static final String urlDeletarArquivo (String arquivo){
        try {
           return Config.SERVER_DELETE + URLEncoder.encode(arquivo, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

}
