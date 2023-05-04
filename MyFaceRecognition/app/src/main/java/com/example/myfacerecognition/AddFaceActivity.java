package com.example.myfacerecognition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ComponentActivity;
import androidx.core.content.ContextCompat;

import androidx.core.app.ComponentActivity.*;
import com.google.android.material.snackbar.Snackbar;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFaceActivity extends AppCompatActivity {

    Button btpic;
    ImageView pictureView;
    Bitmap bmp=null;
    int select_code=100,camera_code=102;
    Uri uri;
    String selectedImagePath=null;
    private static final int READ_REQUEST_CODE = 42;
    ActivityResultLauncher<String[]>mPermissionResultLauncher;
    ActivityResultLauncher<Intent> mGetImage;
    ActivityResultLauncher<String>mTakePhoto;
    private  boolean isReadPermissionGranted=false;
    private  boolean isWritePermissionGranted=false;
    private  static final int CAMERA = 100;
    private  ProgressDialog pd;
    RelativeLayout layout;
    Button upload;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face);

        /*------------Barre de titre,Drawer menu,menu,etc-----------------*/

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        /*-------------Définir les différents éléments des fichiers xml--------*/

        pd = new ProgressDialog(AddFaceActivity.this);
        layout = findViewById(R.id.layout_snack);
        pictureView = (ImageView) findViewById(R.id.Imageprev);
        btpic = (Button) findViewById(R.id.cpic);
        upload = (Button) findViewById(R.id.upload);

        /*-------------donner les permissions d'accès en ecriture et en lecture au stockage externe/interne----------*/

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if (result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null) {
                    isReadPermissionGranted = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                    isWritePermissionGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });
        requestPermission();
        mGetImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    pictureView.setImageBitmap(bitmap);
                    bmp=bitmap;
                }
            }
        });

        /*--------------Definir les differentes actions à accomplir suite à un clic-----------*/

        btpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetImage.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(bmp);
            }
        });
    }

    /*----------------Differentes fonctions utilisées------------*/


    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }

    private  void requestPermission(){
        boolean minsdk = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
        isReadPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        isWritePermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        isWritePermissionGranted = isWritePermissionGranted||minsdk;

        List<String> permissionRequest = new ArrayList<>();
        if (isWritePermissionGranted){
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionRequest.isEmpty()){
            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }

    public void uploadImage(Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // Read BitMap by file path
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Intent intent = getIntent();
        String id_user = intent.getStringExtra("userId");
        String imgName = id_user+".1.jpg";

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imgName, RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();

        pd.setTitle("Please Wait...");
        pd.setMessage("Preparing to upload...");
        pd.show();

        postRequest(" http://192.168.244.104:5000/api/upload", postBodyImage);

    }

    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.hide();
                        pd.dismiss();
                        Snackbar snackbar = Snackbar.make(layout,"Failed to connect to server",Snackbar.LENGTH_LONG);
                        snackbar.show();
                        //Toast.makeText(AddFaceActivity.this, "Failed to Connect to Server", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                String res = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.hide();
                        pd.dismiss();
                        if(res.equals("This image does not have a face")){
                            Snackbar snackbar = Snackbar.make(layout,res+"\n"+"please take another capture!",Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    Toast.makeText(AddFaceActivity.this, "Undo Clicked", Toast.LENGTH_SHORT).show();
                                }
                            });
                            snackbar.show();
                        }else{
                            startActivity(new Intent(getApplicationContext(),UserInfoActivity.class));
                        }
                    }
                });
            }
        });
    }

}