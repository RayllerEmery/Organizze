package com.example.user.organizze.model;

import android.content.Context;
import android.widget.Toast;

import com.example.user.organizze.config.ConfiguracaoFirebase;
import com.example.user.organizze.helper.Base64Custom;
import com.example.user.organizze.helper.DataUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {

    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private Double valor;
    private String key;

    public Movimentacao() {
    }

    public void salvar(String data, Context context){
        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail());

            reference.child("movimentacao")
                    .child(idUsuario)
                    .child(DataUtil.mesAnoDataEscolhida(data))
                    .push()
                    .setValue(this);

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
