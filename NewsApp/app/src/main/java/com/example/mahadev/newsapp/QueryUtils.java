package com.example.mahadev.newsapp;

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
 * Created by mahadev on 05/03/18.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static List<NewsDetails> extractNews(String jsonUrl) {

        if (TextUtils.isEmpty(jsonUrl)) {
            return null;
        }

        List<NewsDetails> newsDetails = new ArrayList<>();

        try {
            JSONObject jsonObj = new JSONObject(jsonUrl);
            JSONObject jsonResults = jsonObj.getJSONObject("response");
            JSONArray resultsArray = jsonResults.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject oneResult = resultsArray.getJSONObject(i);
                String webTitle = oneResult.getString("webTitle");
                String url = oneResult.getString("webUrl");
                String date = oneResult.getString("webPublicationDate");
                String sectionName = oneResult.getString("sectionName");

                JSONArray tagsArray = oneResult.getJSONArray("tags");

                String author = null;

                for (int j = 0; j < tagsArray.length(); j++) {

                    String authorFirst = "", authorLast = "";

                    JSONObject results = tagsArray.getJSONObject(j);

                    if (results.has("firstName")) {
                        authorFirst = results.getString("firstName");
                    }
                    if (results.has("lastName")) {
                        authorLast = results.getString("lastName");
                    }

                    author = authorFirst + " " + authorLast;

                    if (tagsArray.length() > 1) {
                        author += ", " + author;
                    }
                }

                newsDetails.add(new NewsDetails(webTitle, date, url, sectionName, author));
            }
        } catch (JSONException e) {
            Log.e("Queryutils", "Error parsing JSON response", e);
        }
        return newsDetails;
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
            urlHttpConnection.setReadTimeout(20000);
            urlHttpConnection.setConnectTimeout(30000);
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

    public static List<NewsDetails> fetchNewsDetails(String requestUrl){
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the http request", e);
        }

        return extractNews(jsonResponse);
    }
}
