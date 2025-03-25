package com.example.lab3.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3.Fragments.InputFragment;
import com.example.lab3.Fragments.ResultFragment;
import com.example.lab3.R;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new InputFragment())
                    .commit();
        }
    }

    public void onResult(String text, float textSize, String textSizeLabel) {
        saveToFile(text, textSizeLabel);

        ResultFragment resultFragment = ResultFragment.newInstance(text, textSize);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, resultFragment)
                .addToBackStack(null)
                .commit();
    }

    private void saveToFile(String text, String size) {
        String filename = "result.txt";
        String data = text + ";" + size + "\n";

        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_APPEND)) {
            fos.write(data.getBytes());
            fos.flush();
            Toast.makeText(this, "Data saved to file!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "File writing error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void openFileFragment() {
        Intent intent = new Intent(this, FileActivity.class);
        startActivity(intent);
    }


    public void clearForm() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new InputFragment())
                .commit();
    }

}
