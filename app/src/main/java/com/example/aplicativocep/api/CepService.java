package com.example.aplicativocep.api;

import com.example.aplicativocep.model.Cep;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CepService {

    @GET("{cep}/json/")
    Call<Cep> recuperarCep(@Path("cep") String cep);


}
