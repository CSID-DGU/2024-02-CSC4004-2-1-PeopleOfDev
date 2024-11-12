package com.example.momentup;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
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

        // Set find account link click listener
        findAccountLink.setOnClickListener(v -> showFindAccountDialog());
    }

    private void setInitialState() {
        // Clear inputs
        usernameInput.setText("");
        passwordInput.setText("");

        // Disable login button
        loginButton.setEnabled(false);
        loginButton.setAlpha(0.5f);
        loginButton.setBackgroundColor(ECEFF1);

        // Hide error message
        errorContainer.setVisibility(View.GONE);
    }

    private void updateLoginButtonState() {
        boolean hasUsername = !usernameInput.getText().toString().trim().isEmpty();
        boolean hasPassword = !passwordInput.getText().toString().trim().isEmpty();

        // Enable button only if both fields have input
        loginButton.setEnabled(hasUsername && hasPassword);
        loginButton.setAlpha(hasUsername && hasPassword ? 1.0f : 0.5f);

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

    private void showFindAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("계정 찾기")
                .setMessage("계정 찾기 화면으로 이동하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> {
                    // Navigate to account recovery screen
                })
                .setNegativeButton("취소", null)
                .show();
    }
}