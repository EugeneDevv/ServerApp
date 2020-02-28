package com.example.thunderbolt_108.serverapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Reports extends AppCompatActivity {
    private TextView textViewFilters;
    private RecyclerView recyclerViewFilters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        textViewFilters= findViewById(R.id.textViewFilters);
        recyclerViewFilters = findViewById(R.id.recyclerViewFilters);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
