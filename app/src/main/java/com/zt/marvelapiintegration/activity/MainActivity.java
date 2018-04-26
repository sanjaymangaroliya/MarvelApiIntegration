package com.zt.marvelapiintegration.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.GridView;
import android.widget.TextView;

import com.zt.marvelapiintegration.R;
import com.zt.marvelapiintegration.global.GlobalConstant;
import com.zt.marvelapiintegration.global.Utils;
import com.zt.marvelapiintegration.restapicall.AsyncTaskCompleteListener;
import com.zt.marvelapiintegration.restapicall.ParseController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private GridView grid_view;
    private ArrayList<HashMap<String, String>> comicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        initUI();

    }

    private void initUI() {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("Comic News");
        grid_view = findViewById(R.id.grid_view);

        getComicNews();
    }

    private void getComicNews() {
        Map<String, String> map = new HashMap<>();
        map.put("url", GlobalConstant.URL + GlobalConstant.COMICS);
        map.put("ts", "1");
        map.put("apikey", GlobalConstant.PUBLIC_KEY);
        map.put("hash", Utils.MD5String("1" + GlobalConstant.PRIVATE_KEY + GlobalConstant.PUBLIC_KEY));

        Utils.hideKeyboard(this);
        new ParseController(this, ParseController.HttpMethod.GET, map,
                true, getResources().getString(R.string.loading),
                new AsyncTaskCompleteListener() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject objMain = new JSONObject(response);
                            String code = objMain.getString("code");
                            if (code.equals("200")) {
                                JSONObject objData = objMain.getJSONObject("data");
                                JSONArray jsonArray = objData.getJSONArray("results");
                                if (jsonArray.length() > 0) {
                                    comicList.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        JSONObject objectThumbnail = object.getJSONObject("thumbnail");
                                        //
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        String path = objectThumbnail.getString("path");
                                        String extension = objectThumbnail.getString("extension");
                                        hashMap.put("image_path", path + "." + extension);
                                        comicList.add(hashMap);
                                    }
                                    setData();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(int statusCode, String msg) {
                        Utils.showToast(MainActivity.this, msg);
                    }
                });
    }

    private void setData() {
        grid_view.setAdapter(new GridViewAdapter(this, comicList));
    }
}
