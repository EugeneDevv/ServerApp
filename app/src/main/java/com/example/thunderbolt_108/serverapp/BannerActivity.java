package com.example.thunderbolt_108.serverapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.thunderbolt_108.serverapp.Common.Common;
import com.example.thunderbolt_108.serverapp.Model.Banner;
import com.example.thunderbolt_108.serverapp.Model.Food;
import com.example.thunderbolt_108.serverapp.ViewHolder.BannerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BannerActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rootLayout;

    FloatingActionButton fab;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference banners;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseRecyclerAdapter<Banner,BannerViewHolder> adapter;

    //Add New Banner
    MaterialEditText edtName,edtFoodId;
    FButton btnUpload,btnSelect;

    Banner newBanner;
    Uri filePath;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Vahika.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_banner);

        //Firebase
        db=FirebaseDatabase.getInstance();
        banners=db.getReference("Banner");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        //Init
        recyclerView=(RecyclerView)findViewById(R.id.recycler_banner);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rootLayout=(RelativeLayout)findViewById(R.id.rootLayout);

        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddBanner();
            }
        });
        
        loadListBanner();
    }

    private void loadListBanner() {
        FirebaseRecyclerOptions<Banner> allBanner=new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(banners,Banner.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(allBanner) {
            @Override
            protected void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull Banner model) {
                holder.banner_name.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(holder.banner_image);
            }

            @NonNull
            @Override
            public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view=LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.banner_layout,viewGroup,false);
                return new BannerViewHolder(view);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadListBanner();
    }

    private void showAddBanner() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(BannerActivity.this);
        alertDialog.setTitle("Add New Banner");
        alertDialog.setMessage("Please Fill Full Information");
        alertDialog.setCancelable(false);

        LayoutInflater inflater=this.getLayoutInflater();
        View v=inflater.inflate(R.layout.add_new_banner,null);

        edtFoodId=v.findViewById(R.id.edtFoodId);
        edtName=v.findViewById(R.id.edtFoodName);

        btnSelect=v.findViewById(R.id.btnSelect);
        btnUpload=v.findViewById(R.id.btnUpload);

        //Set Event for select picture from phone
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        //Set event for Uploading a picture after selection
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPicture();
            }
        });

        alertDialog.setView(v);
        alertDialog.setIcon(R.drawable.ic_laptop_black_24dp);

        //set button for dialog
        alertDialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edtName.getText().toString().trim().length()==0||
                        edtFoodId.getText().toString().trim().length()==0){
                    Toast.makeText(BannerActivity.this, "Please Enter All Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.dismiss();
                    if (newBanner != null)
                        banners.push().setValue(newBanner);
                    loadListBanner();
                }
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                newBanner=null;
                loadListBanner();
                Toast.makeText(BannerActivity.this, "Cancelled !!!", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    private void uploadPicture() {

        if (filePath!=null) {
            final android.app.AlertDialog dialog=new SpotsDialog.Builder().setContext(BannerActivity.this).build();
            dialog.show();
            dialog.setMessage("Uploading Image . . .");
            dialog.setCancelable(false);

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            dialog.dismiss();
                            Toast.makeText(BannerActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set value for new food if image uploadand we can get download link
                                    newBanner = new Banner();
                                    newBanner.setName(edtName.getText().toString());
                                    newBanner.setId(edtFoodId.getText().toString());
                                    newBanner.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(BannerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setTitle("Uploaded" + progress + "%");
                        }
                    });
        }
    }

    private void chooseImage() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Common.PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            filePath=data.getData();
            btnSelect.setText("Image Selected !");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE))
        {
            showUpdateBannerDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deleteBanner(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateBannerDialog(final String key, final Banner item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(BannerActivity.this);
        alertDialog.setTitle("Update Banner");
        alertDialog.setMessage("Please Fill Full Information");
        alertDialog.setCancelable(false);

        LayoutInflater inflater=this.getLayoutInflater();
        View edit_banner=inflater.inflate(R.layout.add_new_banner,null);

        edtName=edit_banner.findViewById(R.id.edtFoodName);
        edtFoodId=edit_banner.findViewById(R.id.edtFoodId);

        //Set Default Value For View
        edtName.setText(item.getName());
        edtFoodId.setText(item.getId());

        btnSelect=edit_banner.findViewById(R.id.btnSelect);
        btnUpload=edit_banner.findViewById(R.id.btnUpload);

        //Event For Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();  //Let user select image from gallery and save url of this image
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        alertDialog.setView(edit_banner);
        alertDialog.setIcon(R.drawable.ic_laptop_black_24dp);

        //Set Button
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                item.setName(edtName.getText().toString());
                item.setId(edtFoodId.getText().toString());

                //Make Update
                Map<String,Object>update=new HashMap<>();
                update.put("id",item.getId());
                update.put("name",item.getName());
                update.put("image",item.getImage());

                banners.child(key)
                        .updateChildren(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar.make(rootLayout,"Updated",Snackbar.LENGTH_SHORT).show();
                                loadListBanner();

                            }
                        });

                Snackbar.make(rootLayout,"Banner"+" "+item.getName()+" "+"Was Updated !!!",Snackbar.LENGTH_SHORT).show();
                loadListBanner();

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                loadListBanner();
                Toast.makeText(BannerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    private void changeImage(final Banner item) {
        if (filePath!=null) {
            final android.app.AlertDialog dialog=new SpotsDialog.Builder().setContext(BannerActivity.this).build();
            dialog.show();
            dialog.setMessage("Uploading Image . . .");
            dialog.setCancelable(false);

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            dialog.dismiss();
                            Toast.makeText(BannerActivity.this, "Image Updated Successfully", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set value for new category if image uploadand we can get download link
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(BannerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setTitle("Uploaded" + progress + "%");
                        }
                    });
        }
    }

    private void deleteBanner(final String key){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(BannerActivity.this);
        alertDialog.setTitle("Hey, "+Common.currentUser.getName());
        alertDialog.setMessage("Do You Want To Delete Banner?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                banners.child(key).removeValue();
                Toast.makeText(BannerActivity.this, "Banner Deleted Successfully", Toast.LENGTH_SHORT).show();

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Toast.makeText(BannerActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setIcon(R.mipmap.picture);
        alertDialog.show();
    }
}
