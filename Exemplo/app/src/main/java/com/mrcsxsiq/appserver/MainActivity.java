package com.mrcsxsiq.appserver;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mrcsxsiq.appserver.adapter.RecyclerViewAdapter;
import com.mrcsxsiq.appserver.app.CustomApplication;
import com.mrcsxsiq.appserver.domain.Arquivo;
import com.mrcsxsiq.appserver.service.NLService;
import com.mrcsxsiq.appserver.utils.Config;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private String TAG = MainActivity.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private FloatingActionButton floatingActionButtonUpload;
    private List<Arquivo> list = new ArrayList<>();


    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;
    int id = 1;
    /*String urlsToDownload[] = {
            "http://www.devoxx.be/wp-content/uploads/2014/10/android.png",
            "http://www.devoxx.be/wp-content/uploads/2014/10/android.png",
            "http://www.devoxx.be/wp-content/uploads/2014/10/android.png",
            "http://www.devoxx.be/wp-content/uploads/2014/10/android.png",
            "http://www.devoxx.be/wp-content/uploads/2014/10/android.png" };
*/

    int counter = 0;
    private NotificationReceiver nReceiver;
    ArrayList<AsyncTask<String, String, Void>> arr;

    class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getExtras().getString(NLService.NOT_EVENT_KEY);
            Log.i("NotificationReceiver", "NotificationReceiver onReceive : " + event);
            if (event.trim().contentEquals(NLService.NOT_REMOVED)) {
                killTasks();
            }
        }
    }

    private void killTasks() {
        if (null != arr & arr.size() > 0) {
            for (AsyncTask<String, String, Void> a : arr) {
                if (a != null) {
                    Log.i("NotificationReceiver", "Killing download thread");
                    a.cancel(true);
                }
            }
            mNotifyManager.cancelAll();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerViewAdapter = new RecyclerViewAdapter(this, list);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);


        floatingActionButtonUpload = (FloatingActionButton) findViewById(R.id.floatingActionButtonUpload);
        floatingActionButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                onRefresh();
            }
        });


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        fetch();
                                    }
                                }
        );



    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        killTasks();
        unregisterReceiver(nReceiver);
    }


    @Override
    public void onRefresh() {
        fetch();
    }

    public void baixarArquivo (String arquivo) {


        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Downloading file...").setContentText("Download in progress").setSmallIcon(R.mipmap.ic_launcher);
        // Start a lengthy operation in a background thread
        mBuilder.setProgress(0, 0, true);
        mNotifyManager.notify(id, mBuilder.build());
        mBuilder.setAutoCancel(true);

        arr = new ArrayList<AsyncTask<String, String, Void>>();

        FileDownloader fileDownloader = new FileDownloader();
        String url = "";
        try {
            url = Config.SERVER_UPLOADS_FOLDER + URLEncoder.encode(arquivo, "UTF-8").replace("+", "%20");

        } catch (Exception e){};

        fileDownloader.execute(url, arquivo);
        arr.add(fileDownloader);


        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();

        // check to see if the enabledNotificationListeners String contains our
        // package name
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            // in this situation we know that the user has not granted the app
            // the Notification access permission
            // Check if notification is enabled for this application
            Log.i("ACC", "Dont Have Notification access");
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } else {
            Log.i("ACC", "Have Notification access");
        }

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NLService.NOT_TAG);
        registerReceiver(nReceiver, filter);


    }

    private void fetch() {

        list.clear();

        swipeRefreshLayout.setRefreshing(true);

        JsonArrayRequest req = new JsonArrayRequest(Config.SERVER,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String name = jsonObject.getString("name");
                                    String size = jsonObject.getString("size");
                                    String type = jsonObject.getString("type");
                                    list.add(new Arquivo(name, size, type));

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }

                            recyclerViewAdapter.notifyDataSetChanged();
                        }

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

              //  Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

            }
        });


        recyclerViewAdapter.notifyDataSetChanged();
        CustomApplication.getInstance().addToRequestQueue(req);
    }


    private void downloadImagesToSdCard(String downloadUrl, String imageName) {
        FileOutputStream fos;
        InputStream inputStream = null;


        try {
            URL url = new URL(downloadUrl);
            /* making a directory in sdcard */
           // String sdCard = Environment.getExternalStorageDirectory().toString();
            String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();

            File myDir = new File(sdCard, "AppServer");

            /* if specified not exist create new */
            if (!myDir.exists()) {
                myDir.mkdir();
                Log.v("", "inside mkdir");
            }

            /* checks the file and if it already exist delete */
            String fname = imageName;
            File file = new File(myDir, fname);
            Log.d("file===========path", "" + file);
            if (file.exists())
                file.delete();

            /* Open a connection */
            URLConnection ucon = url.openConnection();

            HttpURLConnection httpConn = (HttpURLConnection) ucon;
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            inputStream = httpConn.getInputStream();

            /*
             * if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
             * inputStream = httpConn.getInputStream(); }
             */

            fos = new FileOutputStream(file);
            // int totalSize = httpConn.getContentLength();
            // int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, bufferLength);
                // downloadedSize += bufferLength;
                // Log.i("Progress:", "downloadedSize:" + downloadedSize +
                // "totalSize:" + totalSize);
            }
            inputStream.close();
            fos.close();
            Log.d("test", "File Saved in sdcard..");
        } catch (IOException io) {
            inputStream = null;
            fos = null;
            io.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    private class FileDownloader extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... param) {
            //downloadImagesToSdCard(param[0], "Image" + counter + ".png");
            downloadImagesToSdCard(param[0], param[1]);
            return null;
        }

        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected void onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called");
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i("Async-Example", "onPostExecute Called");
/*
            float len = urlsToDownload.length;
            // When the loop is finished, updates the notification
            if (counter >= len - 1) {
                mBuilder.setContentTitle("Done.");
                mBuilder.setContentText("Download complete")
                        // Removes the progress bar
                        .setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
            } else {
                int per = (int) (((counter + 1) / len) * 100f);
                Log.i("Counter", "Counter : " + counter + ", per : " + per);
                mBuilder.setContentText("Downloaded (" + per + "/100");
                mBuilder.setProgress(100, per, false);
                // Displays the progress bar for the first time.
                mNotifyManager.notify(id, mBuilder.build());
            }
            counter++;

*/
        }

    }

}
