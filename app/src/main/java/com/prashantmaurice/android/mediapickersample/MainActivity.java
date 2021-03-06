package com.prashantmaurice.android.mediapickersample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.prashantmaurice.android.mediapicker.ExternalInterface.CustomFolder;
import com.prashantmaurice.android.mediapicker.ExternalInterface.ResultData;
import com.prashantmaurice.android.mediapicker.ExternalInterface.SelectedMedia;
import com.prashantmaurice.android.mediapicker.MediaPicker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 *
 *
 * ---------------------
 * content://com.android.providers.media.documents/document/image%3A23969
 *
 * BitmapFactory.Options options = new BitmapFactory.Options();
 * InputStream inputStream = getContentResolver().openInputStream( imageUri );
 * Bitmap bmp = BitmapFactory.decodeStream(inputStream, null, options);
 * if( inputStream != null ) inputStream.close();
 * ---------------------
 *
 *
 */
public class MainActivity extends AppCompatActivity {

    View btn_selectmultiple, btn_selectdefault,btn_selectcamera, btn_selectvideo,btn_customfolder;
    LinearLayout imageGallery;

    static final int REQUEST_PICK_MULTIPLE = 1001;
    static final int REQUEST_PICK_DEFAULTGALLERY = 1002;
    static final int REQUEST_PICK_CAMERA = 1003;
    static final int REQUEST_PICK_VIDEO = 1004;

    static final String CUSTOMALBUM_1 = "custom1";
    static final String CUSTOMALBUM_2 = "custom2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeListeners();
    }

    private void initializeViews() {
        btn_selectmultiple = findViewById(R.id.btn_selectmultiple);
        btn_selectdefault = findViewById(R.id.btn_selectdefault);
        btn_selectcamera = findViewById(R.id.btn_selectcamera);
        btn_customfolder = findViewById(R.id.btn_customfolder);
        imageGallery = (LinearLayout) findViewById(R.id.imageGallery);
        btn_selectvideo = findViewById(R.id.btn_selectvideo);
    }

    private void initializeListeners() {
        btn_selectmultiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new MediaPicker.IntentBuilder()
                        .pick(MediaPicker.Pick.IMAGE_VIDEO)
                        .from(MediaPicker.From.GALLERY_AND_CAMERA)
                        .selectMultiple(true)
                        .showNumberingStartFrom(3)
                        .setMaximumImageCount(5)
                        .build(MainActivity.this);
                startActivityForResult(intent, REQUEST_PICK_MULTIPLE);
            }
        });
        btn_selectdefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("image/*");
