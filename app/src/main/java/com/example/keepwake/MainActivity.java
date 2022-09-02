package com.example.keepwake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Disposable scanTask;
    private boolean isScan;
    Button button;
    private OkHttpClient okHttpClient;
    public static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .callTimeout(3, TimeUnit.SECONDS)
                .build();
        setContentView(R.layout.activity_main);
        button = this.findViewById(R.id.button_switch);
        button.setOnClickListener( v -> {
            switchScan();
            button.setText( isScan ? "停止" : "开始" );
        });
    }

    private void sendHttpData(Long index) {
        Logger.i("----on interval %s----",index);
        Gson gson = new Gson();
        Request request = new Request.Builder()
                .url(String.format("http://%s/beacon", "172.16.25.80:5000"))
                .post(RequestBody.create(gson.toJson(index), mediaType))
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Logger.e(e, "failure report to server");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                Logger.i("success report to server");
            }
        });
    }

    private void switchScan(){
        if(isScan){
            this.isScan = false;
            if( scanTask != null ) {
                scanTask.dispose();
            }
        }else{
            scanTask = Observable.interval(5, TimeUnit.SECONDS)
                    .subscribe( t -> sendHttpData(t),
                            e -> Logger.e(e, "on error"));
            this.isScan = true;
        }
    }
}