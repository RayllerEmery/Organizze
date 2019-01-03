package com.example.user.organizze.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.organizze.R;
import com.example.user.organizze.config.ConfiguracaoFirebase;
import com.example.user.organizze.helper.Base64Custom;
import com.example.user.organizze.helper.ConnectionHelper;
import com.example.user.organizze.helper.DataUtil;
import com.example.user.organizze.model.Movimentacao;
import com.example.user.organizze.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData;
    private TextInputEditText campoCategoria;
    private TextInputEditText campoDescricao;
    private EditText campoValor;
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;
    private ConnectionHelper connectionHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoData = findViewById(R.id.editDespesaData);
        campoCategoria = findViewById(R.id.editDespesaCategoria);
        campoDescricao = findViewById(R.id.editDespesaDescricao);
        campoValor = findViewById(R.id.editDespesaValor);


        campoData.setText(DataUtil.dataAtual());

        connectionHelper = new ConnectionHelper(this);
        if(connectionHelper.isInternetAvailble())
            recuperarDespesaTotal();
    }

    public void salvarDespesa(View view){
        Log.i("boolean", "" + connectionHelper.isInternetAvailble());
        if(connectionHelper.isInternetAvailble()){
            if(validarCampos()) {
                String data = campoData.getText().toString();

                Movimentacao movimentacao = new Movimentacao();
                Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
                movimentacao.setValor(valorRecuperado);
                movimentacao.setCategoria(campoCategoria.getText().toString());
                movimentacao.setDescricao(campoDescricao.getText().toString());
                movimentacao.setData(data);
                movimentacao.setTipo("d");

                Double despesaAtualizada = despesaTotal + valorRecuperado;
                atualizarDespesa(despesaAtualizada);

                try{
                    movimentacao.salvar(data, this);
                    finish();
                }catch(Exception e){
                    Toast.makeText(this, "Ocorreu um erro! Tente novamente! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }else{
            Toast.makeText(this, "Não foi possível salvar, verifique sua conexão.", Toast.LENGTH_SHORT).show();
        }

    }

    public Boolean validarCampos(){

        String txtValor = (campoValor.getText().toString());
        String txtData = (campoData.getText().toString());
        String txtCategoria = (campoCategoria.getText().toString());
        String txtDescricao = (campoDescricao.getText().toString());

        if(!txtValor.isEmpty()){
            if(!txtData.isEmpty()){
                if(!txtCategoria.isEmpty()){
                    if(!txtDescricao.isEmpty()){
                        return true;
                    }else{
                        Toast.makeText(this, "Preencha o Descricao!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(this, "Preencha o Categoria!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(this, "Preencha o Data!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(this, "Preencha o Valor!", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void recuperarDespesaTotal(){
        String email = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail().toString());
        DatabaseReference firebase = reference.child("usuario").child(email);
//
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //recuperando o usuario e setando o valor despesaTotal somando com o valor adicionado
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void atualizarDespesa(Double despesa){
        String email = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail().toString());
        DatabaseReference firebase = reference.child("usuario").child(email);

        firebase.child("despesaTotal").setValue(despesa);
    }
}
