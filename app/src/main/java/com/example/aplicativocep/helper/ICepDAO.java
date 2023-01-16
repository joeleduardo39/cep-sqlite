package com.example.aplicativocep.helper;

import com.example.aplicativocep.model.Cep;

import java.util.List;

public interface ICepDAO {

    public boolean salvar(Cep cep);

    public boolean atualizar(Cep cep);

    public boolean deletar(Cep cep);

    public List<Cep> listar();


}
