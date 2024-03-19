package com.example.memeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button refreshButton;
    Button shareButton;
    RequestQueue requestQueue;
    String apiUrl = "https://meme-api.com/gimme/memes"; // Initial API URL
    Gson gson = new Gson();
    MemeData currentMemeData;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        refreshButton = findViewById(R.id.button);
        shareButton = findViewById(R.id.button1);
        //author.setText(currentMemeData.getAuthor());

        // Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Fetch initial meme data
        fetchMemeData();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fetch new meme data on refresh button click
                fetchMemeData();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareMeme();
            }
        });
    }

    private void fetchMemeData() {
            // Fetch JSON data using Volley StringRequest
            StringRequest stringRequest = new StringRequest(apiUrl,
                    response -> {
                        // Parse JSON response using Gson
                        currentMemeData = gson.fromJson(response, MemeData.class);

                        // Get the image URL from the parsed data
                        String imageUrl = currentMemeData.getImageUrl();

                        // Load image using ImageRequest
                        ImageRequest imageRequest = new ImageRequest(imageUrl,
                                responseBitmap -> {
                                    // Set the loaded image to your ImageView
                                    imageView.setImageBitmap(responseBitmap);
                                },
                                0,
                                0,
                                ImageView.ScaleType.CENTER_INSIDE,
                                Bitmap.Config.RGB_565,
                                error -> {
                                    // Handle error loading image
                                    Log.e("ImageRequest", "Error loading image: " + error.getMessage());
                                });

                        // Add the image request to the RequestQueue
                        requestQueue.add(imageRequest);
                    },
                    error -> {
                        // Handle error fetching JSON data
                        Log.e("StringRequest", "Error fetching JSON: " + error.getMessage());
                    });

            // Add the string request to the RequestQueue
            requestQueue.add(stringRequest);
    }

    private void shareMeme() {
        // Convert ImageView to Bitmap
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        // Save the Bitmap to a temporary file
        File imageFile = saveBitmapToFile(bitmap);

        if (imageFile != null) {
            // Create a content URI for the file using FileProvider
            Uri imageUri = FileProvider.getUriForFile(this,
                    "com.example.memeapp.fileprovider",
                    imageFile);

            // Create a share intent to share the image
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this meme!"); // Optional text message

            // Set the WhatsApp package explicitly to ensure sharing via WhatsApp
            //shareIntent.setPackage("com.whatsapp");
            //shareIntent.setPackage("com.facebook");

            // Start the activity to share the image
            startActivity(Intent.createChooser(shareIntent, "Share Meme"));
        } else {
            Log.e("ShareMeme", "Error saving image to file.");
        }
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        File imagesDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MemeApp");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        File imageFile = new File(imagesDir, "meme_image.png");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
