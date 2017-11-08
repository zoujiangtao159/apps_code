package com.appsflyer;

/**
 * Created by golan on 5/21/15.
 */


import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.util.Map;


class BackgroundHttpTask extends AsyncTask<String, Void, String> {


    private static final int WAIT_TIMEOUT = 30 * 1000;

    private String content = "";
    private boolean error = false;
    Map<String,String> bodyParameters;
    String bodyAsString;
    private Context mContext;
    private URL url;
    private boolean remoteDebugMode;
    private HttpURLConnection conn;

    private boolean shouldReadResponse;

    public BackgroundHttpTask(Context context){

        this.mContext = context;
        remoteDebugMode = true;
        shouldReadResponse = true;
    }

    protected void onPreExecute() {

        if (bodyAsString == null) {
            JSONObject jsonObject = new JSONObject(this.bodyParameters);
            if (jsonObject != null) {
                this.bodyAsString = jsonObject.toString();
            }
        }
    }

    protected String doInBackground(String... urls) {

        try {

            url = new URL(urls[0]);
            if (remoteDebugMode) {
                RemoteDebuggingManager.getInstance().addServerRequestEvent(url.toString(), bodyAsString);
            }
            int sizeOfBody = bodyAsString.getBytes("UTF-8").length;
            LogMessages.logMessageMaskKey("call = " + url + "; size = "+sizeOfBody+" byte"+(sizeOfBody>1?"s":"")+"; body = " + bodyAsString);

            conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(WAIT_TIMEOUT);
            conn.setConnectTimeout(WAIT_TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(bodyAsString);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();


            int responseCode= conn.getResponseCode();
            if (shouldReadResponse) {
                content = AppsFlyerLib.getInstance().readServerResponse(conn);
            }
            if (remoteDebugMode) {
                RemoteDebuggingManager.getInstance().addServerResponseEvent(url.toString(), responseCode, content);
            }

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                AFLogger.afLog("Status 200 ok");
            } else {
                error = true;
            }

        } catch (Throwable t) {
            AFLogger.afLogE("Error while calling "+url.toString(),t);
            error = true;
        }

        return content;
    }


    protected void onCancelled() {
    }

    protected void onPostExecute(String response) {
        if (error) {
            AFLogger.afLog("Connection error: "+ response);
            return;
        } else {
            AFLogger.afLog("Connection call succeeded: "+response);
        }
    }

    public void setRemoteDebugMode(boolean remoteDebugMode) {
        this.remoteDebugMode = remoteDebugMode;
    }

    public HttpURLConnection getConnection() {
        return conn;
    }

    public void setShouldReadResponse(boolean shouldReadResponse) {
        this.shouldReadResponse = shouldReadResponse;
    }


}