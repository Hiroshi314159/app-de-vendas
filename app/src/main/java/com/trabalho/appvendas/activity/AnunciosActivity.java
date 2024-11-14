package com.trabalho.appvendas.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.trabalho.appvendas.R;
import com.trabalho.appvendas.adapter.AdapterAnuncios;
import com.trabalho.appvendas.helper.ConfiguracaoFirebase;
import com.trabalho.appvendas.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autentificacao;
    private RecyclerView recyclerAnunciosPublicos;
    private AdapterAnuncios adapterAnunciosPublicos;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);
        //Inicializacão
        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);

        //Configurações Iniciais
        autentificacao = ConfiguracaoFirebase.getFirebaseAutentificacao();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios");

        //Configurar a Recyclerview
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnunciosPublicos = new AdapterAnuncios(listaAnuncios, this);
        recyclerAnunciosPublicos.setAdapter(adapterAnunciosPublicos);

        //Recuperar anuncios para a tela da activity
        recuperarAnunciosPublicos();

    }

    public void recuperarAnunciosPublicos() {
        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAnuncios.clear();  // Limpa a lista para garantir que ela não acumule dados duplicados

                for (DataSnapshot estados : snapshot.getChildren()) {
                    Log.d("AnunciosActivity", "Estado: " + estados.getKey());

                    for (DataSnapshot categorias : estados.getChildren()) {
                        Log.d("AnunciosActivity", "Categoria: " + categorias.getKey());

                        for (DataSnapshot anuncioSnapshot : categorias.getChildren()) {
                            Anuncio anuncio = anuncioSnapshot.getValue(Anuncio.class);
                            if (anuncio != null) {
                                listaAnuncios.add(anuncio);
                                Log.d("AnunciosActivity", "Anuncio adicionado: " + anuncio.getTitulo());
                            } else {
                                Log.e("AnunciosActivity", "Anuncio nulo encontrado em: " + anuncioSnapshot.getKey());
                            }
                        }
                    }
                }
                adapterAnunciosPublicos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AnunciosActivity", "Erro ao recuperar anúncios públicos: " + error.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
       //Verificar se o usuário está logado
        if(autentificacao.getCurrentUser() == null){//usuário deslogado -> aparecer itens de menu para o group deslogado
            menu.setGroupVisible(R.id.group_deslogado, true);
        }else{//usuário logado
            menu.setGroupVisible(R.id.group_logado, true);
        }

        return super.onPrepareOptionsMenu(menu);
    }
    //Controla as permissões do usuário, caso logado ou deslogado
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Items do menu -> ao clicar redirecionar para uma nova activity
        if(item.getItemId() == R.id.menu_cadastrar){//Abre a CadastroActivity
            startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
            finish();
            return true;
        }else if(item.getItemId() == R.id.menu_sair){//Desloga o usuário
            autentificacao.signOut();
            //Invalida os itens de menu caso seja clickado em sair -> retira os 3 pontinhos
            invalidateOptionsMenu();
            return true;
        }else if(item.getItemId() == R.id.menu_anuncios){
            startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}