package com.example.memegen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.service.chooser.ChooserTarget;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    Button next,share;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        next = findViewById(R.id.NextButton);
        share = findViewById(R.id.shareButton);
        loadMeme();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMeme();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadMeme() {

        url = "https://meme-api.herokuapp.com/gimme";
        progressBar.setVisibility(View.VISIBLE);



// Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            url = response.getString("url");
                            Glide.with(MainActivity.this).load(url).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return false;
                                }
                            }).into(imageView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }



                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });

// Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    public void shareImage() throws IOException {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        File file= new File(getExternalCacheDir()+"/"+"MemeGen"+".png");
        Intent shareImage = new Intent(Intent.ACTION_SEND);
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        outputStream.flush();
        outputStream.close();
        shareImage.setType("image/*");
        shareImage.putExtra(Intent.EXTRA_TEXT,"Hey Check out his cool Meme");
        shareImage.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareImage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(shareImage);

    }


}