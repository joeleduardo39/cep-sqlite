package com.example.aplicativocep.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicativocep.R;
import com.example.aplicativocep.model.Cep;

import java.util.List;

public class EnderecoAdapter extends RecyclerView.Adapter<EnderecoAdapter.MyViewHolder> {

    private List<Cep> listaCep;
    Context context;

    public EnderecoAdapter(List<Cep> listaCep, Context context) {
        this.listaCep = listaCep;
        this.context = context;
    }

    // onCreate configura os dados da visualizacao
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.endereco_item, parent, false);

        return new MyViewHolder(view);
    }

    //Cria a exibicao dos itens
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.rua.setText(listaCep.get(position).getLogradouro());
        holder.bairro.setText(listaCep.get(position).getBairro());
        holder.cidade.setText(listaCep.get(position).getLocalidade());
    }

    @Override
    public int getItemCount() {
        return listaCep.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView rua;
        TextView bairro;
        TextView cidade;

        //MyViewHolder vai localizar os componentes
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            rua = itemView.findViewById(R.id.tvMostrarResultado);
            bairro = itemView.findViewById(R.id.tvMostrarResultadoBairro);
            cidade = itemView.findViewById(R.id.tvMostrarResultadoCidade);

        }
    }


}
