package com.styliii.gridimagesearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liouyang on 2/27/15.
 */
public class ImageResult {
    public String fullUrl;
    public String title;
    public String thumbUrl;
    public String width;
    public String height;

    public ImageResult(JSONObject json) {
        try {
            this.fullUrl = json.getString("url");
            this.title = json.getString("title");
            this.thumbUrl = json.getString("tbUrl");
            this.width = json.getString("width");
            this.height = json.getString("height");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
        ArrayList<ImageResult> results = new ArrayList<ImageResult>();

        for (int i = 0; i < array.length(); i++) {
            try {
              results.add(new ImageResult(array.getJSONObject(i)));

            } catch (JSONException e) {
                e.printStackTrace();;
            }
        }

        return results;
    }
}
