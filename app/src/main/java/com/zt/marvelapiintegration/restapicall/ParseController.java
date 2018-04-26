package com.zt.marvelapiintegration.restapicall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.zt.marvelapiintegration.global.GlobalConstant;
import com.zt.marvelapiintegration.global.Utils;

import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

public class ParseController {

    private Map<String, String> map;
    private Context activity;
    private AsyncTaskCompleteListener listener = null;
    private String strURL;
    private boolean isShowLoading;
    private String loadingMsg;
    private HttpMethod httpMethod;
    private static String strInternet = "Internet Connection is not available";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
    private static final int TIMEOUT = 15 * 60 * 1000;
    private int statusCode = 0;

    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }

        return client;
    }

    protected static OkHttpClient configureHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null)
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true).
                        connectionPool(new ConnectionPool(0, 1, TimeUnit.NANOSECONDS));
        return enableTls12OnPreLollipop(builder).build();
    }

    public enum HttpMethod {
        GET,
        POST,
        PUT,
        DELETE,
        FILEUPLOAD
    }

    // ParseController constructor,get all parameter via map,checking internet
    // connection
    public ParseController(Context act, HttpMethod method,
                           Map<String, String> map, boolean isShowLoading, String loadingMsg,
                           AsyncTaskCompleteListener listener) {
        this.map = map;
        this.httpMethod = method;
        this.activity = act;
        this.listener = listener;
        this.isShowLoading = isShowLoading;
        this.loadingMsg = loadingMsg;
        this.statusCode = 0;
        // is Internet Connection Available...
        if (Utils.isNetworkAvailable(activity)) {
            if (map.containsKey("url")) {
                strURL = map.get("url");
                map.remove("url");
            }
            //Multipart body must have at least one part.
            if (map.size() == 0) {
                map.put("device_type", "android");
            }
            //  getUnsafeOkHttpClient();
            new AsyncHttpRequest().execute();
            Log.d("TYPE", httpMethod.toString());
        } else {
            listener.onFailed(100, strInternet);
        }
    }


    // API call via async Task
    class AsyncHttpRequest extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog = new ProgressDialog(activity);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (activity != null && activity instanceof Activity
                    && !((Activity) activity).isFinishing() && isShowLoading) {
                progressDialog.setMessage(loadingMsg);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            return chooseWebService();
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            checkResponse(result);
            super.onPostExecute(result);
        }
    }

    public String chooseWebService() {
        switch (httpMethod) {
            case POST:
                return callPOSTAPI();
            case GET:
                return callGETAPI();
            case DELETE:
                return callDeleteAPI();
            case PUT:
                return callPUTAPI();
            case FILEUPLOAD:
                map.put("url", strURL);
                return fileVideoAndImageUploading(map, 2);
            default:
                break;
        }
        return null;
    }


    public String callPOSTAPI() {
        try {
            Request.Builder builder = new Request.Builder();
            MultipartBody.Builder multipartBody = new MultipartBody.Builder();
            multipartBody.setType(MultipartBody.FORM);
            Log.e("STRURL", strURL);
            for (String key : map.keySet()) {
                Log.e("Params", key + " = " + map.get(key));
                multipartBody.addFormDataPart(key, map.get(key));
            }
            builder.url(strURL);
            builder.post(multipartBody.build());
            Request request = builder.build();
            Response response = configureHttpClient().newCall(request).execute();
            statusCode = response.code();
            return response.body().string();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (SocketTimeoutException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
            return strInternet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String fileVideoAndImageUploading(Map<String, String> hashMap, int k) {
        try {
            Request.Builder builder = new Request.Builder();
            MultipartBody.Builder multipartBody = new MultipartBody.Builder();
            multipartBody.setType(MultipartBody.FORM);
            if (hashMap.containsKey("media")) {
                String strFilePath = hashMap.get("media");
                hashMap.remove("media");
                File file = new File(strFilePath);
                Log.d("file size", file.length() + "");
                if (file != null && file.exists()) {
                    Log.d("Params >> ", "file_data = " + strFilePath);
                    multipartBody
                            .addFormDataPart("profile_photo", file.getName(),
                                    RequestBody.create(MEDIA_TYPE_PNG, file));
                } else {
                    Log.d("file path error", "path not found: " + strFilePath);
                }
            }
            String strUrl = hashMap.get("url");
            hashMap.remove("url");
            Log.d("STRURL >> ", strUrl);
            for (String key : hashMap.keySet()) {
                Log.d("Params", key + " = " + hashMap.get(key));
                multipartBody.addFormDataPart(key, hashMap.get(key));
            }
            builder.url(strUrl);
            builder.post(multipartBody.build());
            Request request = builder.build();
            Response response = configureHttpClient().newCall(request).execute();
            return response.body().string();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (SocketTimeoutException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
            return strInternet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String callPOSTAPI(Map<String, String> map1) {
        try {

            Request.Builder builder = new Request.Builder();

            MultipartBody.Builder multipartBody = new MultipartBody.Builder();
            multipartBody.setType(MultipartBody.FORM);


            String strUrl = map1.get("url");
            map1.remove("url");
            Log.e("STRURL >> ", strUrl);


            for (String key : map1.keySet()) {
                Log.e("Params", key + " = " + map1.get(key));

                multipartBody.addFormDataPart(key, map1.get(key));
            }


            builder.url(strUrl);
            builder.post(multipartBody.build());
            Request request = builder.build();


            Response response = configureHttpClient().newCall(request).execute();
            return response.body().string();

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (SocketTimeoutException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
            return strInternet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String callPUTAPI() {
        try {
            Request.Builder builder = new Request.Builder();
            Log.e("Url in ParseController", strURL);
            FormBody.Builder formBody = new FormBody.Builder();
            for (String key : map.keySet()) {
                Log.e("Params", key + " = " + map.get(key));
                formBody.add(key, map.get(key));
            }
            builder.url(strURL);
            builder.put(formBody.build());
            Request request = builder.build();

            Response response = configureHttpClient().newCall(request).execute();
            statusCode = response.code();
            return response.body().string();

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (SocketTimeoutException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
            return strInternet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String callGETAPI() {
        try {
            Log.d("STRURL", strURL);
            HttpUrl.Builder httpBuider = HttpUrl.parse(strURL).newBuilder();
            for (String key : map.keySet()) {
                Log.e("Params", key + " = " + map.get(key));
                httpBuider.addQueryParameter(key, map.get(key));
            }
            Request request = new Request.Builder().url(httpBuider.build()).build();
            Response response = configureHttpClient().newCall(request).execute();
            statusCode = response.code();
            return response.body().string();

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (SocketTimeoutException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
            return strInternet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String callDeleteAPI() {
        try {
            Request.Builder builder = new Request.Builder();
            Log.d("STRURL", strURL);
            FormBody.Builder formBody = new FormBody.Builder();
            for (String key : map.keySet()) {
                Log.d("Params", key + " = " + map.get(key));
                formBody.add(key, map.get(key));
            }
            builder.url(strURL);
            builder.delete(formBody.build());
            Request request = builder.build();

            Response response = configureHttpClient().newCall(request).execute();
            statusCode = response.code();
            return response.body().string();

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (SocketTimeoutException ex) {
            ex.printStackTrace();
            return strInternet;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
            return strInternet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // checking response is null or empty if not null call onTaskCompleted
    // method
    public void checkResponse(String response) {
        if (response == null || TextUtils.isEmpty(response.trim())
                || response.trim().equalsIgnoreCase("null")) {
            Log.e("Response in Controller", "Response is null");
            listener.onFailed(statusCode, "Response Is Empty Please Try Again Later");
        } else {
            Log.e("Response in Controller", response.trim());
            Log.e("=======", "=================================================================");
            listener.onSuccess(response);
        }
    }
}
