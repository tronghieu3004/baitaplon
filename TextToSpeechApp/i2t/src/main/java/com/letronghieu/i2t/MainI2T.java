package com.letronghieu.i2t;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class MainI2T extends AppCompatActivity {
    ImageView delete_all, scanner_document, copy;
    EditText data_document_scan;
    Uri imageUrl;
    TextRecognizer textRecognizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_i2_t);
        //gán id
        delete_all = findViewById(R.id.btn_delete_all);
        scanner_document = findViewById(R.id.btn_camera);
        copy = findViewById(R.id.btn_copy_all);
        data_document_scan = findViewById(R.id.tv_data_input);

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        //copyall
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = data_document_scan.getText().toString();
                if (text.isEmpty()) {
                    showToast("There is NO TEXT to copy ");
                }else {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(MainActivity.this.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Data",data_document_scan.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                    showToast("Text copy to clipboard");
                }
            }
        });

        //clear data
        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = data_document_scan.getText().toString();
                if (text.isEmpty()) {
                    showToast("There is NO TEXT to copy ");
                }else {
                    data_document_scan.setText("");
                }
            }
        });


        //scanner_document
        scanner_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK || requestCode == ImagePicker.REQUEST_CODE) {
            if (data!= null) {
                imageUrl = data.getData();
                showToast("Image selected");
                recognizeText();
            }

        }else {
            showToast("Image not selected");
        }
    }
    private void recognizeText(){
        if (imageUrl!= null) {
            try {
                InputImage inputImage = InputImage.fromFilePath(MainActivity.this,imageUrl);

                Task<Text> result = textRecognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                String recognizeText = text.getText();
                                data_document_scan.setText(recognizeText);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast(e.getMessage());
                            }
                        });
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    private void showToast(String textToast){
        Toast.makeText(this,textToast,Toast.LENGTH_SHORT).show();
    }
    }
}