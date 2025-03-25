package com.example.lab3.Fragments;

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

import com.example.lab3.Activities.MainActivity;
import com.example.lab3.R;

public class InputFragment extends Fragment {

    private EditText editText;
    private RadioGroup radioGroup;
    private Button buttonOk, buttonOpen;


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
        buttonOpen = view.findViewById(R.id.buttonOpen);


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
            String textSizeLabel = "Small";

            if (selectedId == R.id.radioButtonSmall) {
                textSize = 24;
                textSizeLabel = "Small";
            } else if (selectedId == R.id.radioButtonMedium) {
                textSize = 36;
                textSizeLabel = "Medium";
            } else if (selectedId == R.id.radioButtonLarge) {
                textSize = 48;
                textSizeLabel = "Large";
            }

            ((MainActivity) getActivity()).onResult(text, textSize, textSizeLabel);

        });
        buttonOpen.setOnClickListener(v -> ((MainActivity) getActivity()).openFileFragment());

    }
}
