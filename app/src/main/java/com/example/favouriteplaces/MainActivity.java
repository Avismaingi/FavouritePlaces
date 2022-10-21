package com.example.favouriteplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.favouriteplaces.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayList<String> totalPlaces=new ArrayList<String>();
    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.favouriteplaces", Context.MODE_PRIVATE);

        ArrayList<String> latitudes=new ArrayList<>();
        ArrayList<String> longitudes=new ArrayList<>();

        totalPlaces.clear();
        latitudes.clear();
        longitudes.clear();
        locations.clear();

        try {
            latitudes= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longs",ObjectSerializer.serialize(new ArrayList<String>())));
            totalPlaces= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(totalPlaces.size()>0 && latitudes.size()>0 && longitudes.size()>0 && totalPlaces.size()==latitudes.size() && latitudes.size()==longitudes.size()){
            for (int i = 0; i < totalPlaces.size(); i++) {
                locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
            }
        }else{
            totalPlaces.add(0,"Add new places...");
            locations.add(0,new LatLng(0,0));
        }


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