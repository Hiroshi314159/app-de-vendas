package com.trabalho.appvendas.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;
import com.trabalho.appvendas.R;
import com.trabalho.appvendas.helper.ConfiguracaoFirebase;
import com.trabalho.appvendas.model.Anuncio;
import android.app.ProgressDialog;
import java.util.ArrayList;
import java.util.List;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText campoTitulo;
    private EditText campoDescricao;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private ImageView imagemAnuncio1, imagemAnuncio2, imagemAnuncio3;
    private ImageView selectedImageView;
    private Spinner campoEstado,campoCategoria;
    private List<Uri>listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlRecuperadas = new ArrayList<>();
    private Button btnCadastrarAnuncio;
    private Anuncio anuncio;
    private StorageReference storage;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        // Inicialização das componentes - vinculando variáveis a elementos de layout
        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);
        campoTelefone = findViewById(R.id.editTelefone);
        imagemAnuncio1 = findViewById(R.id.imagemAnuncio1);
        imagemAnuncio2 = findViewById(R.id.imagemAnuncio2);
        imagemAnuncio3 = findViewById(R.id.imagemAnuncio3);
        campoEstado = findViewById(R.id.spinnerEstado);
        campoCategoria = findViewById(R.id.spinnerCategoria);
        btnCadastrarAnuncio = findViewById(R.id.btnCadastrarAnuncio);
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        // Configuração do OnClickListener para cada ImageView
        imagemAnuncio1.setOnClickListener(this);
        imagemAnuncio2.setOnClickListener(this);
        imagemAnuncio3.setOnClickListener(this);

        // Inicializa a lista de fotos recuperadas


        // Carregar dados spinner
        carregarDadosSpinner();

        //Evento de click para o botão cadastrar anúncio
        btnCadastrarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDadosAnuncio();
            }
        });

    }

    public void carregarDadosSpinner(){
        String [] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapterEstado);

        String [] categorias = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter( adapterCategoria);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.imagemAnuncio1){
            abrirGaleria(imagemAnuncio1);
        }else if (view.getId() == R.id.imagemAnuncio2){
            abrirGaleria(imagemAnuncio2);
        }else if (view.getId() == R.id.imagemAnuncio3){
            abrirGaleria(imagemAnuncio3);
        }
    }



    // Método para abrir a galeria e selecionar uma imagem
    private void abrirGaleria(ImageView imageView) {
        // Armazena a ImageView que foi clicada para que possamos definir a imagem selecionada posteriormente
        selectedImageView = imageView;

        // Cria uma Intent para abrir a galeria de fotos
        // Intent.ACTION_PICK permite selecionar um item de dados, nesse caso, uma imagem
        Intent intent = new Intent(Intent.ACTION_PICK);

        // O MIME type "image/*" indica apenas imagens
        intent.setType("image/*");

        // Inicia a Activity de seleção de imagem com um código de solicitação (2),
        // que será verificado em onActivityResult para processar o resultado
        startActivityForResult(intent, 2);
    }

    // Método para processar o resultado da seleção de imagem
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica se o código de solicitação corresponde à seleção de imagem (2)
        // e se a operação foi bem-sucedida (RESULT_OK)
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            // Obtém o URI da imagem selecionada na galeria
            Uri selectedImage = data.getData();

            // Define o URI da imagem selecionada como o conteúdo da ImageView clicada,
            // exibindo a imagem selecionada na tela
            selectedImageView.setImageURI(selectedImage);
            // Adiciona o URI da imagem à lista de fotos recuperadas
            if (selectedImage != null) {
                listaFotosRecuperadas.add(selectedImage);
                Log.d("Fotos", "Número de fotos adicionadas: " + listaFotosRecuperadas.size());
            }
        }
    }

    public void salvarAnuncio() {
        // Criar e configurar o ProgressDialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("Salvando anúncio...");
        dialog.setCancelable(false); // Impede o fechamento do diálogo ao clicar fora
        dialog.show();

        //Salvar imagens no Storage
        //Percorre as 3 fotos e salva elas no storage
        for(int i = 0; i < listaFotosRecuperadas.size(); i++){
            String urlImagem = listaFotosRecuperadas.get(i).toString();
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i);
        }
    }

    private void salvarFotoStorage(String urlString, int totalFotos, int contador) {
        String idAnuncio = anuncio.getIdAnuncio();
        Uri uriImagem = Uri.parse(urlString);

        // Criar nó no Storage
        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(idAnuncio)
                .child("imagem" + contador);

        Log.d("Caminho Firebase", "Caminho do Firebase Storage: " + imagemAnuncio.getPath());

        // Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(uriImagem);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Obtém a URL de download da imagem após o upload bem-sucedido
                imagemAnuncio.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri firebaseUrl) {
                        String urlConvertida = firebaseUrl.toString();
                        listaUrlRecuperadas.add( urlConvertida);
                        if(totalFotos == listaUrlRecuperadas.size()){
                            anuncio.setFotos(listaUrlRecuperadas);
                            anuncio.salvar();
                            dialog.dismiss();
                            finish();
                        }


                        Log.d("URL Imagem", "URL da imagem: " + urlConvertida);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CadastrarAnuncioActivity.this, "Falha ao obter URL de download", Toast.LENGTH_SHORT).show();
                        Log.i("INFO", "Falha ao obter URL de download: " + e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CadastrarAnuncioActivity.this, "Falha ao fazer upload", Toast.LENGTH_SHORT).show();
                Log.i("INFO", "Falha ao fazer upload: " + e.getMessage());
            }
        });
    }



    private Anuncio configurarAnuncio(){
        String titulo = campoTitulo.getText().toString();
        String valor = campoValor.getText().toString();
        String descricao = campoDescricao.getText().toString();
        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String telefone = campoTelefone.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setDescricao(descricao);
        anuncio.setTelefone(telefone);
        return anuncio;
    }



    //Validar dados do anúncios -> garantir que nenhum campo ficou em branco, antes do anúncio ser cadastrado
    public void validarDadosAnuncio(){
        anuncio = configurarAnuncio();
        String valor = String.valueOf(campoValor.getRawValue());
        if(listaFotosRecuperadas.size() != 0){
            if(!anuncio.getEstado().isEmpty()){
                if(!anuncio.getCategoria().isEmpty()){
                    if(!anuncio.getTitulo().isEmpty()){
                        if(!valor.isEmpty() && !valor.equals("0")){
                            if(!anuncio.getDescricao().isEmpty()){
                                if(!anuncio.getTelefone().isEmpty()){
                                    salvarAnuncio();
                                }else{
                                    Toast.makeText(this, "Preencha o campo telefone!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(this, "Preencha o campo descrição!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "Preencha o campo valor!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this, "Preencha o campo título!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Preencha o campo categoria!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Preencha o campo estado!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Selecione ao menos uma foto!", Toast.LENGTH_SHORT).show();
        }
    }
    // Método para salvar o anúncio, chamado ao clicar em um botão


}
