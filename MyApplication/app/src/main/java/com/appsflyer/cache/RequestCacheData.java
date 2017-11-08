package com.appsflyer.cache;

import java.io.StringReader;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: gilmeroz
 * Date: 5/8/14
 * Time: 1:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestCacheData {
    private String version;
    private String postData;
    private String requestURL;
    private String cacheKey;

    public RequestCacheData(String urlString, String postData, String sdkBuildNumber) {
        this.requestURL = urlString;
        this.postData = postData;
        this.version = sdkBuildNumber;
    }

    public RequestCacheData(char[] chars) {
        Scanner scanner = new Scanner(new String(chars));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("url=")){
                this.requestURL = line.substring("url=".length()).trim();
            } else if (line.startsWith("version=")){
                this.version = line.substring("version=".length()).trim();
            } else if (line.startsWith("data=")){
                this.postData = line.substring("data=".length()).trim();
            }
        }
        scanner.close();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public String getCacheKey() {
        return cacheKey;
    }
}
