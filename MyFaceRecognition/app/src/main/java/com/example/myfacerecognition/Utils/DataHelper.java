package com.example.myfacerecognition.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataHelper extends SQLiteOpenHelper {

    private  static  int DATABASE_VERSION = 1;
    private  static  final String DATABASE_NAME="en_tete";
    public static final  String HEADER_TABLE_NAME="header";
    public static final String CODE_UE ="Code_ue",FILIERE ="Filière",INTITULE = "Intitulé",SEMESTRE = "Semestre",GRADE ="Grade",ANNEE ="Annee";

    private static  final  String HEADER_TABLE_CREATE="CREATE TABLE " +HEADER_TABLE_NAME+
            "("+CODE_UE+" TEXT, "+FILIERE+" TEXT, "+INTITULE+" TEXT, "+SEMESTRE+" TEXT, "+GRADE+" TEXT," +ANNEE+" TEXT);";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HEADER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+HEADER_TABLE_NAME);
        onCreate(db);
    }
}

