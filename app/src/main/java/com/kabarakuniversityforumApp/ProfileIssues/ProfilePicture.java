package com.kabarakuniversityforumApp.ProfileIssues;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kabarakuniversityforumApp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfilePicture extends AppCompatActivity {
    private ImageView mProfile;
    private String imgeUrl,context;
    private RequestOptions requestOptions;
    private Toolbar toolbar;
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        toolbar = (Toolbar)findViewById(R.id.toolbar_profile_my_picture);
        setSupportActionBar(toolbar);

        mProfile= (ImageView)findViewById(R.id.image_profile_profile);
        mBack = (ImageView)findViewById(R.id.image_back_profile_my_picture);
        //passed value
        imgeUrl = getIntent().getExtras().getString("imageUrl");
        context = getIntent().getExtras().getString("activity");

        if (context.equals("adapter")){
           toolbar.setTitle("Posted Picture");
        }
        requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(imgeUrl).into(mProfile);



        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_download){
            BitmapDrawable draw = (BitmapDrawable) mProfile.getDrawable();
            Bitmap bitmap = draw.getBitmap();
            if (mProfile!=null) {

                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/Kabarak");
                dir.mkdirs();
                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                try {
                    outStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(dir));
                    sendBroadcast(intent);
                    Toast.makeText(getApplicationContext(), "Saved to gallery", Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else
            {
                Toast.makeText(getApplicationContext(),"oops!:\nCannot download null image...!!",Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
