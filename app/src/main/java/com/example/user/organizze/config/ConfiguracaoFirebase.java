package com.example.user.organizze.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {
    private static FirebaseAuth autenticacao;
    private static DatabaseReference storage;

    public static FirebaseAuth getFirebaseAutenticacao(){
        if(autenticacao == null)
            autenticacao = FirebaseAuth.getInstance();

        return autenticacao;
    }

    public static DatabaseReference getFirebaseDatabase(){
        if(storage == null){
            storage = FirebaseDatabase.getInstance().getReference();
        }

        return storage;
    }
}
