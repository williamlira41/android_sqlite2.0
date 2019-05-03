package com.example.sqlite20;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {
    // declaração da variáveis para manipulação dos objetos
    EditText editRa, editNome, editCurso;
    Button btnAdd, btnSearch, btnList, btnEdit, btnSave, btnDelete;
    ListView lvAlunos;

    // cria uma variável para trabalhar com SQLite
    SQLiteDatabase db;

    // lista para armazenar os dados recuperados do banco de dados
    ArrayList<String> listaAlunos = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // identificação dos objetos da View
        editRa = findViewById(R.id.editRa);
        editNome = findViewById(R.id.editNome);
        editCurso = findViewById(R.id.editCurso);

        btnAdd = findViewById(R.id.btnAdd);
        btnSearch = findViewById(R.id.btnSearch);
        btnList = findViewById(R.id.btnList);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        lvAlunos = findViewById(R.id.listViewAlunos);

        // abre ou cria o banco de dados
        db = openOrCreateDatabase("AlunoDB", Context.MODE_PRIVATE, null);

        // cria a tabela se não existir, senaõ carrega para uso
        db.execSQL("CREATE TABLE IF NOT EXISTS aluno(ra VARCHAR, nome VARCHAR, curso VARCHAR);");

        // adiciona um registro no banco de dados
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("INSERT INTO aluno VALUES('" +
                        editRa.getText() + "','" +
                        editNome.getText() + "','" +
                        editCurso.getText() + "');");

                showMessage("Successo", "Aluno Incluído!");
                clearText();
            }
        });

        // pesquisa por um RA informado
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = db.rawQuery(
                        "SELECT * FROM aluno WHERE ra='" + editRa.getText() + "'", null);
                if (c.moveToFirst()) {
                    editNome.setText(c.getString(1));
                    editCurso.setText(c.getString(2));
                } else {
                    showMessage("Erro", "RA Inválido!");
                    clearText();
                }
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // apaga todos registros da lista
                listaAlunos.clear();

                Cursor c = db.rawQuery("SELECT * FROM aluno", null);
                while (c.moveToNext()) {
                    listaAlunos.add(c.getString(1));
                }

                Collections.sort(listaAlunos);

                // cria o adaptador para receber a lista de nomes (arraylist)
                ArrayAdapter<String> meuAdapter =
                        new ArrayAdapter<String>(
                                getApplicationContext(),
                                android.R.layout.simple_expandable_list_item_1,
                                listaAlunos
                        );

                // anexa o adaptador na ListView
                lvAlunos.setAdapter(meuAdapter);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = db.rawQuery(
                        "SELECT * FROM aluno WHERE ra='" + editRa.getText() + "'", null);

                // verifica se o registro existe
                if (c.moveToFirst()) {
                    db.execSQL("UPDATE aluno SET nome='" +
                            editNome.getText() + "', curso='" +
                            editCurso.getText()
                            + "' WHERE ra='" + editRa.getText() + "'");

                    showMessage("Successo", "Aluno Alterado com Sucesso!");
                } else {
                    showMessage("Erro", "RA Inválido!");
                }
                clearText();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editRa.getText().toString().trim().length() == 0) {
                    showMessage("Error", "Digite o RA!");
                    return;
                }
                Cursor c = db.rawQuery(
                        "SELECT * FROM aluno WHERE ra='" + editRa.getText() + "'", null);

                if (c.moveToFirst()) {
                    db.execSQL("DELETE FROM aluno WHERE ra='" + editRa.getText() + "'");
                    showMessage("Successo", "Registro Excluído!");
                } else {
                    showMessage("Error", "RA Inválido!");
                }
                clearText();
            }
        });
    }

    // método que gera uma caixa de Alerta
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Fechar", null);
        builder.setIcon(R.drawable.dialogerror_92823);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void clearText() {
        editRa.setText("");
        editNome.setText("");
        editCurso.setText("");
        editRa.requestFocus();
    }

}
