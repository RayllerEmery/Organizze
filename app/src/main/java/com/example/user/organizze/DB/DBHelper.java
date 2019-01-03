package com.example.user.organizze.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static int VERSION = 1;
    public static String NOME_DB = "DB_Organizze";
    public static String TABELA_USUARIO = "tabela_usuario";
    public static String TABELA_DESPESAS = "tabela_despesa";

    public DBHelper(Context context) {
        super(context, NOME_DB, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABELA_USUARIO +
        "(nome TEXT, saldo NUMERIC, movimentacoes BLOB)";

        try {
            db.execSQL(sql);
            Log.i("Tabela","Sucesso ao criar tabela!");
        }catch (Exception e){
            Log.i("Tabela","Erro ao criar tabela! " + e.getMessage());
            e.printStackTrace();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
