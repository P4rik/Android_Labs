package com.example.lab1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private EditText editText;
    private TextView textView;
    private Button buttonOk, buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = findViewById(R.id.radioGroup);
        editText = findViewById(R.id.textInputEditText);
        textView = findViewById(R.id.textView);
        buttonOk = findViewById(R.id.buttonOk);
        buttonCancel = findViewById(R.id.buttonCancel);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString().trim();
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (text.isEmpty()) {
                    Toast.makeText(MainActivity.this, "The text field cannot be empty!", Toast.LENGTH_LONG).show();
                } else if (selectedId == -1) {
                    Toast.makeText(MainActivity.this, "Please select a font size!", Toast.LENGTH_LONG).show();
                } else {

                    float textSize = 16;
                    if (selectedId == R.id.radioButtonSmall) {
                        textSize = 14;
                    } else if (selectedId == R.id.radioButtonMedium) {
                        textSize = 20;
                    } else if (selectedId == R.id.radioButtonLarge) {
                        textSize = 26;
                    }

                    textView.setText(text);
                    textView.setTextSize(textSize);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                textView.setText("");
                textView.setVisibility(View.INVISIBLE);
                radioGroup.clearCheck();
            }
        });
    }
}