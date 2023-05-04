package com.example.myfacerecognition;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity {

    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    ActivityResultLauncher<Intent> mGetImage;
    ActivityResultLauncher<String> mTakePhoto;
    private boolean isReadPermissionGranted = false;
    private boolean isWritePermissionGranted = false;
    private static final int CAMERA = 100;
    private ProgressDialog pd;
    RelativeLayout layout;
    EditText userid, username, usersurname, usermatricule, userfiliere, userniveau, usertransactnumber, usertranche, userprice;
    Button submit;
    public String id = null;
    RelativeLayout layouts;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        /*------------Barre de titre,Drawer menu,menu,etc-----------------*/

        Toolbar toolbar = findViewById(R.id.userInfo_toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        /*-------------Définir les différents éléments des fichiers xml--------*/

        pd = new ProgressDialog(UserInfoActivity.this);
        layout = findViewById(R.id.layout_snack);
        userid = (EditText) findViewById(R.id.UserId);
        username = (EditText) findViewById(R.id.UserName);
        usersurname = (EditText) findViewById(R.id.UserSurname);
        usermatricule = (EditText) findViewById(R.id.UserMatricule);
        userfiliere = (EditText) findViewById(R.id.UserFiliere);
        userniveau = (EditText) findViewById(R.id.UserNiveau);
        usertransactnumber = (EditText) findViewById(R.id.UserTransaction);
        usertranche = (EditText) findViewById(R.id.UserTranche);
        userprice = (EditText) findViewById(R.id.UserPrix);
        submit = (Button) findViewById(R.id.Submit_btn);
        layouts = findViewById(R.id.usersnackbar);

        /*--------------Definir les differentes actions à accomplir suite à un clic-----------*/

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userid.getText().toString().isEmpty() || usermatricule.getText().toString().isEmpty() || usertransactnumber.getText().toString().isEmpty() || userprice.getText().toString().isEmpty() || usertranche.getText().toString().isEmpty() || userfiliere.getText().toString().isEmpty() || username.getText().toString().isEmpty() || usersurname.getText().toString().isEmpty() || userniveau.getText().toString().isEmpty()) {
                    Toast.makeText(UserInfoActivity.this, "Empty field....please fill all fields!", Toast.LENGTH_SHORT).show();
                } else {
                    id = userid.getText().toString();
                    uploadForm();
                }
            }
        });
        getUserId();
        userid.setEnabled(false);
    }

    /*----------------Differentes fonctions utilisées------------*/

    @Override
    public boolean onNavigateUp() {return super.onNavigateUp();}

    @Override
    public  void onBackPressed(){super.onNavigateUp();}

    public void uploadForm() {
        String dumyText = null;
        dumyText = userid.getText().toString() + "-" + username.getText().toString() + "-" + usersurname.getText().toString() + "-" + usermatricule.getText().toString() + "-" + userfiliere.getText().toString() + "-" + userniveau.getText().toString() + "-" + usertransactnumber.getText().toString() + "-" + usertranche.getText().toString() + "-" + userprice.getText().toString();
        RequestBody formbody
                = new FormBody.Builder()
                .add("sample", dumyText)
                .build();

        pd.setTitle("Please Wait...");
        pd.setMessage("Preparing to upload...");
        pd.show();

        postRequest_form(" http://192.168.244.104:5000/sample", formbody);
    }

    void postRequest_form(String postUrl, RequestBody formbody) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(postUrl)
                .post(formbody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.hide();
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "server down", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body().string().equals("received")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.hide();
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "data received", Toast.LENGTH_SHORT).show();
                            username.setText(null);
                            usersurname.setText(null);
                            usermatricule.setText(null);
                            userfiliere.setText(null);
                            userniveau.setText(null);
                            usertransactnumber.setText(null);
                            usertranche.setText(null);
                            userprice.setText(null);
                            OpenAddFaceView();
                        }
                    });
                }
            }
        });
    }

    public void OpenAddFaceView() {
        Intent intent = new Intent(this, AddFaceActivity.class);
        intent.putExtra("userId", id);
        startActivity(intent);
    }

    public void getUserId() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://192.168.244.104:5000/getuserid").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar snackbar = Snackbar.make(layouts,"Failed to connect to server",Snackbar.LENGTH_LONG);
                        snackbar.show();
                        //Toast.makeText(UserInfoActivity.this, "server down", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String st=response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userid.setText(st);
                    }
                });
            }
        });
    }

}