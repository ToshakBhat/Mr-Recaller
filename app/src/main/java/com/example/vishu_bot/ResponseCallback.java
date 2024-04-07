package com.example.vishu_bot;

public interface ResponseCallback {
    void onResponse(String response);

    void onError(Throwable throwable);
}
