package com.example.momentup;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    private EditText editTextName, editTextId, editTextPassword, editTextEmail;
    private Button buttonSignup;
    private ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // View 초기화
        editTextName = findViewById(R.id.editTextName);
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSignup = findViewById(R.id.buttonSignUp);
        buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(v -> finish());

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSignUpButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTextId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSignUpButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSignUpButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSignUpButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 회원가입 버튼 클릭 리스너
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력값 가져오기
                String name = editTextName.getText().toString().trim();
                String id = editTextId.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                // 입력값 검증
                if(validateInput(name, id, password, email)) {
                    // TODO: 회원가입 처리 로직 구현
                    // 서버 통신이나 로컬 DB 저장 등
                    Toast.makeText(SignUpActivity.this,
                            "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void requestPermission() {
    }

    private boolean validateInput(String name, String id,
                                  String password, String email) {
        if(name.isEmpty()) {
            editTextName.setError("이름을 입력해주세요.");
            return false;
        }
        if(id.isEmpty()) {
            editTextId.setError("아이디를 입력해주세요.");
            return false;
        }
        if(password.isEmpty()) {
            editTextPassword.setError("비밀번호를 입력해주세요.");
            return false;
        }
        if(email.isEmpty()) {
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

        // Enable button only if both fields have input
        if(hasUsername && hasId && hasPassword && hasEmail) {
            buttonSignup.setEnabled(true);
            buttonSignup.setTextColor(this.getColor(R.color.white));
        }
        else {
            buttonSignup.setEnabled(false);
            buttonSignup.setTextColor(this.getColor(R.color.gray3));
        }
    }
}