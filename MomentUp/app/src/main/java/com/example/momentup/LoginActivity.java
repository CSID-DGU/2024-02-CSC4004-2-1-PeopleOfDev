package com.example.momentup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button signupButton;
    private TextView errorMessage;
    private TextView findAccountLink;
    private ViewGroup errorContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);
        errorMessage = findViewById(R.id.error_message);
        findAccountLink = findViewById(R.id.find_account_link);
        errorContainer = findViewById(R.id.error_container);

        // Set initial state
        setInitialState();

        // Add text change listeners
        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set login button click listener
        loginButton.setOnClickListener(v -> attemptLogin());

        // 회원가입 버튼 클릭
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                //startActivity(intent);
            }
        });

        // 계정 찾기 text 중 일부만 다른 style 적용
        SpannableString findAccountText = new SpannableString("혹시 계정 정보가 기억나지 않으세요?");
        int start = 3;
        int end = 8;
        findAccountText.setSpan(new ForegroundColorSpan(this.getColor(R.color.primary)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        findAccountText.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        findAccountLink.setText(findAccountText);

        // Set find account link click listener
        findAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setInitialState() {
        // Clear inputs
        usernameInput.setText("");
        passwordInput.setText("");

        // Disable login button
        loginButton.setEnabled(false);

        // Hide error message
        errorContainer.setVisibility(View.GONE);
    }

    private void updateLoginButtonState() {
        boolean hasUsername = !usernameInput.getText().toString().trim().isEmpty();
        boolean hasPassword = !passwordInput.getText().toString().trim().isEmpty();

        // Enable button only if both fields have input
        if(hasUsername && hasPassword) {
            loginButton.setEnabled(true);
            loginButton.setTextColor(this.getColor(R.color.white));
        }
        else {
            loginButton.setEnabled(false);
            loginButton.setTextColor(this.getColor(R.color.gray3));
        }

        // Hide error when user starts typing
        errorContainer.setVisibility(View.GONE);
    }

    private void attemptLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Simulate login check (replace with actual login logic)
        if (username.equals("momentup111") && password.equals("password123")) {
            // Success - proceed to next screen
            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();
        } else {
            // Show error state
            showErrorState();
        }
    }

    private void showErrorState() {
        errorContainer.setVisibility(View.VISIBLE);
        errorMessage.setText("입력 정보를 확인해주세요.");

        // Shake animation for error feedback
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        errorContainer.startAnimation(shake);
    }
}