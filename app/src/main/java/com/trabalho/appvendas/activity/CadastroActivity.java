package com.trabalho.appvendas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.trabalho.appvendas.R;
import com.trabalho.appvendas.helper.ConfiguracaoFirebase;

public class CadastroActivity extends AppCompatActivity {
    private Button botaoAcessar;
    private EditText campoEmail;
    private EditText campoSenha;
    private Switch tipoAcesso;
    private FirebaseAuth autentificacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        autentificacao = ConfiguracaoFirebase.getFirebaseAutentificacao();
        botaoAcessar = findViewById(R.id.buttonAcesso);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        tipoAcesso = findViewById(R.id.switchAcesso);

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();
                //Verificar se os campos estão preenchidos
                if( !email.isEmpty()){
                    if( !senha.isEmpty()){
                        //Verificar o estado do switch: Marcado = Cadastro e Desmarcado = Login

                        if(tipoAcesso.isChecked()){//Cadastro:
                            autentificacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(CadastroActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                                        //Direcionar o usuário para a tela principal

                                    }else { //Estrutura try-catch para capturar exceções no cadastro
                                        String erroExcecao = "";
                                        try{
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            erroExcecao = "Digite uma senha mais forte!";
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            erroExcecao = "Por favor, digite um e-mail válido!";
                                        }catch (FirebaseAuthUserCollisionException e){
                                            erroExcecao = "Esta conta já está cadastrada!";
                                        }catch (Exception e){
                                            erroExcecao = "ao cadastrar o usuário: "+e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(CadastroActivity.this, "Erro: "+erroExcecao, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }else {//Login
                            autentificacao.signInWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if( task.isSuccessful()){
                                        Toast.makeText(
                                                CadastroActivity.this, "Logado com sucesso!", Toast.LENGTH_SHORT
                                        ).show();
                                        startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));
                                    }

                                }
                            });
                        }
                    }else {
                        Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CadastroActivity.this, "Preencha o e-mail!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}