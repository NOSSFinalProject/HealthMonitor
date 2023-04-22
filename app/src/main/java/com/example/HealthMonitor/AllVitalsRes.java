package com.example.HealthMonitor;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AllVitalsRes extends AppCompatActivity {
    String selectedImagePath;
    File photoFile = null;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    String mCurrentPhotoPath;

    private static final String IMAGE_DIRECTORY_NAME = "VLEMONN";


    //Camera Storage Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    private String user, Date;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    Date today = Calendar.getInstance().getTime();
    int VHR, VO2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_vitals_res);
        Button cambutton = (Button) findViewById(R.id.btnCaptureImage);
        Button predbutton = (Button) findViewById(R.id.connectbtnpred);
        TextView responseText2 = findViewById(R.id.responseTextpred);
        TextView responseText = findViewById(R.id.responseText);
        Date = df.format(today);
        TextView VSHR = this.findViewById(R.id.HRV);
        TextView VSO2 = this.findViewById(R.id.O2V);
        //ImageButton All = this.findViewById(R.id.SendAll);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            VHR = bundle.getInt("bpm");
            VO2 = bundle.getInt("O2R");
            user = bundle.getString("Usr");

            VSHR.setText(String.valueOf(VHR));

            VSO2.setText(String.valueOf(VO2));
        }
        //Checking for storage
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        predbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest("POST", "prediction", "oxy", "pul", "temperature", Integer.toString(VO2), Integer.toString(VHR), responseText.getText().toString());
            }
        });

        cambutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Button isbutton = (Button) findViewById(R.id.imgbtncxn);
        isbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });

        Button cbutton = (Button) findViewById(R.id.connectbtn);
        cbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectServer(v);
            }
        });

        cambutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    captureImage();
                }
                else
                {
                    captureImage2();
                }
            }
        });
    }

    void connectServer2(View v){

        String ipv4Address = "192.168.1.35";
        //EditText portNumberView = findViewById(R.id.portNumber);
        //String portNumber = portNumberView.getText().toString();
        String portNumber = "5000";
        String postUrl= "http://"+ipv4Address+":"+portNumber+"/prediction";

        String postBodyText="Hello";
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        RequestBody postBody = RequestBody.create(mediaType, postBodyText);

        postRequest(postUrl, postBody);
    }

    void connectServer(View v) {
        //EditText ipv4AddressView = findViewById(R.id.IPAddress);
        //String ipv4Address = ipv4AddressView.getText().toString();
        String ipv4Address = "192.168.1.27";
        //EditText portNumberView = findViewById(R.id.portNumber);
        //String portNumber = portNumberView.getText().toString();
        String portNumber = "5000";
        String postUrl = "http://" + ipv4Address + ":" + portNumber + "/";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // Read BitMap by file path
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();

        TextView responseText = findViewById(R.id.responseText);
        responseText.setText("Please wait ...");

        postRequest(postUrl, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {
        ExecutorService mExecutor = Executors.newSingleThreadExecutor();
        Handler mHandler = new Handler(Looper.getMainLooper());
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();
        Runnable backgroundRunnable = new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Cancel the post on failure.
                        call.cancel();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Update the UI here
                                TextView responseText = findViewById(R.id.responseText);
                                responseText.setText("Failed to Connect to Server");
                                mExecutor.shutdown();

                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Update the UI here
                                TextView responseText = findViewById(R.id.responseText);
                                try {
                                    responseText.setText(response.body().string());
                                    mExecutor.shutdown();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });
            }
        };
        mExecutor.execute(backgroundRunnable);

    }

    public void selectImage(View v) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (resCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            selectedImagePath = getPath(getApplicationContext(), uri);
            EditText imgPath = findViewById(R.id.imgPath);
            imgPath.setText(selectedImagePath);
            //Toast.makeText(getApplicationContext(), selectedImagePath, Toast.LENGTH_LONG).show();
        }
        if (reqCode == CAPTURE_IMAGE_REQUEST && resCode == RESULT_OK) {
            //Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            //ImageView imageView= findViewById(R.id.imageView);
            //imageView.setImageBitmap(myBitmap);
            //Uri uri = data.getData();
            //EditText imgPath = findViewById(R.id.imgPath);
            //imgPath.setText(selectedImagePath);
        } else {
            displayMessage(getBaseContext(), "Request cancelled or something went wrong.");
        }
    }

    // Implementation of the getPath() method and all its requirements is taken from the StackOverflow Paul Burke's answer: https://stackoverflow.com/a/20559175/5426539
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
    private void captureImage2() {

        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = createImageFile4();
            if (photoFile != null) {
                displayMessage(getBaseContext(), photoFile.getAbsolutePath());
                selectedImagePath = photoFile.getAbsolutePath();
                EditText imgPath = findViewById(R.id.imgPath);
                imgPath.setText(selectedImagePath);
                //Toast.makeText(getApplicationContext(), selectedImagePath, Toast.LENGTH_LONG).show();

                //Log.i("Mayank",photoFile.getAbsolutePath());
                Uri photoURI = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
            }
        } catch (Exception e) {
            displayMessage(getBaseContext(), "Camera is not available." + e.toString());
        }
    }

    private void captureImage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {

                    photoFile = createImageFile();
                    selectedImagePath = photoFile.getAbsolutePath();
                    EditText imgPath = findViewById(R.id.imgPath);
                    imgPath.setText(selectedImagePath);
                    //ImageView imageView= findViewById(R.id.imageView);
                    //Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    //imageView.setImageBitmap(myBitmap);
                    displayMessage(getBaseContext(), photoFile.getAbsolutePath());
                    //Log.i("Mayank",photoFile.getAbsolutePath());

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.example.HealthMonitor.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                } catch (Exception ex) {
                    // Error occurred while creating the File
                    displayMessage(getBaseContext(), ex.getMessage().toString());
                }


            } else {
                displayMessage(getBaseContext(), "Nullll");
            }
        }


    }

    private File createImageFile4() {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                displayMessage(getBaseContext(), "Unable to create directory.");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void displayMessage(Context context, String message) {
        //Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    void sendRequest(String type, String method, String paramname1, String paramname2, String paramname3, String param1, String param2, String param3) {
        String ipv4Address = "192.168.1.27";
        //http://127.0.0.1:5000/prediction?oxy=99&pul=72&temperature=97
        //EditText portNumberView = findViewById(R.id.portNumber);
        //String portNumber = portNumberView.getText().toString();
        String portNumber = "5000";
        /* if url is of our get request, it should not have parameters according to our implementation.
         * But our post request should have 'name' parameter. */
        //String fullURL = "http://" + ipv4Address + ":" + portNumber + "/prediction" + "/" + method + (param1 == null ? "" : "/" + param1 + param2 == null ? "" : "/" + param2 + param3 == null ? "" : "/" + param3);
        String fullURL = "http://" + ipv4Address + ":" + portNumber + "/prediction";
        Request request;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).build();

        /* If it is a post request, then we have to pass the parameters inside the request body*/
        if (type.equals("POST")) {
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add(paramname1, param1);

// dynamically add more parameter like this:
            formBuilder.add(paramname2, param2);
            formBuilder.add(paramname3, param3);
            RequestBody formBody = formBuilder.build();
            request = new Request.Builder()
                    .url(fullURL)
                    .post(formBody)
                    .build();
        } else {
            /*If it's our get request, it doen't require parameters, hence just sending with the url*/
            request = new Request.Builder()
                    .url(fullURL)
                    .build();
        }
        /* this is how the callback get handled */
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                // Read data on the worker thread
                final String responseData = response.body().string();

                // Run view-related code back on the main thread.
                // Here we display the response message in our text view
                TextView responseText2 = findViewById(R.id.responseTextpred);
                AllVitalsRes.this.runOnUiThread(() -> responseText2.setText(responseData));
            }
        });}
        @Override
        public void onBackPressed () {
            super.onBackPressed();
            Intent i = new Intent(AllVitalsRes.this, HeathCheckMenu.class);
            i.putExtra("Usr", user);
            startActivity(i);
            finish();
        }

}

