package com.example.myfacerecognition;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myfacerecognition.Utils.DataHelper;
import com.example.myfacerecognition.Utils.DataUserHelper;
import com.example.myfacerecognition.Utils.DialogFragment;
import com.example.myfacerecognition.Utils.DataHelper;
import com.example.myfacerecognition.Utils.DataUserHelper;
import com.example.myfacerecognition.Utils.DialogFragment;
import com.google.android.material.navigation.NavigationView;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener, DialogFragment.OnInputListener {

    DrawerLayout drawerLayout;


    private static final String TAG = "MainActivity";
    @Override
    public void sendInput(String input) {
        Log.d(TAG, "sendInput: got the input: " + input);
        mInput = input;
        st=mInput.split(";");

        SQLiteDatabase base = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DataHelper.CODE_UE,st[0].toUpperCase(Locale.ROOT));
        contentValues.put(DataHelper.FILIERE,st[1].toUpperCase(Locale.ROOT));
        contentValues.put(DataHelper.INTITULE,st[2].toUpperCase(Locale.ROOT));
        contentValues.put(DataHelper.SEMESTRE,st[3].toUpperCase(Locale.ROOT));
        contentValues.put(DataHelper.GRADE,st[4].toUpperCase(Locale.ROOT));
        contentValues.put(DataHelper.ANNEE,st[5].toUpperCase(Locale.ROOT));
        base.insert(DataHelper.HEADER_TABLE_NAME,null,contentValues);
        base.close();

        setInputToTextView();

    }

    public String mInput;
    public TextView code_ue,filiere,intitulé,semestre,grade,annee;
    String[]st;
    private DataHelper db;
    private DataUserHelper dbs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*------------Barre de titre,Drawer menu,menu,etc-----------------*/
        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        /*-------------Définir les différents éléments des fichiers xml--------*/

        code_ue = findViewById(R.id.code_ue_m);
        filiere = findViewById(R.id.filiere_m);
        intitulé = findViewById(R.id.intitulé_m);
        semestre = findViewById(R.id.semestre_m);
        grade = findViewById(R.id.grade_m);
        annee = findViewById(R.id.annee_m);

        /*--------------Definir les differentes actions à accomplir suite à un clic-----------*/

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        //faire basculer le menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        db = new DataHelper(this);
        dbs = new DataUserHelper(this);
        setInputToTextView();
        getUsers();

    }

    /*----------------Differentes fonctions utilisées------------*/

    public boolean getUsers(){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://192.168.244.104:5000/api/users_recognize").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "server down", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String st=response.body().string();
                String[] tab=st.split(",");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SQLiteDatabase base = dbs.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();

                        contentValues.put(DataUserHelper.ID,tab[0].toUpperCase(Locale.ROOT));
                        contentValues.put(DataUserHelper.NOM,tab[1].toUpperCase(Locale.ROOT));
                        contentValues.put(DataUserHelper.PRENOM,tab[2].toUpperCase(Locale.ROOT));
                        contentValues.put(DataUserHelper.MATRICULE,tab[3].toUpperCase(Locale.ROOT));
                        contentValues.put(DataUserHelper.FILIERE,tab[4].toUpperCase(Locale.ROOT));
                        contentValues.put(DataUserHelper.NIVEAU,tab[5].toUpperCase(Locale.ROOT));
                        contentValues.put(DataUserHelper.TRANSACTION,tab[6].toUpperCase(Locale.ROOT));
                        contentValues.put(DataUserHelper.TRANCHE,tab[7].toUpperCase(Locale.ROOT));
                        contentValues.put(DataUserHelper.SOMME,tab[8].toUpperCase(Locale.ROOT));
                        contentValues.put(DataUserHelper.ANNEE,tab[9].toUpperCase(Locale.ROOT));
                        base.insert(DataUserHelper.HEADER_TABLE_NAMES,null,contentValues);
                        base.close();
                        //init();
                    }
                });
            }
        });
        return true;
    }

    public void init() {
        int i =0;
        /*----------------------Titre du tableau--------------------*/
        TableLayout stk = (TableLayout) findViewById(R.id.Presence_Table);
        stk.setDividerPadding(2);
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setMinimumHeight(50);
        tbrow0.setPadding(5,5,5,5);
        /*---------------------------------------------*/
        TextView tv0 = new TextView(this);
        tv0.setText(" N° ");
        tv0.setTextSize(14);
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        /*---------------------------------------------*/
        TextView tv1 = new TextView(this);
        tv1.setText(" Noms et Prenoms ");
        tv1.setTextSize(14);
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        /*---------------------------------------------*/
        TextView tv2 = new TextView(this);
        tv2.setText(" Matricules ");
        tv2.setTextSize(14);
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        /*---------------------------------------------*/
        TextView tv3 = new TextView(this);
        tv3.setText(" Signatures ");
        tv3.setTextSize(14);
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);
        stk.addView(tbrow0);
        /*----------Auto Mapping content-----------------------------*/

        SQLiteDatabase base = dbs.getReadableDatabase();
        Cursor cursor = base.query("student",new String[]{DataUserHelper.ID,DataUserHelper.NOM,DataUserHelper.PRENOM,DataUserHelper.MATRICULE,DataUserHelper.FILIERE,DataUserHelper.NIVEAU,DataUserHelper.TRANSACTION,DataUserHelper.TRANCHE,DataUserHelper.SOMME,DataUserHelper.ANNEE},
                null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String st = cursor.getString(0)+"->"+cursor.getString(1)+"->"+cursor.getString(2)+"->"+cursor.getString(3)+"->"
                        +cursor.getString(4)+"->"+cursor.getString(5)+"->"+cursor.getString(6)+"->"+cursor.getString(7)+"->"+cursor.getString(8)+"->"+cursor.getString(9)+".";
                String[] tab=st.split("->");

                TableRow tbrow = new TableRow(this);
                tbrow.setPadding(5, 5, 5, 5);
                if (i % 2 == 0) {
                    tbrow.setBackgroundColor(Color.WHITE);
                } else {
                    tbrow.setBackgroundColor(Color.LTGRAY);
                }
                /*--------------------------------------*/
                TextView t1v = new TextView(this);
                t1v.setText(tab[0]);
                t1v.setTextColor(Color.BLACK);
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v);
                /*-------------------------------------*/
                TextView t2v = new TextView(this);
                t2v.setText(tab[1] + tab[2]);
                t2v.setTextColor(Color.BLACK);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);
                /*-------------------------------------*/
                TextView t3v = new TextView(this);
                t3v.setText(tab[3]);
                t3v.setTextColor(Color.BLACK);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v);
                /*-------------------------------------*/
                TextView t4v = new TextView(this);
                t4v.setText("");
                t4v.setTextColor(Color.BLACK);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v);
                stk.addView(tbrow);

                i++;

            }while (cursor.moveToNext());
        }
        base.close();
    }

    private void setInputToTextView(){
        SQLiteDatabase base = db.getReadableDatabase();
        Cursor cursor = base.query("header",new String[]{DataHelper.CODE_UE,DataHelper.FILIERE,DataHelper.INTITULE,DataHelper.SEMESTRE,DataHelper.GRADE,DataHelper.ANNEE},
                null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String st = cursor.getString(0)+"->"+cursor.getString(1)+"->"+cursor.getString(2)+"->"+cursor.getString(3)+"->"
                        +cursor.getString(4)+"->"+cursor.getString(5)+"->"+".";
                String[] tab=st.split("->");

                code_ue.setText("Code UE:"+tab[0]);
                filiere.setText("Filière:"+tab[1]);
                intitulé.setText("Intitulé:"+tab[2]);
                semestre.setText("Semestre:"+tab[3]);
                annee.setText("Annee:"+tab[4]);
                grade.setText("Grade:"+tab[5]);

            }while (cursor.moveToNext());
        }
        base.close();
    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public  void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to exit ?");
        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {finish();});
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {dialog.cancel();});
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id==R.id.theme){
            Toast.makeText(this, "Nothing ....", Toast.LENGTH_SHORT).show();
        }
        if(id==R.id.ChangeHearder){
            DialogFragment dialog = new DialogFragment();
            dialog.show(getFragmentManager(), "MyCustomDialog");
        }
        if(id==R.id.update_person){
            Toast.makeText(this,"update person informations",Toast.LENGTH_SHORT).show();
        }
        if(id==R.id.Contact){
            Toast.makeText(this,"see all contact of enterprise",Toast.LENGTH_SHORT).show();
        }

        return  true;
    }

    public  void OpenUserInfoView(){startActivity(new Intent(this,UserInfoActivity.class));}
    public  void OpenUserFaceRecoView(){startActivity(new Intent(getApplicationContext(),Face_Reco_Activity.class));}

    private void shareTextOnly(String titlee) {
        String sharebody = titlee;
        Intent intentt = new Intent(Intent.ACTION_SEND);
        intentt.setType("text/plain");
        intentt.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        intentt.putExtra(Intent.EXTRA_TEXT, sharebody);
        startActivity(Intent.createChooser(intentt, "Share Via"));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                //startActivity(new Intent(this,MainActivity.class));
                //replaceFragment(new HomeFragment());
                break;

            case R.id.sample:
                OpenUserInfoView();
                break;

            case R.id.scanface:
                OpenUserFaceRecoView();
                //OpenScanFaceview();
                break;

            case R.id.nav_share:
                shareTextOnly("My Campus Face App");
                break;

            case R.id.nav_about:
                replaceFragment(new AboutFragment());
                break;

            case R.id.nav_logout:
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}