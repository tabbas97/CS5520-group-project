package edu.northeastern.g15finalproject;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetCoordinates extends AsyncTask<String, Void, List<Double>> {
    private List<Double> output = new ArrayList<>();

    @Override
    protected List<Double> doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream response = connection.getInputStream();
                java.util.Scanner s = new java.util.Scanner(response).useDelimiter("\\A");
                String responseString = s.hasNext() ? s.next() : "";
                response.close();
                responseString += "\0";

                JSONObject responseJSON = new JSONObject(responseString);

                JSONArray result = responseJSON.getJSONArray("results");

                double lat = result.getJSONObject(0).getJSONObject("geometry").getDouble("lat");
                double lng = result.getJSONObject(0).getJSONObject("geometry").getDouble("lng");
                this.output.add(lat);
                this.output.add(lng);
                return output;
            }
            return null;
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
