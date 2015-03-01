package com.styliii.gridimagesearch.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.styliii.gridimagesearch.R;

public class SettingsActivity extends ActionBarActivity {
    private Spinner spImageSize;
    private Spinner spColorFilter;
    private Spinner spImageType;
    private EditText etSiteFilter;
    private Button btnSave;
    private ArrayAdapter<CharSequence> imageSizeAdapter;
    private ArrayAdapter<CharSequence> imageTypeAdapter;
    private ArrayAdapter<CharSequence> colorFilterAdapter;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        pref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        setupViews();
        setupAdapters();
        setupPrefs();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imgsz = (String) imageSizeAdapter.getItem(spImageSize.getSelectedItemPosition());
                String imgcolor = (String) colorFilterAdapter.getItem(spColorFilter.getSelectedItemPosition());
                String imgtype = (String) imageTypeAdapter.getItem(spImageType.getSelectedItemPosition());

                SharedPreferences.Editor edit = pref.edit();
                edit.putString("imgsz", imgsz);
                edit.putString("imgcolor", imgcolor);
                edit.putString("imgtype", imgtype);
                edit.putString("site", etSiteFilter.getText().toString());
                edit.commit();

                setResult(RESULT_OK);
                // Close the activity, so you can go back
                finish();
            }
        });
    }

    private void setupViews() {
        spImageSize = (Spinner) findViewById(R.id.spImageSize);
        spColorFilter = (Spinner) findViewById(R.id.spColorFilter);
        spImageType = (Spinner) findViewById(R.id.spImageType);
        etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);
        btnSave = (Button) findViewById(R.id.btnSave);
    }

    private void setupPrefs() {
        String imgsz = pref.getString("imgsz", "");
        if (imgsz != "") {
            int i = imageSizeAdapter.getPosition(imgsz);
            spImageSize.setSelection(i);
        }

        String imgcolor = pref.getString("imgcolor", "");
        if (imgcolor != "") {
            int i = colorFilterAdapter.getPosition(imgcolor);
            spColorFilter.setSelection(i);
        }

        String imgtype = pref.getString("imgtype", "");
        if (imgtype != "") {
            int i = imageTypeAdapter.getPosition(imgtype);
            spImageType.setSelection(i);
        }

        String site = pref.getString("site", "");
        if (site != "") {
            etSiteFilter.setText(site);
        }
    }

    private void setupAdapters() {
        imageSizeAdapter = ArrayAdapter.createFromResource(this,
                R.array.image_size_array, android.R.layout.simple_spinner_item);
        spImageSize.setAdapter(imageSizeAdapter);

        imageTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.image_type_array, android.R.layout.simple_spinner_item);
        spImageType.setAdapter(imageTypeAdapter);

        colorFilterAdapter = ArrayAdapter.createFromResource(this,
                R.array.color_filter_array, android.R.layout.simple_spinner_item);
        spColorFilter.setAdapter(colorFilterAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
