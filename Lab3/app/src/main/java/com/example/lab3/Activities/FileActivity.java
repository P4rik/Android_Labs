package com.example.lab3.Activities;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileActivity extends AppCompatActivity {

    private static final String SAVED_FILE = "result.txt";
    private TableLayout tableLayout;
    private Button buttonClear, buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        buttonBack = findViewById(R.id.buttonBack);
        buttonClear = findViewById(R.id.buttonClear);
        tableLayout = findViewById(R.id.tableLayout);

        loadTableData();

        buttonClear.setOnClickListener(v -> clearFile());

        buttonBack.setOnClickListener(v -> finish());
    }

    private void loadTableData() {
        File file = new File(getFilesDir(), SAVED_FILE);
        tableLayout.removeAllViews();

        if (!file.exists()) {
            showEmptyMessage();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            insertTableHeader();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    addTableRow(tableLayout, parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showEmptyMessage() {
        TextView message = new TextView(this);
        message.setText("The storage is empty");
        message.setTextColor(getResources().getColor(R.color.white));
        message.setTextSize(20);
        message.setGravity(Gravity.CENTER);
        tableLayout.removeAllViews();
        tableLayout.addView(message);
    }

    private void clearFile() {
        File file = new File(getFilesDir(), SAVED_FILE);

        if (file.exists()) {
            file.delete();
            Toast.makeText(this, "All saved data has been deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "The storage is already empty", Toast.LENGTH_LONG).show();
        }

        tableLayout.removeAllViews();
        showEmptyMessage();
    }

    private void insertTableHeader() {
        TableRow row = new TableRow(this);

        TextView savedText = new TextView(this);
        savedText.setText("Text");
        savedText.setTextColor(getResources().getColor(R.color.white));
        savedText.setTextSize(20);
        row.addView(savedText);

        TextView sizeText = new TextView(this);
        sizeText.setText("Size");
        sizeText.setTextColor(getResources().getColor(R.color.white));
        sizeText.setTextSize(20);
        row.addView(sizeText);

        tableLayout.addView(row);

        TextView textView = new TextView(this);
        textView.setText("   ");
        tableLayout.addView(textView);
    }

    private void addTableRow(TableLayout tableLayout, String text, String size) {
        TableRow row = new TableRow(this);

        TextView textColumn = new TextView(this);
        textColumn.setText(text);
        textColumn.setTextColor(getResources().getColor(R.color.white));
        row.addView(textColumn);

        TextView sizeColumn = new TextView(this);
        sizeColumn.setText(size);
        sizeColumn.setTextColor(getResources().getColor(R.color.white));
        row.addView(sizeColumn);

        tableLayout.addView(row);
    }
}
