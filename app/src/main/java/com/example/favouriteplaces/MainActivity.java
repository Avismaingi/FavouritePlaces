package com.example.favouriteplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.favouriteplaces.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayList<String> totalPlaces;
    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        totalPlaces = new ArrayList<>(Arrays.asList("Add new places..."));
        locations.add(new LatLng(0,0));

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, totalPlaces);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("placeId", i);
                startActivity(intent);
            }
        });
    }
}