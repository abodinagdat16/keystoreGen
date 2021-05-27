package com.example.myapp;

import android.app.Activity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;

public class DebugActivity extends Activity {

    private String error = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.error = getIntent().getStringExtra("error");

        if (error != null) 
            new AlertDialog.Builder(this)
                .setTitle("An error occured")
                .setMessage(error)
                .setPositiveButton("CLOSE APP", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface p1, int p2) {
						p1.dismiss();
                        finish();
					}
				})
                .setNegativeButton("COPY LOG", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface p1, int p2) {
						ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText(getPackageName(), error));
						p1.dismiss();
                        finish();
			        }
                })
                .setCancelable(false)
                .show();
    }
}

