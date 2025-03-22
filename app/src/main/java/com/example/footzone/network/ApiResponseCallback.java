package com.example.footzone.network;

public interface ApiResponseCallback {

    void onSuccess(String jsonData);
    void onFailure(String errorMessage);

}
