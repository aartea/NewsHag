package badassapps.aaron.newshag;

import android.content.ContentValues;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by aaron on 5/20/2016.
 */
public class NavDGrab {
    private static NavDGrab instance;
    private static ApiResponseHandler responseHandler;

    private static final String BASE_URL = "http://api.nytimes" +
            ".com/svc/news/v3/content/copper/all/all" +
            ".json?limit=10&api-key=d1934738c85789ae6e8dac61ddca1abc%3A12%3A74602111";

    //Empty constructor
    private NavDGrab(){
    }


    //Creates our singleton
    public static NavDGrab getInstance(ApiResponseHandler handler){
        responseHandler = handler;
        if(instance == null){
            instance = new NavDGrab();
        }
        return instance;
    }

    public void doRequest(String parameter){
        AsyncHttpClient client = new AsyncHttpClient();

        //Ensure somewhere our wifi is on/off

        client.get(
                BASE_URL,null,
                new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        {
                            String id, secret, server, farm, address = null;
                            //Need to reconstruct and grab JSON object for image! Return image when done.
                            try {
                                String data = "";
                                Gson gson = new Gson();
                                Article result = gson.fromJson(data, Article.class);

                                //Loop through the results and insert the contents of each NewsItem into the database via our content provider.
                                for (int i = 0; i < result.getResults().size(); i++) {
                                    ContentValues values = new ContentValues();

                                    values.put("title", result.getResults().get(i).getTITLE());
                                    values.put("url", result.getResults().get(i).getURL());
                                    values.put("thumbnail_standard", result.getResults().get(i).getIMAGE());
                                    values.put("abstract", result.getResults().get(i).getABSTRACT());
                                    responseHandler.handleResponse(address);

                                        }
                                    }
                                catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                });
    }

    public interface ApiResponseHandler{
        void handleResponse(String response);
    }
}
