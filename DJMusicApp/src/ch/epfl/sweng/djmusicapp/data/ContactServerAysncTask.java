package ch.epfl.sweng.djmusicapp.data;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Contacts server asynchronously
 * 
 * @author csbenz
 * 
 */
public class ContactServerAysncTask extends AsyncTask<String, String, String> {

    private static final int CONNECTION_TIMEOUT = 15000;

    private HttpCallback mHttpCallback;

    public ContactServerAysncTask(HttpCallback httpCallback) {

        if (httpCallback == null) {
            throw new NullPointerException("null callback");
        }

        mHttpCallback = httpCallback;
    }

    /**
     * params[0] is url to query, params[1] is optional JSONObject in String
     * format
     */
    @Override
    protected String doInBackground(String... params) {
        // Get the parameters sent to AsyncTask
        String url = params[0];
        String jsonObjectInString = params[1];

        String result = null;

        try {
            StringEntity stringEntity = new StringEntity(jsonObjectInString);
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParameters,
                    CONNECTION_TIMEOUT);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(stringEntity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            result = EntityUtils.toString(entity);

            Log.i("server response", url + ":\n" + result);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    protected void onPostExecute(String result) {
        if (mHttpCallback != null) {

            if (result != null) {
                try {
                    JSONObject resultJsonObject = new JSONObject(result);
                    mHttpCallback.onHttpRequestReponse(resultJsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                    mHttpCallback
                            .onHttpRequestFail("Failed to parse JSON response");
                }

            } else {
                mHttpCallback.onHttpRequestFail("Error contacting server.");
            }
        }
    }

    /**
     * Called when the background task is finished, with the String response
     * (may be a jsoned object or a simple string)
     * 
     */
    public interface HttpCallback {
        public void onHttpRequestReponse(JSONObject jsonResponse);

        public void onHttpRequestFail(String errorMessage);
    }
}
