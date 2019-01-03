package com.example.user.organizze.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.organizze.R;
import com.example.user.organizze.config.ConfiguracaoFirebase;
import com.example.user.organizze.helper.Base64Custom;
import com.example.user.organizze.helper.DataUtil;
import com.example.user.organizze.model.Movimentacao;
import com.example.user.organizze.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private EditText editValorReceita, editDataReceita, editCategoriaReceita, editDescricaoReceita;
    private Double receitaTotal = 0.0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        editValorReceita = findViewById(R.id.editValorReceita);
        editCategoriaReceita = findViewById(R.id.editCategoriaReceira);
        editDataReceita = findViewById(R.id.editDataReceita);
        editDescricaoReceita = findViewById(R.id.editDescricaoReceita);

        editDataReceita.setText(DataUtil.dataAtual());

        recuperaReceita();
    }

    public void salvarReceita(View view){
        if(validarCampos()){

            String data = editDataReceita.getText().toString();

            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setTipo("r");
            movimentacao.setData(data);
            movimentacao.setDescricao(editDescricaoReceita.getText().toString());
            movimentacao.setCategoria(editCategoriaReceita.getText().toString());
            Double receita = Double.parseDouble(editValorReceita.getText().toString());
            movimentacao.setValor(receita);

            receitaTotal = receitaTotal + receita;
            atualizaReceita();

            try{
                movimentacao.salvar(data, this);
                finish();
            }catch(Exception e){
                Toast.makeText(this, "Ocorreu um erro! Tente novamente! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public boolean validarCampos(){

        if(!editValorReceita.getText().toString().isEmpty()){
            if(!editCategoriaReceita.getText().toString().isEmpty()){
                if(!editDataReceita.getText().toString().isEmpty()){
                    if(!editDescricaoReceita.getText().toString().isEmpty()){
                        return true;
                    }else{
                        Toast.makeText(this, "Preencha a descrição!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(this, "Preencha a data!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(this, "Preencha a categoria!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(this, "Preencha o valor!", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void recuperaReceita(){

        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //convertendo para base64
        String email = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail().toString());
        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase().child("usuario").child(email);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void atualizaReceita(){
        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //convertendo para base64 e recuperando o usuario
        String email = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail().toString());
        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase().child("usuario").child(email);

        reference.child("receitaTotal").setValue(receitaTotal);
    }
}
