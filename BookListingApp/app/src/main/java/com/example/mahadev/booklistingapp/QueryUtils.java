package com.example.mahadev.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahadev on 24/02/18.
 */

public class QueryUtils {

    private static String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static List<BookDetails> extractBooks(String jsonUrl) {

        if (TextUtils.isEmpty(jsonUrl)) {
            return null;
        }

        List<BookDetails> books = new ArrayList<>();

        try {
            JSONObject jsonObj = new JSONObject(jsonUrl);
            if(jsonObj.getInt("totalItems") == 0) {
                return books;
            }
            JSONArray jsonArr = jsonObj.getJSONArray("items");

            for(int i=0; i<jsonArr.length(); i++) {
                JSONObject bookObject = jsonArr.getJSONObject(i);

                JSONObject bookInfo = bookObject.getJSONObject("volumeInfo");

                String title = bookInfo.getString("title");
                JSONArray authorsList = bookInfo.getJSONArray("authors");
                String authors = formattedListofAuthors(authorsList);

                String preview = bookInfo.getString("previewLink");

                BookDetails bookDetails = new BookDetails(title, authors, preview);
                books.add(bookDetails);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "failed to extract", e);
        }

        return books;
    }

    private static String formattedListofAuthors(JSONArray authorsList) throws JSONException {

        String ListofAuthors = null;
        if(authorsList.length() == 0) {
            return null;
        }

        for(int i=0; i<authorsList.length(); i++) {
            if(i == 0) {
                ListofAuthors = authorsList.getString(0);
            }
            else {
                ListofAuthors += ", " + authorsList.getString(i);
            }
        }

        return ListofAuthors;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlHttpConnection = null;
        InputStream inputStream = null;
        try {
            urlHttpConnection = (HttpURLConnection) url.openConnection();
            urlHttpConnection.setRequestMethod("GET");
            urlHttpConnection.setReadTimeout(10000);
            urlHttpConnection.setConnectTimeout(15000);
            urlHttpConnection.connect();

            if (urlHttpConnection.getResponseCode() == 200) {
                inputStream = urlHttpConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code" + urlHttpConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with creating url", e);
        } finally {
            if (urlHttpConnection != null) {
                urlHttpConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<BookDetails> fetchBookDetials(String requestUrl){
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the http request", e);
        }

        return extractBooks(jsonResponse);
    }
}
