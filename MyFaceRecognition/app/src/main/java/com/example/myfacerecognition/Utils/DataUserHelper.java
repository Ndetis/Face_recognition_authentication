package com.example.myfacerecognition.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataUserHelper extends SQLiteOpenHelper {

    private  static  int DATABASE_VERSION = 1;
    private  static  final String DATABASE_NAME="user";
    public static final  String HEADER_TABLE_NAMES="student";
    public static final String ID ="id",NOM="nom",PRENOM="prenom",FILIERE ="Filière",MATRICULE = "matricule",TRANCHE = "tranche",SOMME="somme",TRANSACTION="transactions",NIVEAU ="niveau",ANNEE ="annee";

    private static  final  String HEADER_TABLE_CREATE="CREATE TABLE "+HEADER_TABLE_NAMES+ "("+ID+" TEXT,"+NOM+" TEXT,"+PRENOM+" TEXT,"+MATRICULE+" TEXT,"+FILIERE+" TEXT,"+NIVEAU+" TEXT,"+TRANSACTION+" TEXT,"+TRANCHE+" TEXT,"+SOMME+" TEXT,"+ANNEE+" TEXT);";

    public DataUserHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase dbs) {
        dbs.execSQL(HEADER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase dbs, int oldVersion, int newVersion) {
        dbs.execSQL("DROP TABLE IF EXISTS "+HEADER_TABLE_NAMES);
        onCreate(dbs);
    }
}
