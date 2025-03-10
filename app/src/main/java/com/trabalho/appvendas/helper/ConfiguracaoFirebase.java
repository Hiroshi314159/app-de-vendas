package com.trabalho.appvendas.helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ConfiguracaoFirebase {
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth referenciaAutentificacao;
    private static StorageReference referenciaStorage;


    public static String getIdUsuario(){
        FirebaseAuth autentificacao = getFirebaseAutentificacao();
        return autentificacao.getCurrentUser().getUid();
    }

    //Retorna a referência do database
    public static DatabaseReference getFirebase(){
        if(referenciaFirebase == null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }
    //Retorna a instância do FirebaseAuth
    public static FirebaseAuth getFirebaseAutentificacao(){
        if(referenciaAutentificacao == null){
            referenciaAutentificacao = FirebaseAuth.getInstance();
        }
        return referenciaAutentificacao;
    }
    public static StorageReference getFirebaseStorage(){
        if(referenciaStorage == null){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }

}
