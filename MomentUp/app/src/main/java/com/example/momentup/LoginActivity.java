package com.example.momentup;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.momentup.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        // 상태바 색상 설정
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void setupListeners() {
        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = binding.etId.getText().toString();
                String password = binding.etPassword.getText().toString();

                if (validateInput(id, password)) {
                    performLogin(id, password);
                }
            }
        });

        // 아이디 찾기
        binding.tvFindId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 아이디 찾기 화면으로 이동
                // Intent intent = new Intent(LoginActivity.this, FindIdActivity.class);
                // startActivity(intent);
            }
        });

        // 비밀번호 찾기
        binding.tvFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 비밀번호 찾기 화면으로 이동
                // Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                // startActivity(intent);
            }
        });

        // 회원가입
        binding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 회원가입 화면으로 이동
                // Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                // startActivity(intent);
            }
        });

        // 아이디 저장 체크박스
        binding.cbRememberId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // SharedPreferences에 아이디 저장 상태 저장
                SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("remember_id", isChecked);
                if (!isChecked) {
                    editor.remove("saved_id");
                }
                editor.apply();
            }
        });
    }

    private boolean validateInput(String id, String password) {
        if (id.isEmpty()) {
            binding.tilId.setError("아이디를 입력해주세요");
            return false;
        } else {
            binding.tilId.setError(null);
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("비밀번호를 입력해주세요");
            return false;
        } else {
            binding.tilPassword.setError(null);
        }

        return true;
    }

    private void performLogin(String id, String password) {
        // TODO: 실제 로그인 로직 구현
        // 예시: API 호출
        if (binding.cbRememberId.isChecked()) {
            // 아이디 저장하기가 체크되어 있으면 SharedPreferences에 아이디 저장
            SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("saved_id", id);
            editor.apply();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 저장된 아이디가 있다면 불러오기
        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        boolean rememberId = prefs.getBoolean("remember_id", false);
        if (rememberId) {
            String savedId = prefs.getString("saved_id", "");
            binding.etId.setText(savedId);
            binding.cbRememberId.setChecked(true);
        }
    }
}