//                startActivityForResult(intent, REQUEST_PICK_DEFAULTGALLERY);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_PICK_DEFAULTGALLERY);
            }
        });

        btn_selectcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new MediaPicker.IntentBuilder()
                        .pick(MediaPicker.Pick.IMAGE)
                        .from(MediaPicker.From.CAMERA)
                        .build(MainActivity.this);
                startActivityForResult(intent, REQUEST_PICK_CAMERA);
            }
        });

        btn_selectvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new MediaPicker.IntentBuilder()
                        .pick(MediaPicker.Pick.VIDEO)
                        .from(MediaPicker.From.GALLERY)
                        .selectMultiple(false)
                        .setMaximumFileSize(5000)
                        .build(MainActivity.this);
                startActivityForResult(intent, REQUEST_PICK_VIDEO);
            }
        });

        btn_customfolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new MediaPicker.IntentBuilder()
                        .pick(MediaPicker.Pick.IMAGE)
                        .from(MediaPicker.From.GALLERY_AND_CAMERA)
                        .addCustomFolder(CustomFolder.Builder.generate("Custom folder 1",12,"http://i.imgur.com/DvpvklR.png",CUSTOMALBUM_1))
                        .addCustomFolder(CustomFolder.Builder.generate("Custom folder 2",1,"http://i.imgur.com/DvpvklR.png",CUSTOMALBUM_2))
                        .selectMultiple(true)
                        .showNumberingStartFrom(3)
                        .setMaximumImageCount(5)
                        .build(MainActivity.this);
                startActivityForResult(intent, REQUEST_PICK_MULTIPLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            switch (requestCode){
                case REQUEST_PICK_MULTIPLE:
                    ResultData dataObject = MediaPicker.getResultData(data);
                    if(dataObject.isCustomSelected()){
                        switch (dataObject.getCustomSelected()){
                            case CUSTOMALBUM_1:
                                Utils.showToast(this,"Custom option "+CUSTOMALBUM_1+" selected");
                                break;
                            case CUSTOMALBUM_2:
                                Utils.showToast(this,"Custom option "+CUSTOMALBUM_2+" selected");
                                break;
                        }
                    }

                    List<SelectedMedia> selectedMedia = dataObject.getSelectedMedias();

//                    List<Uri> picUris = dataObject.getSelectedOriginalUri();
                    addImagesToThegallery(selectedMedia);
                    if(selectedMedia.size()>0){
                        Uri uri = selectedMedia.get(0).getOriginalUri();// content://media/external/images/media/23969
                        Utils.showToast(this,"Picked "+uri.getPath());
                    }
                    Utils.showToast(this,"Picked "+selectedMedia.size()+" images");

                    break;
                case REQUEST_PICK_DEFAULTGALLERY:

                    //  content://com.android.providers.media.documents/document/image%3A42617
                    //  content://com.android.providers.media.documents/document/image%3A39853
                    //  content://com.android.providers.media.documents/document/image%3A23969
                    Uri uri = data.getData();
                    List<Uri> picUris2 = new ArrayList<>();
                    picUris2.add(uri);
                    addImagesToThegalleryFromUri(picUris2);
                    Utils.showToast(this,"Picked "+uri.getPath());
                    break;
                case REQUEST_PICK_CAMERA:
                    ResultData dataObject2 = MediaPicker.getResultData(data);
                    List<SelectedMedia> selectedMedia2 = dataObject2.getSelectedMedias();

//                    List<Uri> picUris = dataObject.getSelectedOriginalUri();
                    addImagesToThegallery(selectedMedia2);
                    if(selectedMedia2.size()>0){
                        Uri uri2 = selectedMedia2.get(0).getOriginalUri();// content://media/external/images/media/23969
                        Utils.showToast(this,"Picked "+uri2.getPath());
                    }
                    Utils.showToast(this,"Picked "+selectedMedia2.size()+" images");
                    break;
                case REQUEST_PICK_VIDEO:
                    ResultData dataObject3 = MediaPicker.getResultData(data);
                    List<SelectedMedia> selectedMedia3 = dataObject3.getSelectedMedias();

                    addImagesToThegallery(selectedMedia3);
                    if(selectedMedia3.size()>0){
                        Uri uri2 = selectedMedia3.get(0).getOriginalUri();// content://media/external/images/media/23969
                        Utils.showToast(this,"Picked "+uri2.getPath());
                    }
                    Utils.showToast(this,"Picked "+selectedMedia3.size()+" videos");
                    break;

            }
        }else{
            Utils.showToast(this,"Cancelled Request");
        }
    }

    private void addImagesToThegallery(List<SelectedMedia> selectedMedia) {
        imageGallery.removeAllViews();
        for (SelectedMedia media : selectedMedia) {
            try {
                imageGallery.addView(getImageView(media));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void addImagesToThegalleryFromUri(List<Uri> selectedUri) {
        imageGallery.removeAllViews();
        for (Uri imageUri : selectedUri) {
            try {
                imageGallery.addView(getImageView(imageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private View getImageView(SelectedMedia selectedMedia) throws IOException {
        if(selectedMedia.hasThumbNail()){
            return getImageView(selectedMedia.getThumbNail(this));
        }else{
            return getImageView(selectedMedia.getOriginalBitmap(this));
        }
    }

    private View getImageView(Bitmap bitmap){
        ImageView imageView = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 200);
        lp.setMargins(0, 0, 10, 0);
        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);
        return imageView;
    }

    private View getImageView(Uri imageUri) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream inputStream = getContentResolver().openInputStream( imageUri );
        Bitmap bmp = BitmapFactory.decodeStream(inputStream, null, options);
        if( inputStream != null ) inputStream.close();
        return getImageView(bmp);
    }
}
