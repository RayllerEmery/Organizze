package com.example.user.organizze.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.user.organizze.model.Movimentacao;
import com.example.user.organizze.model.Usuario;

public class DBInternoDAO implements iDBInternoDAO{

    private SQLiteDatabase escrever;
    private SQLiteDatabase ler;

    public DBInternoDAO(Context context){
        DBHelper db = new DBHelper(context);
        escrever = db.getWritableDatabase();
        ler = db.getReadableDatabase();
    }

    @Override
    public boolean salvar(Usuario usuario, Movimentacao movimentacao) {
        return false;
    }

    @Override
    public boolean atualizar(Usuario usuario, Movimentacao movimentacao) {
        return false;
    }

    @Override
    public boolean deletar() {
        return false;
    }

    @Override
    public void recuperar(Usuario usuario, Movimentacao movimentacao) {

    }
}
