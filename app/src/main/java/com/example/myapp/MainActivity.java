package com.example.myapp;
 
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.Manifest;
import java.io.File;
import android.widget.Toast;

import com.example.myapp.signing.KeyStoreHelper;

public class MainActivity extends Activity { 
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        
    }
    public void gen(View view) {
        KeyStoreHelper.Builder builder = new KeyStoreHelper.Builder();
        builder.setAlias("Rohit Verma");
        builder.setKeyPassword("12345678");
        builder.setStorePassword("12345678");
        builder.setCommonName("Rohit Verma");
        builder.setOrganizationName("Rohit Verma");
        builder.setOrganizationUnit("Rohit Verma");
        builder.setStateName("Bihar");
        builder.setValidityYears(25);
        builder.setCityOrLocalityName("Gaya");
        builder.setCountryCode("IN");
        builder.setStoreType(KeyStoreHelper.Type.JKS);
        builder.setKeySize(KeyStoreHelper.Size.S_1024);
        builder.setSigAlgorithm(KeyStoreHelper.SigAlgorithm.SHA1WITHRSA);
        builder.setKeyAlgorithm(KeyStoreHelper.Algorithm.RSA);
        builder.setOutputFile(new File("/storage/emulated/0/.TEST/keystore.jks"));
        try {
            KeyStoreHelper.generate(builder);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
} 
