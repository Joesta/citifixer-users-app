package com.dso30bt.project2019.potapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Coordinates;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.repository.UserImpl;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;
import com.dso30bt.project2019.potapp.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;


/**
 * Created by Joesta on 2019/06/05.
 */
public class PotholeImageCaptureActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "PotholeImageCapture";

    private String currentPhotoPath;
    //widgets
    private ImageView potholeImage;
    private TextView tvDescription;
    private TextView tvLat;
    private TextView tvLng;
    private TextView tvDate;
    private Button btnUpload;
    private Button btnCancel;
    private String userEmail;
    private User user;


    //members
    private Coordinates coordinates;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //get login details
        userEmail = SharedPreferenceManager.getEmail(this);
        initUI();
        dispatchTakePictureIntent();
    }

    private void initUI() {
        potholeImage = findViewById(R.id.potholeImageView);
        tvDescription = findViewById(R.id.tvPotholeDescriptionText);
        tvLat = findViewById(R.id.tvLatitudeText);
        tvLng = findViewById(R.id.tvLongitudeText);
        tvDate = findViewById(R.id.tvDateText);
        btnUpload = findViewById(R.id.btnUpload);
        btnCancel = findViewById(R.id.btnCancel);

        registerButtons();
    }

    private void registerButtons() {
        btnUpload.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "dispatchTakePictureIntent: Errror occured while creating file: " + ex.getLocalizedMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.dso30bt.project2019.potapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(currentPhotoPath);
                        Bitmap bitmap
                                = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

                            //save image uri
                            imageUri = Uri.fromFile(file);
                            final double[] imageCoordinates = getCoordinatesFromImageExit(file);
                            if (imageCoordinates != null) {
                                //@Todo - uncomment to get actual image exif gps data
                                //final double[] imageCoordinates = new double[]{-25.7499763, 28.2151983};
                                Log.d(TAG, "onActivityResult: coordinates. Lat " + imageCoordinates[0] + " lng " + imageCoordinates[1]);
                                coordinates = getCoordinates(imageCoordinates);
                                setUIValues(scaled, coordinates);
                            }
                        } // end bitmap nullable check
                    } // end resultCode check
                    break;
                } // end case
            } // end switch

        } catch (Exception error) {
            error.printStackTrace();
            Log.d(TAG, "onActivityResult: " + error.getLocalizedMessage());
        }
    }

    private Coordinates getCoordinates(double[] paramCoordinates) {
        return new Coordinates(1, paramCoordinates[0], paramCoordinates[1]);
    }

    private void setUIValues(Bitmap scaled, Coordinates coordinates) {
        potholeImage.setImageBitmap(scaled); // set image

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        final String potholeDescription = "Pothole";
        tvDescription.setText(potholeDescription);
        tvLat.setText(String.valueOf(coordinates.getLatitude()));
        tvLng.setText(String.valueOf(coordinates.getLongitude()));
        tvDate.setText(timestamp.toString());

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private double[] getCoordinatesFromImageExit(File file) {
        double latlng[] = null;
        try {
            ExifInterface exifInterface = new ExifInterface(String.valueOf(file));
            latlng = exifInterface.getLatLong();
            double lat = Objects.requireNonNull(latlng)[0];
            double lng = latlng[1];

            Log.d(TAG, "getCoordinatesFromImageExit: Lat " + lat + " lng " + lng);
            return latlng;

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException: Error: " + e.getLocalizedMessage());
        } catch (NullPointerException npe) {
            Utils.showToast(this, "Error. Failed to read image metadata");
//            System.out.println("NullPointerException Error: " + npe.getLocalizedMessage());
            npe.printStackTrace();
        }
        return latlng;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpload:
                addNewPothole();
                break;
            case R.id.btnCancel:
                displayDialog();
                break;
            default:
                break;

        }
    }

    private void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Take another photo?");
        builder.setPositiveButton("Yes", (dialog, which) -> dispatchTakePictureIntent());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()).create();

        builder.show();
    }

    private void addNewPothole() {
        UserImpl userImp = new UserImpl(this);
        Pothole pothole = new Pothole();
        pothole.setCoordinates(coordinates);
        //pothole.setDescription("Pothole");

        File imageFile = new File(currentPhotoPath);
        //add pothole
        //userImp.addPotholeAndImage(pothole, imageFile);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotHome();
    }

    private void gotHome() {
        startActivity(new Intent(PotholeImageCaptureActivity.this, MainActivity.class));
    }
}
