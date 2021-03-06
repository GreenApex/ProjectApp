package com.greenapex;

/**
 * Created by rultech on 15/2/16.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greenapex.Utils.Constants;
import com.greenapex.Utils.Utils;
import com.greenapex.response.models.CommonResponse;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by admin on 21-Oct-15.
 */
public abstract class WebserviceBase extends AsyncHttpResponseHandler {

    private final AsyncHttpClient client;

    @Override
    public void onProgress(long bytesWritten, long totalSize) {
        int update = (int) (bytesWritten);
        webserviceOnProgress(update);
        super.onProgress(bytesWritten, totalSize);
    }

    @Override
    public void onStart() {
        super.onStart();
        webserviceStart();

    }

    @Override
    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
        super.onPreProcessResponse(instance, response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (statusCode != 200)
            webserviceFailedWithMessage("Unable to process please check your internet and try again...");
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

        ByteArrayInputStream bis = new ByteArrayInputStream(responseBody);
        BufferedReader r = new BufferedReader(new InputStreamReader(bis));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            parseResponse(total.toString(), statusCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Context context;


    public abstract void webserviceStart();

    public abstract void webserviceOnProgress(int update);

    public abstract void webserviceSucessful(String response, String message);

    public abstract void webserviceFailedWithMessage(String message);


    public WebserviceBase(Context mContext) {
        context = mContext;
        client = new AsyncHttpClient();
        client.setResponseTimeout(9999999);
        client.setLoggingLevel(Log.ERROR);
    }

    public abstract void callService(@NonNull final JSONObject params, int method_type) throws UnsupportedEncodingException, JSONException;

    public abstract void callService(@NonNull final JSONObject params) throws UnsupportedEncodingException, JSONException;

    protected void callService(String url, final JSONObject params, int method_type) throws UnsupportedEncodingException {
        if (Utils.isNetworkConnected(context)) {

            switch (method_type) {
                case Constants.METHOD_POST: {
                    StringEntity entity = new StringEntity(params.toString());
                    client.post(context, url, entity, "application/json", this);

                    webserviceStart();
                    break;
                }
                case Constants.METHOD_GET: {
                    callService(url);
                    break;
                }
            }

        } else {
            webserviceFailedWithMessage("Oops!!! Please check your internet.");
        }


    }

    protected void callService(String url, final JSONObject params) throws UnsupportedEncodingException {
        if (Utils.isNetworkConnected(context)) {

            StringEntity entity = new StringEntity(params.toString());
            client.post(context, url, entity, "application/json", this);

            webserviceStart();

        } else {
            webserviceFailedWithMessage("Oops!!! Please check your internet.");
        }


    }

    private void callService(String url) {
        if (Utils.isNetworkConnected(context)) {
            client.get(context, url, this);
            webserviceStart();
        } else {
            webserviceFailedWithMessage("Oops!!! Please check your internet.");
        }


    }

    protected void parseResponse(String response, int statusCode) {

        try {
            Gson gson = new GsonBuilder().create();
            CommonResponse commonResponse = gson.fromJson(response, CommonResponse.class);
            if (statusCode == 200) {
                if (commonResponse.getData().isJsonObject()) {
                    webserviceSucessful(commonResponse.getData().getAsJsonObject().toString(), commonResponse.getMessage());
                } else if (commonResponse.getData().isJsonArray()) {
                    webserviceSucessful(commonResponse.getData().getAsJsonArray().toString(), commonResponse.getMessage());
                } else {
                    webserviceSucessful(commonResponse.getData().toString(), commonResponse.getMessage());
                }
            } else {
                webserviceFailedWithMessage(commonResponse.getMessage());
            }

        } catch (Exception e) {
            webserviceFailedWithMessage("Unable to process please try again...");
            e.printStackTrace();
        }
    }
}
