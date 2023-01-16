package com.example.aplicativocep.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicativocep.adapters.EnderecoAdapter;
import com.example.aplicativocep.api.CepService;
import com.example.aplicativocep.databinding.ActivityMainBinding;
import com.example.aplicativocep.helper.CepDAO;
import com.example.aplicativocep.model.Cep;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*Created by Joel Eduardo
 **************************
 */
public class MainActivity extends AppCompatActivity {

    private static final Pattern CEP_MODELO = Pattern.compile("[0-9]{5}-[0-9]{3}");

    private ActivityMainBinding binding;
    private Retrofit retrofit;
    private Cep cep;
    private RecyclerView recyclerViewEndereco;
    private List<Cep> listaCep;
    private CepDAO cepDAO;
    private EnderecoAdapter enderecoAdapter;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerViewEndereco = binding.recyclerViewEndereco;
        listaCep = new ArrayList<>();
        configurarRecycler();
        configRetrofit();
        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewEndereco);

        binding.buttonBuscarCep.setOnClickListener(view -> recuperarCepRetrofit());

    }

    private void recuperarCepRetrofit() {
        if (binding.editTextCep.getText().toString().isEmpty()) {
            Toast.makeText(this, "Cep não pode ser vazio! Por favor, informe um Cep.", Toast.LENGTH_LONG).show();
            return;
        } else if (!CEP_MODELO.matcher(binding.editTextCep.getText()).matches()) {
            Toast.makeText(this, "CEP Inválido, digite novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        CepService cepService = retrofit.create(CepService.class);
        Call<Cep> call = cepService.recuperarCep(binding.editTextCep.getText().toString());

        //cria uma tarefa assincrona dentro de uma trhead para baixar as informacoes
        call.enqueue(new Callback<Cep>() {
            @Override
            public void onResponse(@NonNull Call<Cep> call, @NonNull Response<Cep> response) {
                if (response.isSuccessful()) {

                    cep = response.body();  //Retorno do model

                    if (cep.getLogradouro() == null) {
                        Toast.makeText(MainActivity.this, "Cep não encontrado! Digite um CEP válido.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    listaCep.add(cep);
                    salvarCepBd();
                    carregarListaCep();

                    Toast.makeText(MainActivity.this, "Cep encontrado com sucesso!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<Cep> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void configurarRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getApplicationContext()));
        recyclerViewEndereco.setLayoutManager(linearLayoutManager);
        enderecoAdapter = new EnderecoAdapter(listaCep, this);
        recyclerViewEndereco.setAdapter(enderecoAdapter);
    }

    private void configRetrofit() {
        //Configuracao do Retrofit para fazer o consumo do servico web
        retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void salvarCepBd() {
        cepDAO = new CepDAO(getApplicationContext());
        cepDAO.salvar(cep);
    }

    public void carregarListaCep() {

        cepDAO = new CepDAO(getApplicationContext());
        listaCep = cepDAO.listar();

        configurarRecycler();
    }

    protected void onStart() {
        carregarListaCep();
        super.onStart();
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            cep = listaCep.get(pos);

            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

            dialog.setTitle("Confirmar exclusão");
            dialog.setMessage("Deseja excluir o Cep?");

            dialog.setPositiveButton("Sim", (dialogInterface, i) -> {
                cepDAO = new CepDAO(getApplicationContext());
                listaCep.remove(pos); //Remove o item do Adapter
                enderecoAdapter.notifyItemRemoved(pos);
                if (cepDAO.deletar(cep)) {  //Remove o item do banco

                    Toast.makeText(getApplicationContext(),
                            "Sucesso ao excluir Cep!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Erro ao excluir cep!",
                            Toast.LENGTH_SHORT).show();
                }
            });
            dialog.setNegativeButton("Não", (dialogInterface, i) -> carregarListaCep());


            dialog.create();
            dialog.show();

        }
    };


}