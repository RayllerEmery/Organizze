package com.example.user.organizze.DB;

import com.example.user.organizze.model.Movimentacao;
import com.example.user.organizze.model.Usuario;

public interface iDBInternoDAO {

    public boolean salvar(Usuario usuario, Movimentacao movimentacao);
    public boolean atualizar(Usuario usuario, Movimentacao movimentacao);
    public boolean deletar();
    public void recuperar(Usuario usuario, Movimentacao movimentacao);
}
