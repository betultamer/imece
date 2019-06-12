package imece.betul.imece;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import imece.betul.imece.Commons.Common;

import java.util.ArrayList;


import imece.betul.imece.Adapter.UploadListAdapter;

import static java.security.AccessController.getContext;

public class PostMultipleActivity extends AppCompatActivity {
    private static final int PICK_IMG = 0;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private ArrayList<String> UrlList= new ArrayList<>();
    private int uploads = 0;
    RecyclerView recyclerView;
    UploadListAdapter myUploadAdapter;


    ImageButton choose;
    String miUrlOk = "";
    StorageReference storageRef;
    ImageView close, image_added;
    TextView post;
    EditText description;
    String il;


    int uploadCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_multiple);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager mLayout = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayout);
        myUploadAdapter = new UploadListAdapter(ImageList);
        recyclerView.setAdapter(myUploadAdapter);
        recyclerView.setHasFixedSize(true);



        choose = findViewById(R.id.choose);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_il);
        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cityArrayList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        storageRef = FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostMultipleActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                il = spinner.getSelectedItem().toString();
                uploadImage_10();
            }
        });
        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(PostMultipleActivity.this);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoFromCamera();
            }
        });

    }

    private void uploadImage_10() {
        final  ProgressDialog pd = new ProgressDialog(PostMultipleActivity.this);
        pd.setMessage("Paylaşılıyor");
        pd.show();

        if (ImageList != null) {
            for (uploads = 0; uploads < ImageList.size(); uploads++) {
                Uri Image = ImageList.get(uploads);
                final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                        + "." + (Image.getLastPathSegment()));


                fileReference.putFile(ImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String url = String.valueOf(uri);
                                UrlList.add(url);
                                uploadCount++;
                                Log.d("Image Upload", "Image " + uploadCount + " uploaded");

                            }
                        });

                    }

                }).addOnFailureListener(PostMultipleActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadCount++;
                        Log.d("Image Upload", "Image " + uploadCount + " upload failed");

                    }
                });


            }
            Thread thread = new Thread(){
                public void run(){
                    int count = uploadCount;
                    while (uploadCount < ImageList.size() ){
                        if (uploadCount != count){
                            Log.d("Image Upload", + uploadCount + " uploaded so far");
                            count = uploadCount;
                        }
                    }
                    Log.d("Image Upload", + uploadCount + " will be saved to Real time");
                    if (UrlList.size() > 0)
                        SendLink(UrlList);
                    PostMultipleActivity.this.runOnUiThread(new Runnable(){
                        public void run(){
                            pd.dismiss();
                        }
                    });
                }
            };
            thread.start();

        }}

            protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data)
            {
                super.onActivityResult(requestCode, resultCode, data);

            if (requestCode ==  CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

                 //   choose.setVisibility(View.GONE);

                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    ImageList.add( result.getUri());
                    myUploadAdapter.notifyDataSetChanged();

                }

                else {
                   Toast.makeText(this, "Paylaşılamadı!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }

            }





    private void SendLink(final ArrayList<String> url) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        final String postid = reference.push().getKey();
        int size = url.size();
        if (size > 0) {
            final HashMap<String, String> hash = new HashMap<>();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("postid", postid);
            hashMap.put("il", il);
            hashMap.put("description", description.getText().toString());
            hashMap.put("publisher", Common.currentuser.getId());

            for (int i = 0; i < size; i++) {
                hash.put("link" + i, url.get(i));
            }
            reference.child(postid).setValue(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                reference.child(postid).child("postimage").setValue(hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ImageList.clear();
                                        UrlList.clear();
                                        uploadCount = 0;
                                        startActivity(new Intent(PostMultipleActivity.this, MainActivity.class));
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        reference.child(postid).removeValue();
                                    }
                                });


                            }
                        }

                    });


        }
    }



    private void takePhotoFromCamera() {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(PostMultipleActivity.this);
    }

}


