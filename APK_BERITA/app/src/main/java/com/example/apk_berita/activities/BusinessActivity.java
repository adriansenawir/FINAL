package com.example.apk_berita.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.apk_berita.R;
import com.example.apk_berita.adapter.NewsAdapter;
import com.example.apk_berita.api.NewsApi;
import com.example.apk_berita.model.ModelNews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusinessActivity extends AppCompatActivity implements NewsAdapter.onSelectData {

    RecyclerView rvBusiness;
    NewsAdapter newsAdapter;
    List<ModelNews> modelNews = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon tunggu");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menampilkan data");

        rvBusiness = findViewById(R.id.rvNews);
        rvBusiness.setHasFixedSize(true);
        rvBusiness.setLayoutManager(new LinearLayoutManager(this));

        setupToolbar();
        loadJSON();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbNews);
        toolbar.setTitle("Berita Bisnis");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadJSON() {
        progressDialog.show();
        AndroidNetworking.get(NewsApi.GET_CATEGORY_BUSINESS)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            JSONArray playerArray = response.getJSONArray("articles");
                            for (int i = 0; i < playerArray.length(); i++) {
                                JSONObject temp = playerArray.getJSONObject(i);
                                ModelNews dataApi = new ModelNews();
                                dataApi.setTitle(temp.getString("title"));
                                dataApi.setUrl(temp.getString("url"));
                                dataApi.setPublishedAt(temp.getString("publishedAt"));
                                dataApi.setUrlToImage(temp.getString("urlToImage"));

                                modelNews.add(dataApi);
                                showNews();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BusinessActivity.this, "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(BusinessActivity.this, "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showNews() {
        newsAdapter = new NewsAdapter(BusinessActivity.this, modelNews, this);
        rvBusiness.setAdapter(newsAdapter);
    }

    @Override
    public void onSelected(ModelNews mdlNews) {
        startActivity(new Intent(BusinessActivity.this, OpenNewsActivity.class).putExtra("url", mdlNews.getUrl()));
    }

}