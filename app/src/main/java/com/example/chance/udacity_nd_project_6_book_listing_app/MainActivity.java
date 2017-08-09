package com.example.chance.udacity_nd_project_6_book_listing_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 10000;
    private ArrayList<Book> booksList;
    private ListAdapter adapter;
    private ListView lv;
    private String searchQuery;
    private EditText editText;
    private TextView errorMessage;
    private View progrssbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        booksList = new ArrayList<>();
        editText = (EditText) findViewById(R.id.search_books);
        errorMessage = (TextView) findViewById(R.id.error_message);
        progrssbar = findViewById(R.id.progress_bar);
        adapter = new ListAdapter(this, booksList);

        lv = (ListView) findViewById(R.id.books_list);

        Button btn = (Button) findViewById(R.id.find_btn);
        progrssbar.setVisibility(View.GONE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userQuery = editText.getText().toString();
                if (userQuery.equals("")) {
                    errorMessage.setText(R.string.no_data);
                    return;
                }
                progrssbar.setVisibility(View.VISIBLE);
                errorMessage.setVisibility(View.GONE);
                searchQuery = "https://www.googleapis.com/books/v1/volumes?q=" + userQuery + "&maxResults=30";
                Log.e(TAG, searchQuery);
                if (isConnected()) {
                    BookAsyncTask b = new BookAsyncTask();
                    b.execute(searchQuery);
                }
            }
        });
    }

    private boolean isConnected() {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            isConnected = true;
            return isConnected;
        } else {
            progrssbar.setVisibility(View.GONE);
            booksList.clear();
            errorMessage.setText(R.string.no_internet_connection);
            errorMessage.setVisibility(View.VISIBLE);
            return isConnected;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private class BookAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            InputStream inputStream;
            HttpURLConnection con;
            URL url = createUrl(params[0]);

            try {
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setReadTimeout(READ_TIMEOUT);
                con.setConnectTimeout(CONNECTION_TIMEOUT);
                con.connect();
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = new BufferedInputStream(con.getInputStream());
                    String response = parseJsonToString(inputStream);
                    extractFeatures(response);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error doing in background: " + e);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            booksList.clear();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            lv.setAdapter(adapter);
            progrssbar.setVisibility(View.GONE);
        }


        private String parseJsonToString(InputStream inputStream) throws IOException {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            if (inputStream != null)
                inputStream.close();

            return sb.toString();
        }

        public URL createUrl(String urls) {
            URL url = null;
            try {
                url = new URL(urls);
                return url;
            } catch (MalformedURLException e) {
                Log.e(TAG, "Error creating URL " + e);
            }
            return url;
        }

        public void extractFeatures(String result) {
            try {
                JSONObject baseJsonObject = new JSONObject(result);
                JSONArray features = baseJsonObject.optJSONArray("items");
                if (features.length() == 0) {
                    Log.e(TAG, "No Json response");
                } else {
                    StringBuilder authors = new StringBuilder();
                    for (int i = 0; i < features.length(); i++) {
                        JSONObject properties = features.optJSONObject(i);
                        JSONObject book = properties.getJSONObject("volumeInfo");
                        String bookTitle = book.getString("title");
                        if (book.has("authors")) { // books have either author or publisher
                            JSONArray authorsArray = book.getJSONArray("authors");
                            for (int j = 0; j < authorsArray.length(); j++) {
                                authors.append(authorsArray.getString(j) + ", ");
                            }
                        } else if (book.has("publisher")) {
                            authors.append(book.getString("publisher"));
                        }
                        // remove any non-alphabet character and any extra whitespaces.
                        String strippedAuthors = authors.toString().replaceAll("[^a-zA-Z]", " ").trim().replaceAll(" +", " ");
                        booksList.add(new Book(bookTitle, strippedAuthors));
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error receiving JSON: " + e);
            }
        }

    }
}
