package com.example.momentup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;

public class SignUpActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String[] REQUIRED_PERMISSIONS;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
    }

    private ImageView profileImageView;
    private EditText editTextName, editTextId, editTextPassword, editTextEmail;
    private Button buttonSignup;
    private ImageButton buttonBack;
    private Uri profileImageUri;

    private final ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), result -> {
                if (result.isSuccessful()) {
                    // 크롭된 이미지 URI 가져오기
                    profileImageUri = result.getUriContent();
                    // ImageView에 크롭된 이미지 설정
                    profileImageView.setImageURI(profileImageUri);
                } else if (result instanceof CropImage.CancelledResult) {
                    Toast.makeText(this, "이미지 선택이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "이미지 크롭에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // View 초기화
        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setClickable(true);
        profileImageView.setFocusable(true);

        editTextName = findViewById(R.id.editTextName);
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSignup = findViewById(R.id.buttonSignUp);
        buttonBack = findViewById(R.id.buttonBack);
    }

    private void setupListeners() {

        View.OnClickListener profileClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionsAndStartCropper();
            }
        };

        // 리스너 설정
        profileImageView.setOnClickListener(profileClickListener);

        // 뒤로가기 버튼 리스너
        buttonBack.setOnClickListener(v -> finish());

        // EditText 리스너들 설정
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSignUpButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        editTextName.addTextChangedListener(textWatcher);
        editTextId.addTextChangedListener(textWatcher);
        editTextPassword.addTextChangedListener(textWatcher);
        editTextEmail.addTextChangedListener(textWatcher);

        // 회원가입 버튼 클릭 리스너
        buttonSignup.setOnClickListener(v -> handleSignUp());
    }

    private void checkPermissionsAndStartCropper() {
        if (hasPermissions()) {
            startImageCropper();
        } else {
            requestPermissions();
        }
    }

    private boolean hasPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 권한이 모두 승인되었는지 확인
            boolean allGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    // 권한이 거부되었고, 다시 묻지 않기가 선택되었는지 확인
                    if (!shouldShowRequestPermissionRationale(permissions[i])) {
                        // 설정으로 이동하도록 안내
                        showPermissionSettingsDialog();
                        return;
                    }
                }
            }

            if (allGranted) {
                startImageCropper();
            } else {
                // 일반적인 권한 거부시 설명과 함께 다시 요청
                new AlertDialog.Builder(this)
                        .setTitle("권한 필요")
                        .setMessage("사진 촬영 및 이미지 선택을 위해서는 카메라와 저장소 접근 권한이 필요합니다.")
                        .setPositiveButton("다시 시도", (dialog, which) -> requestPermissions())
                        .setNegativeButton("취소", null)
                        .show();
            }
        }
    }

    private void showPermissionSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("권한 설정")
                .setMessage("프로필 이미지 설정을 위해서는 설정에서 권한을 허용해주세요.")
                .setPositiveButton("설정으로 이동", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void startImageCropper() {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeCamera = true;
        cropImageOptions.imageSourceIncludeGallery = true;
        cropImageOptions.cropShape = CropImageView.CropShape.OVAL;
        cropImageOptions.guidelines = CropImageView.Guidelines.ON;
        cropImageOptions.outputCompressFormat = android.graphics.Bitmap.CompressFormat.JPEG;
        cropImageOptions.outputCompressQuality = 85;

        // 1:1 비율 고정 설정
        cropImageOptions.aspectRatioX = 1;
        cropImageOptions.aspectRatioY = 1;
        cropImageOptions.fixAspectRatio = true; // 비율 고정

        CropImageContractOptions cropImageContractOptions =
                new CropImageContractOptions(null, cropImageOptions);

        cropImage.launch(cropImageContractOptions);
    }

    private void handleSignUp() {
        // 입력값 가져오기
        String name = editTextName.getText().toString().trim();
        String id = editTextId.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        // 입력값 검증
        if (validateInput(name, id, password, email)) {
            if (profileImageUri == null) {
                Toast.makeText(this, "프로필 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: 회원가입 처리 로직 구현
            // 서버 통신이나 로컬 DB 저장 등
            // profileImageUri를 사용하여 이미지 처리

            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean validateInput(String name, String id, String password, String email) {
        if (name.isEmpty()) {
            editTextName.setError("이름을 입력해주세요.");
            return false;
        }
        if (id.isEmpty()) {
            editTextId.setError("아이디를 입력해주세요.");
            return false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("비밀번호를 입력해주세요.");
            return false;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("이메일을 입력해주세요.");
            return false;
        }
        return true;
    }

    private void updateSignUpButtonState() {
        boolean hasUsername = !editTextName.getText().toString().trim().isEmpty();
        boolean hasId = !editTextId.getText().toString().trim().isEmpty();
        boolean hasPassword = !editTextPassword.getText().toString().trim().isEmpty();
        boolean hasEmail = !editTextEmail.getText().toString().trim().isEmpty();

        if (hasUsername && hasId && hasPassword && hasEmail) {
            buttonSignup.setEnabled(true);
            buttonSignup.setTextColor(this.getColor(R.color.white));
        } else {
            buttonSignup.setEnabled(false);
            buttonSignup.setTextColor(this.getColor(R.color.gray3));
        }
    }
}