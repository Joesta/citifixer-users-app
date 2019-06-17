package com.dso30bt.project2019.potapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Coordinates;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.UserReport;
import com.dso30bt.project2019.potapp.repository.UserImpl;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;
import com.dso30bt.project2019.potapp.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * Created by Joesta on 2019/06/05.
 */
public class ImageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "ImageActivity";

    private String currentPhotoPath;
    //widgets
    private ImageView potholeImage;
    private TextView tvLat;
    private TextView tvLng;
    private TextView tvDate;
    private Button btnUpload;
    private Button btnCancel;
    private String userEmail;


    //members
    private List<UserReport> userReportList;
    private Coordinates coordinates;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //get login details
        userEmail = SharedPreferenceManager.getUserEmail(this);
        initUI();
        dispatchTakePictureIntent();
    }

    private void initUI() {
        potholeImage = findViewById(R.id.potholeImageView);
        tvLat = findViewById(R.id.tvLat);
        tvLng = findViewById(R.id.tvLng);
        tvDate = findViewById(R.id.tvDate);
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
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

                            //save image uri
                            imageUri = Uri.fromFile(file);
                            //@Todo - uncomment to get actual image exif gps data
                            //final double[] imageCoordinates = getCoordinatesFromImageExit(file);
                            final double[] imageCoordinates = new double[]{-25.7499763, 28.2151983};
                            Log.d(TAG, "onActivityResult: coordinates. Lat " + imageCoordinates[0] + " lng " + imageCoordinates[1]);
                            coordinates = getCoordinates(imageCoordinates);
                            setUIValues(scaled, coordinates);
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
        return new Coordinates(new Date(), paramCoordinates[0], paramCoordinates[1]);
    }

    private void setUIValues(Bitmap scaled, Coordinates coordinates) {
        potholeImage.setImageBitmap(scaled); // set image

        String latitudeInfo = tvLat.getText() + " : " + coordinates.getLatitude();
        String longitudeInfo = tvLng.getText() + "  : " + coordinates.getLongitude();
        String dateInfo = tvDate.getText() + "  : " +
                new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(coordinates.getDate());

        tvLat.setText(latitudeInfo);
        tvLng.setText(longitudeInfo);
        tvDate.setText(dateInfo);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
            Utils.showToast(this, "Error. Failed to ready image metadata");
            System.out.println("NullPointerException Error: " + npe.getLocalizedMessage());
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
        Toast.makeText(this, "Upload button tapped", Toast.LENGTH_SHORT).show();
        UserImpl userImp = new UserImpl(this);
        Pothole pothole = new Pothole();
        pothole.setCoordinates(coordinates);
        pothole.setDescription("Pothole");

        File imageFile = new File(currentPhotoPath);
        //add pothole
        userImp.addPothole(pothole, imageFile);

    }
}
