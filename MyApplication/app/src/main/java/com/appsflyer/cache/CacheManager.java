package com.appsflyer.cache;

import android.content.Context;
import android.util.Log;
import com.appsflyer.AppsFlyerLib;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gilmeroz
 * Date: 5/8/14
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class CacheManager {

    public static final int CACHE_MAX_SIZE = 40;

    public static final String AF_CACHE_DIR = "AFRequestCache";

    private static CacheManager ourInstance = new CacheManager();

    public static CacheManager getInstance() {
        return ourInstance;
    }

    private CacheManager() {
    }

    private File getCacheDir(Context context){
        return new File(context.getFilesDir(),AF_CACHE_DIR);
    }

    public void init(Context context){
        try {
            if (!getCacheDir(context).exists()){
                getCacheDir(context).mkdir();
            }
        } catch (Exception e){
            Log.i(AppsFlyerLib.LOG_TAG,"Could not create cache directory");
        }
    }

    public void cacheRequest(RequestCacheData data, Context context){
//        SharedPreferences sharedPreferences = context.getSharedPreferences(AF_CACHE_PREF, 0);
//        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        editor.putString(SENT_SUCCESSFULLY_PREF,"true");
//        editor.commit();
        OutputStreamWriter out = null;
        try {
            File cacheDir = getCacheDir(context);
            if (!cacheDir.exists()){
                // we're not supposed to getString here. directory should have been created during init()
                cacheDir.mkdir();
                return;
            } else {
                File[] cacheFileList = cacheDir.listFiles();
                if (cacheFileList != null && cacheFileList.length > CACHE_MAX_SIZE){
                    Log.i(AppsFlyerLib.LOG_TAG,"reached cache limit, not caching request");
                    return;
                }
                Log.i(AppsFlyerLib.LOG_TAG,"caching request...");
                File requestFile = new File(getCacheDir(context),Long.toString(System.currentTimeMillis()));
                requestFile.createNewFile();
                out = new OutputStreamWriter(new FileOutputStream( requestFile.getPath(), true));
                out.write("version=");
                out.write(data.getVersion());
                out.write('\n');

                out.write("url=");
                out.write(data.getRequestURL());
                out.write('\n');

                out.write("data=");
                out.write(data.getPostData());
                out.write('\n');

                out.flush();
            }
        } catch (Exception e){
            Log.i(AppsFlyerLib.LOG_TAG,"Could not cache request");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public List<RequestCacheData> getCachedRequests(Context context){
        List<RequestCacheData> requests = new ArrayList<RequestCacheData>();

        try {
            File cacheDir = getCacheDir(context);
            if (!cacheDir.exists()){
                cacheDir.mkdir();
            } else {
                File[] files = cacheDir.listFiles();
                for (File file : files){
                    Log.i(AppsFlyerLib.LOG_TAG,"Found cached request"+file.getName());
                    requests.add(loadRequestData(file));
                }
            }
        } catch (Exception e){
            Log.i(AppsFlyerLib.LOG_TAG,"Could not cache request");
        }

        return requests;
    }

    private RequestCacheData loadRequestData(File file) {
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            RequestCacheData cacheData = new RequestCacheData(chars);
            cacheData.setCacheKey(file.getName());
            return cacheData;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public void deleteRequest(String cacheKey,Context context){
        File cacheDir = getCacheDir(context);
        File cachedRequestFile = new File(cacheDir,cacheKey);
        Log.i(AppsFlyerLib.LOG_TAG,"Deleting "+cacheKey+" from cache");
        if (cachedRequestFile.exists()){
            try {
                cachedRequestFile.delete();
            } catch (Exception e){
                Log.i(AppsFlyerLib.LOG_TAG,"Could not delete "+cacheKey+" from cache",e);
            }
        }
    }

}
