package com.example.kamhi.ex15;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;

public class MainActivity extends Activity {

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
        startActivityForResult(galleryIntent, PERMISSION_IMAGE);
    }

    private void displayContactList(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent,PERMISSION_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == PERMISSION_IMAGE && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.contactImage);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            } else if (requestCode == PERMISSION_CONTACT && resultCode == RESULT_OK && null != data){
                ContactInfo cI = getContectInfo(data.getData());

                TextView firstName = (TextView) findViewById(R.id.fillFirstName);
                firstName.setText(cI.getFirstName());
                TextView lastName = (TextView) findViewById(R.id.fillLastName);
                lastName.setText(cI.getLastName());
                TextView phone = (TextView) findViewById(R.id.fillPhone);
                phone.setText(cI.getCellNumber());
                TextView adress = (TextView) findViewById(R.id.fillAdress);
                adress.setText(cI.getAdress());
                }
            else {
                Toast.makeText(this, "You haven't picked anything",
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

            if (requestCode == PERMISSION_IMAGE) {
                Log.i("resultcode",""+requestCode);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImageFromGalery();

                }    else if(requestCode == PERMISSION_CONTACT && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    displayContactList();
                }
                else {
                    Toast.makeText(getApplicationContext(),  "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

    private ContactInfo getContectInfo(Uri uriContact){
        long contactID = ContentUris.parseId(uriContact);
        ContactInfo ci = new ContactInfo();

        Cursor cursorDetails = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS},
                ContactsContract.Data.CONTACT_ID + " = ?",
                new String[]{Long.toString(contactID)},
                null);

        while (cursorDetails.moveToNext()){
            String rowType = cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.Data.MIMETYPE));
            switch (rowType){
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                    int phoneType = cursorDetails.getInt((cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                    ci.setCellNumber(cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    break;
                case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                    if (ci.adress == null || ci.adress.isEmpty())
                    ci.setAdress(cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                    break;
                case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                    if (ci.getFirstName() == null || ci.getFirstName().isEmpty())
                        ci.setFirstName(cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
                    if (ci.getLastName() == null || ci.getLastName().isEmpty())
                        ci.setLastName(cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
                    break;
            }
        }
        cursorDetails.close();
        return ci;
    }

    private class ContactInfo{
        String cellNumber;
        String firstName;
        String lastName;
        String adress;

        public String getCellNumber() {
            return cellNumber;
        }

        public void setCellNumber(String cellNumber) {
            this.cellNumber = cellNumber;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAdress() {
            return adress;
        }

        public void setAdress(String adress) {
            this.adress = adress;
        }
    }





}
