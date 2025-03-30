package com.example.denik;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.concurrent.futures.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hlavní aktivita aplikace "Denik".
 * Umožňuje:
 * 1) Pořídit fotku pomocí CameraX (pokud je k dispozici).
 * 2) Fallback na ACTION_IMAGE_CAPTURE, pokud CameraX není možná.
 * 3) Vybrat fotku z galerie (ACTION_PICK).
 * A zobrazuje vybranou/pořízenou fotku v ImageView.
 */
public class MainActivity extends AppCompatActivity {

    // Kódy pro requesty
    private static final int REQUEST_CAMERA_PERMISSION = 1001;  // Pro runtime camera permission
    private static final int REQUEST_IMAGE_CAPTURE = 1002;      // Fallback foto
    private static final int REQUEST_IMAGE_PICK = 1003;         // Výběr z galerie

    // UI prvky
    private ImageView photoImageView;       // Zde se ukáže náhled fotky
    private Button btnTakePhoto, btnChoosePhoto, btnCapture, btnCancelCamera;
    private View mainLayout;                // Hlavní layout (LinearLayout)
    private ConstraintLayout cameraLayout;  // Layout s náhledem kamery (CameraX)
    private PreviewView previewView;        // Komponenta pro zobrazení náhledu kamery

    // CameraX
    private ProcessCameraProvider cameraProvider;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    // Temp soubory
    private File photoFile;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        // Jednovláknový executor pro CameraX
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Tlačítko "Pořídit fotku" - zkusit spustit CameraX
        btnTakePhoto.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Oprávnění kamery není => požádáme
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                // Máme oprávnění => spustit CameraX
                startCameraX();
            }
        });

        // Tlačítko "Vybrat z galerie"
        btnChoosePhoto.setOnClickListener(view -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickIntent, REQUEST_IMAGE_PICK);
        });

        // "Vyfotit" (CameraX spoušť)
        btnCapture.setOnClickListener(view -> capturePhotoCameraX());

        // "Zrušit" režim CameraX
        btnCancelCamera.setOnClickListener(view -> closeCameraX());
    }

    /**
     * Inicializace všech UI prvků.
     */
    private void initUI() {
        photoImageView = findViewById(R.id.photoImageView);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnCapture = findViewById(R.id.btnCapture);
        btnCancelCamera = findViewById(R.id.btnCancelCamera);
        mainLayout = findViewById(R.id.mainLayout);
        cameraLayout = findViewById(R.id.cameraLayout);
        previewView = findViewById(R.id.previewView);
    }

    /**
     * Spuštění CameraX (náhled + ImageCapture).
     * Pokud se CameraX nepodaří inicializovat, volá se fallback.
     */
    private void startCameraX() {
        // Skrýt hlavní layout, zobrazit layout kamery
        mainLayout.setVisibility(View.GONE);
        cameraLayout.setVisibility(View.VISIBLE);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                // Zadní kamera
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Náhled
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Objekt pro pořízení fotky
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                // Odpojit staré cesty a znovu připojit na lifecykl aktivity
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                // CameraX nepodporována / selhala
                Log.e("CameraX", "startCameraX error: " + e.getMessage(), e);
                fallbackCameraIntent();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Pořízení fotky v režimu CameraX.
     */
    private void capturePhotoCameraX() {
        if (imageCapture == null) {
            Toast.makeText(this, "CameraX není připravená.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Vytvoříme soubor pro uloženou fotku
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        photoFile = new File(getFilesDir(), "photo_" + timeStamp + ".jpg");
        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // Fotka je zapsána
                runOnUiThread(() -> {
                    // Zobrazit v ImageView
                    Uri uri = Uri.fromFile(photoFile);
                    photoImageView.setImageURI(uri);
                    closeCameraX();
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                // Chyba ukládání
                Log.e("CameraX", "capturePhotoCameraX: " + exception.getMessage(), exception);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Chyba při ukládání fotky", Toast.LENGTH_SHORT).show();
                    closeCameraX();
                });
            }
        });
    }

    /**
     * Zavřít režim CameraX a přepnout zpátky na hlavní layout.
     */
    private void closeCameraX() {
        cameraLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }

    /**
     * Fallback: ACTION_IMAGE_CAPTURE, pokud CameraX nejde spustit.
     * Uloží fotku do photoFile a předá URI pomocí FileProvider.
     */
    private void fallbackCameraIntent() {
        cameraLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Vytvoříme soubor
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            photoFile = new File(getFilesDir(), "photo_" + timeStamp + ".jpg");
            photoUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", photoFile);

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Nelze najít aplikaci fotoaparátu", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Výsledek žádosti o oprávnění ke kameře.
     * Pokud schváleno => startCameraX(), jinak => fallback.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Můžeme spustit CameraX
                startCameraX();
            } else {
                // Oprávnění zamítnuto => fallback
                fallbackCameraIntent();
            }
        }
    }

    /**
     * Zpracování výsledku fallback foto + výběru z galerie.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Výsledek fallback fotoaparátu
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Fotka by měla být v photoFile
            if (photoFile != null && photoFile.exists()) {
                photoImageView.setImageURI(Uri.fromFile(photoFile));
            }
            // Některé starší fotoaparáty vrací i thumbnail v data.getExtras()
            else if (data != null && data.getExtras() != null) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                if (thumbnail != null) {
                    photoImageView.setImageBitmap(thumbnail);
                }
            }
        }
        // Výsledek výběru z galerie
        else if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                photoImageView.setImageURI(selectedImage);
            }
        }
    }

    /**
     * Pokud uživatel stiskne zpět a je otevřen cameraLayout, zavřeme ho.
     */
    @Override
    public void onBackPressed() {
        if (cameraLayout.getVisibility() == View.VISIBLE) {
            closeCameraX();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Uvolnění zdrojů cameraExecutor při zničení aktivity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}
