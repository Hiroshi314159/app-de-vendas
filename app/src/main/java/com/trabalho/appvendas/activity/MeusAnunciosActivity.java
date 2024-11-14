package com.trabalho.appvendas.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.trabalho.appvendas.R;
import com.trabalho.appvendas.adapter.AdapterAnuncios;
import com.trabalho.appvendas.databinding.ActivityMeusAnunciosBinding;
import com.trabalho.appvendas.helper.ConfiguracaoFirebase;
import com.trabalho.appvendas.helper.RecyclerItemClickListener;
import com.trabalho.appvendas.model.Anuncio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MeusAnunciosActivity extends AppCompatActivity {

    private ActivityMeusAnunciosBinding binding;
    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        //inicializarComponentes
        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);
        anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_anuncios")
                .child(ConfiguracaoFirebase.getIdUsuario());

        //Configurar o Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Configurar o botão flutuante
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurar a Recyclerview
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncios, this);
        recyclerAnuncios.setAdapter(adapterAnuncios);

        //Recuperar anuncios para o usuario
        recuperarAnuncios();

        //Adcionar evento de click no RecyclerView
        recyclerAnuncios.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerAnuncios, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                Anuncio anuncioSelecionado = anuncios.get(position);
                anuncioSelecionado.remover();
                adapterAnuncios.notifyDataSetChanged();
            }
        }));


    }

    public boolean onSupportNavigateUp() {
        // Inicia a AnunciosActivity quando o botão de voltar é clicado
        Intent intent = new Intent(MeusAnunciosActivity.this, AnunciosActivity.class);
        startActivity(intent);
        finish(); // Encerra a MeusAnunciosActivity para não retornar a ela
        return true;
    }

    private void recuperarAnuncios(){

        // Criar e configurar o ProgressDialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("Recuperando anúncios");
        dialog.setCancelable(false); // Impede o fechamento do diálogo ao clicar fora
        dialog.show();

        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Recuperar os Dados
                anuncios.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    anuncios.add(ds.getValue(Anuncio.class));
                }
                Collections.reverse( anuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
