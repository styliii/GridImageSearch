package com.styliii.gridimagesearch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class EditSettingsDialog extends DialogFragment {
    private Spinner spImageSize;
    private Spinner spColorFilter;
    private Spinner spImageType;
    private EditText etSiteFilter;
    private Button btnSave;
    private ArrayAdapter<CharSequence> imageSizeAdapter;
    private ArrayAdapter<CharSequence> imageTypeAdapter;
    private ArrayAdapter<CharSequence> colorFilterAdapter;
    private SharedPreferences pref;

    public static EditSettingsDialog newInstance() {
        EditSettingsDialog frag = new EditSettingsDialog();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_edit_settings, null);
        pref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        setupViews(view);
        setupAdapters(view);
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

                dismiss();
            }
        });
        return view;
    }

    private void setupViews(View view) {
        spImageSize = (Spinner) view.findViewById(R.id.spImageSize);
        spColorFilter = (Spinner) view.findViewById(R.id.spColorFilter);
        spImageType = (Spinner) view.findViewById(R.id.spImageType);
        etSiteFilter = (EditText) view.findViewById(R.id.etSiteFilter);
        btnSave = (Button) view.findViewById(R.id.btnSave);
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

    private void setupAdapters(View view) {
        imageSizeAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.image_size_array, R.layout.spinner_item);
        spImageSize.setAdapter(imageSizeAdapter);

        imageTypeAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.image_type_array, R.layout.spinner_item);
        spImageType.setAdapter(imageTypeAdapter);

        colorFilterAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.color_filter_array, R.layout.spinner_item);
        spColorFilter.setAdapter(colorFilterAdapter);
    }
}
