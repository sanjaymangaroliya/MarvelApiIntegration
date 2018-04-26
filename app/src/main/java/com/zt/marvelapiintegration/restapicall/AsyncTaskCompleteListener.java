package com.zt.marvelapiintegration.restapicall;

public interface AsyncTaskCompleteListener {
    void onSuccess(String response);
    void onFailed(int statusCode, String msg);
}
