package com.example.kamhi.ex15;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

    final int RESULT = 1;
    String imgDecodableString = "";
    final int PERMISSION_IMAGE = 1;
    final int PERMISSION_CONTACT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button pickImage = (Button) findViewById(R.id.newImage);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (checkPermission(PERMISSION_IMAGE))
                getImageFromGalery();

            }
        });

        Button pickContact = (Button) findViewById(R.id.contactDetails);
        pickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission(PERMISSION_CONTACT))
                    displayContactList();
            }
        });

    }

    private void getImageFromGalery(){
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT);
    }

    private void displayContactList(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.contactImage);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }}

        private boolean checkPermission(int permissionId) {
            // give whatever permission you want. for example i am taking--Manifest.permission.READ_PHONE_STATE
            switch (permissionId) {
                case PERMISSION_IMAGE:
                    if ((Build.VERSION.SDK_INT >= 23) && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)) {

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                        return false;

                    } else {
                        return true;
                    }
                case PERMISSION_CONTACT: if ((Build.VERSION.SDK_INT >= 23) && (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED)) {

                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 2);
                    return false;

                } else {
                    return true;
                }
                default:
                    return false;
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == 2) {
                Log.i("resultcode",""+requestCode);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("resultcode",""+requestCode);
                    getImageFromGalery();

                }
                else {
                    Toast.makeText(getApplicationContext(),  "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }







}
