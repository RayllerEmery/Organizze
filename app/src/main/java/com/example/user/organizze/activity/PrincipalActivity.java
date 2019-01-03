package com.example.user.organizze.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.organizze.DB.DBInternoDAO;
import com.example.user.organizze.R;
import com.example.user.organizze.adapter.AdapterMovimentacoes;
import com.example.user.organizze.config.ConfiguracaoFirebase;
import com.example.user.organizze.helper.Base64Custom;
import com.example.user.organizze.helper.ConnectionHelper;
import com.example.user.organizze.helper.DataFormatHelper;
import com.example.user.organizze.model.Movimentacao;
import com.example.user.organizze.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView txtLoad, txtSaldoTotal;
    private Double receitaSaldo, despesaSaldo, saldoTotal;
    private DatabaseReference reference;
    private DatabaseReference movimentacaoRef;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacoes;
    private RecyclerView recicleView;
    private AdapterMovimentacoes adapterMovimentacoes;
    private String mesAnoSelecionado;
    private List<Movimentacao> movimentacoesLista = new ArrayList<>();
    private Movimentacao movimentacaoAuxiliar;
    private ConnectionHelper connectionHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        connectionHelper =  new ConnectionHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        calendarView = findViewById(R.id.calendarView);
        txtLoad = findViewById(R.id.txtLoad);
        txtSaldoTotal = findViewById(R.id.txtSaldoTotal);


        recicleView = findViewById(R.id.recycleViewMov);

        //configurando adapter
        adapterMovimentacoes = new AdapterMovimentacoes(movimentacoesLista, this);

        //configurando recicleview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recicleView.setLayoutManager(layoutManager);
        recicleView.setHasFixedSize(true);
        recicleView.setAdapter(adapterMovimentacoes);

        //trocando o nome da toolbar
        toolbar.setTitle("Organizze");
        //metodo para compatibilidade de versões mais antigas do android
        setSupportActionBar(toolbar);

        configuraCalendario();

        swipe();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(connectionHelper.isInternetAvailble()){
            recuperarDados();
            recuperaMovimentacoes();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(reference != null) {
            reference.removeEventListener(valueEventListenerUsuario);
            movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
        }
    }

    //metodo para inflar o menu criado
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sair, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //metodo que seleciona o item do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
                auth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //chamando activty despesa
    public void adicionarDespesa(View view){
        startActivity(new Intent(this, DespesasActivity.class));
    }
    //chamando activity receita
    public void adicionarReceita(View view){
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void configuraCalendario(){

        //configurando o nome dos meses e dias do calendário
        String [] mesNomes = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho",
                                "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

        String [] semanaNomes = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"};

        calendarView.setTitleMonths(mesNomes);
        calendarView.setWeekDayLabels(semanaNomes);

        CalendarDay day = calendarView.getCurrentDate();

        String dataFormatada = String.format("%02d", (day.getMonth() + 1));
        mesAnoSelecionado = (dataFormatada + "" + day.getYear());

        Log.i("mesAnoSelecionado", mesAnoSelecionado);

        //recuperando o mes selecionado pelo usuário
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
                String dataFormatada = String.format("%02d", (calendarDay.getMonth() + 1));
                mesAnoSelecionado = (dataFormatada + "" + calendarDay.getYear());

                movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
                recuperaMovimentacoes();
            }
        });
    }

    public void recuperarDados(){

        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String email = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail());

        reference = ConfiguracaoFirebase.getFirebaseDatabase().child("usuario").child(email);

        valueEventListenerUsuario = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaSaldo = usuario.getReceitaTotal();
                despesaSaldo = usuario.getDespesaTotal();
                saldoTotal = receitaSaldo - despesaSaldo;
                String saldoFormatado = DataFormatHelper.fomatoDecimal(saldoTotal);

                txtLoad.setText("Olá " + usuario.getNome());
                txtSaldoTotal.setText("R$ " + saldoFormatado);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void recuperaMovimentacoes(){
        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String email = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail());
        movimentacaoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("movimentacao").child(email).child(mesAnoSelecionado);

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movimentacoesLista.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Movimentacao movimentacao = data.getValue(Movimentacao.class);
                    movimentacao.setKey(data.getKey());
                    Log.i("Key", movimentacao.getKey());
                    movimentacoesLista.add(movimentacao);
                }
                adapterMovimentacoes.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void swipe(){
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlag = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlag, swipeFlag);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                exluirItem(viewHolder);
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(recicleView);
    }

    public void exluirItem(final RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(R.string.alert_principalActivity_title);
        alert.setMessage(R.string.alert_principalActivity_message);
        alert.setCancelable(false);

        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                movimentacaoAuxiliar = movimentacoesLista.get(position);

                FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
                String email = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail());
                movimentacaoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("movimentacao").child(email).child(mesAnoSelecionado);

                movimentacaoRef.child(movimentacaoAuxiliar.getKey()).removeValue();


//
                Toast.makeText(PrincipalActivity.this, "Item excluido", Toast.LENGTH_SHORT).show();

                adapterMovimentacoes.notifyDataSetChanged();
                atualizaValores();
            }
        });

        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapterMovimentacoes.notifyDataSetChanged();
            }
        });

        alert.create();
        alert.show();
    }

    public void atualizaValores(){
        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String email = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail());
        reference = ConfiguracaoFirebase.getFirebaseDatabase().child("usuario").child(email);

        if(movimentacaoAuxiliar.getTipo().equals("r")){
            receitaSaldo = receitaSaldo - movimentacaoAuxiliar.getValor();
            reference.child("receitaTotal").setValue(receitaSaldo);

        }
        if(movimentacaoAuxiliar.getTipo().equals("d")){
            receitaSaldo = despesaSaldo - movimentacaoAuxiliar.getValor();
            reference.child("despesaTotal").setValue(receitaSaldo);
        }
    }

}
