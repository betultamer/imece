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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;


import imece.betul.imece.Adapter.UploadListAdapter;

public class Phto extends AppCompatActivity {
private static final int PICK_IMG = 1;
private ArrayList<Uri> ImageList = new ArrayList<Uri>();
private ArrayList<String> UrlList= new ArrayList<>();
private int uploads = 0;
RecyclerView recyclerView;
    UploadListAdapter myUploadAdapter;
private DatabaseReference databaseReference;
private ProgressDialog progressDialog;
        int index = 0;
        TextView textView;
        Button choose,send;
    private Uri mImageUri;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phto);
         recyclerView = findViewById(R.id.recyclerview);
    LinearLayoutManager mLayout = new GridLayoutManager(this, 2);
    recyclerView.setLayoutManager(mLayout);
    myUploadAdapter = new UploadListAdapter( ImageList);
    recyclerView.setAdapter(myUploadAdapter);
    recyclerView.setHasFixedSize(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User_one");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading ..........");
        textView = findViewById(R.id.alert);
        choose = findViewById(R.id.choose);
        send = findViewById(R.id.upload);

        choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPictureDialog();

                }
        });
        send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        textView.setText("Please Wait ... If Uploading takes Too much time please the button again ");
                        progressDialog.show();
                        final StorageReference ImageFolder =  FirebaseStorage.getInstance().getReference().child("ImageFolder");
                        for (uploads=0; uploads < ImageList.size(); uploads++) {
                                Uri Image  = ImageList.get(uploads);
                                final StorageReference imagename = ImageFolder.child("image/"+Image.getLastPathSegment());

                                imagename.putFile(ImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                                String url = String.valueOf(uri);
                                                                UrlList.add(url);

                                                        }
                                                });

                                        }
                                });


                        }
                    SendLink(UrlList);


                }
        });
        }




protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == PICK_IMG) {
        if (resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();

                int CurrentImageSelect = 0;

                while (CurrentImageSelect < count) {
                    Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                    ImageList.add(imageuri);
                    CurrentImageSelect = CurrentImageSelect + 1;
                }
                if (ImageList.size() > 0) {
                    myUploadAdapter.notifyDataSetChanged();
                }
                textView.setVisibility(View.VISIBLE);
                textView.setText("You Have Selected " + ImageList.size() + " Pictures");

                choose.setVisibility(View.GONE);
            }

        }

            } else if (requestCode ==  CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            ImageList.clear();
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        mImageUri = result.getUri();
        ImageList.add(mImageUri);
        myUploadAdapter.notifyDataSetChanged();

            }


}

private void SendLink(ArrayList<String> url) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("UserOne");
       int size= url.size();
    HashMap<String, String> hashMap = new HashMap<>();
    if (size>0){
    for (int i = 0; i<size; i++){
        hashMap.put("link"+i, url.get(i));
    }
        databaseReference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {

        progressDialog.dismiss();
        textView.setText("Image Uploaded Successfully");
        send.setVisibility(View.GONE);
        ImageList.clear();
        }
        });


         }}


    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void choosePhotoFromGallary() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMG);
    }

    private void takePhotoFromCamera() {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(Phto.this);
    }
}