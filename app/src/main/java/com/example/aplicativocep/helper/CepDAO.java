package com.example.aplicativocep.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.aplicativocep.model.Cep;

import java.util.ArrayList;
import java.util.List;

public class CepDAO implements ICepDAO {

    private SQLiteDatabase escreve; //usado para escrever dados na tabela
    private SQLiteDatabase ler; //usado para ler dados na tabela

    public CepDAO(Context context) {
        DatabaseHelper db = new DatabaseHelper(context);
        escreve = db.getWritableDatabase();
        ler = db.getReadableDatabase();
    }

    @Override
    public boolean salvar(Cep cep) {

        ContentValues cv = new ContentValues();
        cv.put("logradouro", cep.getLogradouro());
        cv.put("bairro", cep.getBairro());
        cv.put("localidade", cep.getLocalidade());

        try {
            escreve.insert(DatabaseHelper.TABELA_CEP, null, cv);
            Log.e("INFO ", "Cep salvo com sucesso");

        } catch (Exception e) {
            Log.e("INFO ", "Erro ao salvar cep " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean atualizar(Cep cep) {
        return false;
    }

    @Override
    public boolean deletar(Cep cep) {

        try {
            String[] args = {cep.getId().toString()};
            escreve.delete(DatabaseHelper.TABELA_CEP, "id=?", args);
            Log.e("INFO ", "Cep removido com sucesso");

        } catch (Exception e) {
            Log.e("INFO ", "Erro ao remover cep " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public List<Cep> listar() {
        List<Cep> cepListDao = new ArrayList<>();

        String sql = "SELECT * FROM " + DatabaseHelper.TABELA_CEP + " ;";
        Cursor c = ler.rawQuery(sql, null);


        while (c.moveToNext()) {
            Cep cep = new Cep();

            Long id = c.getLong(0); //(c.getColumnIndex("id"));
            String logradouro = c.getString(1); //(c.getColumnIndex("logradouro"));
            String bairro = c.getString(2); //(c.getColumnIndex("bairro"));
            String localidade = c.getString(3); //(c.getColumnIndex("localidade"));

            cep.setId(id);
            cep.setLogradouro(logradouro);
            cep.setBairro(bairro);
            cep.setLocalidade(localidade);


            cepListDao.add(cep);
            Log.i("cepDao", cep.getLogradouro());
            Log.i("cepDaoB", cep.getBairro());
            Log.i("cepDaoL", cep.getLocalidade());

        }

        return cepListDao;
    }
}
