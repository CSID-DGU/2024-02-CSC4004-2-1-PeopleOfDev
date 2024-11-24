package com.example.momentup;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FindPasswordFragment extends Fragment {
    private EditText editId;
    private EditText editEmail;
    private Button btnFind;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        editId = view.findViewById(R.id.edit_id);
        editEmail = view.findViewById(R.id.edit_email);
        btnFind = view.findViewById(R.id.btn_find);

        // Add text change listener
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFindButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editId.addTextChangedListener(textWatcher);
        editEmail.addTextChangedListener(textWatcher);

        // Set button click listener
        btnFind.setOnClickListener(v -> findPassword());

        // Set initial button state
        updateFindButtonState();
    }

    private void updateFindButtonState() {
        boolean isValid = !editId.getText().toString().trim().isEmpty()
                && !editEmail.getText().toString().trim().isEmpty();
        btnFind.setEnabled(isValid);
    }

    private void findPassword() {
        String id = editId.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        // TODO: Implement actual password finding logic
        // This is just a placeholder implementation
        if (isValidEmail(email)) {
            // API 호출 또는 데이터베이스 조회 로직 구현
            Toast.makeText(getContext(),
                    "입력하신 이메일로 임시 비밀번호를 발송했습니다.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(),
                    "유효하지 않은 이메일 주소입니다.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}