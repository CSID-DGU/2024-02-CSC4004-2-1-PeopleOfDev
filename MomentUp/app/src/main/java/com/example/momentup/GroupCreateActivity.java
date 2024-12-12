package com.example.momentup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.material.imageview.ShapeableImageView;

import lombok.Getter;

public class GroupCreateActivity extends AppCompatActivity {
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

    @Getter private GroupRepository groupRepository;
    private ImageView backgroundImage;
    private ShapeableImageView imagePreview;
    private ImageButton buttonBack;
    private EditText editGroupName;
    private EditText editGroupTag;
    private EditText editGroupText;
    private Button submitButton;
    private ImageView pencilIcon;

    private Uri selectedBackgroundUri;
    private Uri selectedProfileUri;

    private enum ImageType {
        BACKGROUND,
        PROFILE
    }

    private ImageType lastClickedImageType;

    // CropImage 계약을 위한 ActivityResultLauncher 추가
    private final ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), result -> {
                if (result.isSuccessful()) {
                    Uri resultUri = result.getUriContent();
                    if (lastClickedImageType == ImageType.BACKGROUND) {
                        selectedBackgroundUri = resultUri;
                        backgroundImage.setImageURI(resultUri);
                    } else {
                        selectedProfileUri = resultUri;
                        imagePreview.setImageURI(resultUri);
                    }
                    validateInputs();
                } else if (result instanceof CropImage.CancelledResult) {
                    Toast.makeText(this, "이미지 선택이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "이미지 크롭에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        groupRepository = GroupRepository.builder()
                .context(this)
                .build();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        backgroundImage = findViewById(R.id.backgroundImage);
        imagePreview = findViewById(R.id.imagePreview);
        buttonBack = findViewById(R.id.buttonBack);
        editGroupName = findViewById(R.id.editGroupName);
        editGroupTag = findViewById(R.id.editGroupTag);
        editGroupText = findViewById(R.id.editGroupText);
        submitButton = findViewById(R.id.submitButton);
        pencilIcon = findViewById(R.id.pencilIcon);
    }

    private void setupListeners() {

        buttonBack.setOnClickListener(v -> onBackPressed());

        // 배경 이미지 선택
        pencilIcon.setOnClickListener(v -> {
            lastClickedImageType = ImageType.BACKGROUND;
            if (hasPermissions()) {
                startImageCropper();
            } else {
                requestPermissions();
            }
        });

        // 프로필 이미지 선택
        imagePreview.setOnClickListener(v -> {
            lastClickedImageType = ImageType.PROFILE;
            if (hasPermissions()) {
                startImageCropper();
            } else {
                requestPermissions();
            }
        });

        // 입력 필드 변경 감지
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateInputs();
            }
        };

        editGroupName.addTextChangedListener(textWatcher);
        editGroupTag.addTextChangedListener(textWatcher);
        editGroupText.addTextChangedListener(textWatcher);

        submitButton.setOnClickListener(v -> createGroup());
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
            boolean allGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    if (!shouldShowRequestPermissionRationale(permissions[i])) {
                        showPermissionSettingsDialog();
                        return;
                    }
                }
            }

            if (allGranted) {
                startImageCropper();
            } else {
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
                .setMessage("이미지 설정을 위해서는 설정에서 권한을 허용해주세요.")
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
        cropImageOptions.guidelines = CropImageView.Guidelines.ON;
        cropImageOptions.outputCompressFormat = Bitmap.CompressFormat.JPEG;
        cropImageOptions.outputCompressQuality = 85;

        if (lastClickedImageType == ImageType.PROFILE) {
            cropImageOptions.cropShape = CropImageView.CropShape.OVAL;
            cropImageOptions.aspectRatioX = 1;
            cropImageOptions.aspectRatioY = 1;
            cropImageOptions.fixAspectRatio = true;
        } else {
            cropImageOptions.cropShape = CropImageView.CropShape.RECTANGLE;
            cropImageOptions.aspectRatioX = 2;
            cropImageOptions.aspectRatioY = 1;
            cropImageOptions.fixAspectRatio = true;
        }

        cropImage.launch(new CropImageContractOptions(null, cropImageOptions));
    }

    private void validateInputs() {
        boolean isValid = !editGroupName.getText().toString().trim().isEmpty() &&
                !editGroupTag.getText().toString().trim().isEmpty() &&
                !editGroupText.getText().toString().trim().isEmpty() &&
                selectedBackgroundUri != null &&
                selectedProfileUri != null;

        submitButton.setEnabled(isValid);
        submitButton.setTextColor(getColor(isValid ? R.color.white : R.color.gray3));
    }

    private void createGroup() {
        String groupName = editGroupName.getText().toString().trim();
        String groupTag = editGroupTag.getText().toString().trim();
        String groupText = editGroupText.getText().toString().trim();

        // 로딩 중 상태 표시
        submitButton.setEnabled(false);

        // TODO: userNumber는 실제 로그인된 사용자의 번호로 교체필요
        Long userNumber = 1L;  // 임시값

        groupRepository.createGroup(
                groupName,
                groupTag,
                groupText,
                userNumber,
                selectedProfileUri,
                new GroupRepository.GroupCreationCallback() {
                    @Override
                    public void onSuccess(Long groupNumber) {
                        runOnUiThread(() -> {
                            Toast.makeText(GroupCreateActivity.this,
                                    "그룹이 성공적으로 생성되었습니다.",
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        runOnUiThread(() -> {
                            submitButton.setEnabled(true);
                            Toast.makeText(GroupCreateActivity.this,
                                    "그룹 생성에 실패했습니다: " + throwable.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                }
        );
    }
}