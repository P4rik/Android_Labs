package com.example.lab2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InputFragment extends Fragment {

    private EditText editText;
    private RadioGroup radioGroup;
    private Button buttonOk;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editText = view.findViewById(R.id.textInputEditText);
        radioGroup = view.findViewById(R.id.radioGroup);
        buttonOk = view.findViewById(R.id.buttonOk);

        buttonOk.setOnClickListener(v -> {
            String text = editText.getText().toString().trim();
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (text.isEmpty()) {
                Toast.makeText(getActivity(), "The text field cannot be empty!", Toast.LENGTH_LONG).show();
                return;
            }
            if (selectedId == -1) {
                Toast.makeText(getActivity(), "Please select a font size!", Toast.LENGTH_LONG).show();
                return;
            }

            float textSize = 16;
            if (selectedId == R.id.radioButtonSmall) {
                textSize = 24;
            } else if (selectedId == R.id.radioButtonMedium) {
                textSize = 36;
            } else if (selectedId == R.id.radioButtonLarge) {
                textSize = 48;
            }

            ((MainActivity) getActivity()).onResult(text, textSize);
        });
    }
}
