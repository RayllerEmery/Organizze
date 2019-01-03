package com.example.user.organizze.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.organizze.R;
import com.example.user.organizze.model.Movimentacao;

import org.w3c.dom.Text;

import java.util.List;

public class AdapterMovimentacoes extends RecyclerView.Adapter<AdapterMovimentacoes.MyHolder> {

    List<Movimentacao> movimentacaos;
    Context context;

    public AdapterMovimentacoes(List<Movimentacao> list, Context context){
        this.movimentacaos = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemLista = LayoutInflater.from(viewGroup.getContext())
                                        .inflate(R.layout.adapter_movimentacao, viewGroup,false);
        return new MyHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        Movimentacao movimentacao = movimentacaos.get(i);

        myHolder.txtNome.setText(movimentacao.getDescricao());
        myHolder.txtCategoria.setText(movimentacao.getCategoria());

        if(movimentacao.getTipo().equals("d")){
            myHolder.txtValor.setTextColor(context.getResources().getColor(R.color.colorAccentDespesa));
            myHolder.txtValor.setText("-" + movimentacao.getValor());
        }else{
            myHolder.txtValor.setTextColor(context.getResources().getColor(R.color.colorAccentReceita));
            myHolder.txtValor.setText("" + movimentacao.getValor());
        }

    }

    @Override
    public int getItemCount() {
        return movimentacaos.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView txtNome, txtCategoria, txtValor;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            txtNome = itemView.findViewById(R.id.txtNomeMovimentacao);
            txtCategoria = itemView.findViewById(R.id.txtCategoriaMovimentacao);
            txtValor = itemView.findViewById(R.id.txtValorMovimentacao);
        }
    }
}
