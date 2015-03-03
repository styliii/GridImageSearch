package com.styliii.gridimagesearch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.styliii.gridimagesearch.EditSettingsDialog;
import com.styliii.gridimagesearch.EndlessScrollListener;
import com.styliii.gridimagesearch.R;
import com.styliii.gridimagesearch.adapters.ImageResultsAdapter;
import com.styliii.gridimagesearch.models.ImageResult;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.support.v7.widget.SearchView.OnQueryTextListener;


public class SearchActivity extends ActionBarActivity {
    private SearchView svQuery;
    private StaggeredGridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    public static final int FORM_REQUEST_CODE = 23;
    private String settingsQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        setupSettings();
        imageResults = new ArrayList<ImageResult>();
        aImageResults = new ImageResultsAdapter(this, imageResults);
        gvResults.setAdapter(aImageResults);
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromAPI(totalItemsCount);
            }
        });
    }

    private void setupViews() {
        gvResults = (StaggeredGridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                ImageResult result = imageResults.get(position);
                intent.putExtra("result", result);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        svQuery = (SearchView) MenuItemCompat.getActionView(searchItem);
        svQuery.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                imageSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void imageSearch(String query) {
        AsyncHttpClient client = new AsyncHttpClient();
        setupSettings();
        String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?q=" + query + "&v=1.0&rsz=8" + settingsQuery;
        client.get(searchUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageResultsJson  = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    aImageResults.clear();
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (isNetworkAvailable()) {
                    Toast.makeText(SearchActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SearchActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                }
             }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FORM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setupSettings();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            FragmentManager manager = getSupportFragmentManager();
            EditSettingsDialog editSettingsDialog = EditSettingsDialog.newInstance();
            editSettingsDialog.show(manager, "settingsDialog");
//            onSettings();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void customLoadMoreDataFromAPI(int offset) {
        String query = svQuery.getQuery().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        setupSettings();
        String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?q=" + query
                + "&v=1.0&rsz=8" + settingsQuery + "&start=" + offset;
        client.get(searchUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageResultsJson = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
//                    Log.d("Debug", response.toString());
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (isNetworkAvailable()) {
                    Toast.makeText(SearchActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SearchActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setupSettings() {
        SharedPreferences pref =
                        PreferenceManager.getDefaultSharedPreferences(this);
        String imgsz = pref.getString("imgsz", "");
        String imgcolor = pref.getString("imgcolor", "");
        String imgtype = pref.getString("imgtype", "");
        String site = pref.getString("site", "");
        settingsQuery = "&imgsz=" + imgsz + "&imgcolor=" + imgcolor + "&imgtype=" + imgtype + "&as_sitesearch=" + site;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
