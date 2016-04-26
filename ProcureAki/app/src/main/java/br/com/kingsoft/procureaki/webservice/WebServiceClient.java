package br.com.kingsoft.procureaki.webservice;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class WebServiceClient {

	public final String[] get(String url) {

		String[] result = new String[2];
		HttpGet httpget = new HttpGet(url);
		HttpResponse response;

		try {
			response = HttpClientSingleton.getHttpClientInstace().execute(httpget);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				result[0] = String.valueOf(response.getStatusLine().getStatusCode());
				InputStream instream = entity.getContent();
				result[1] = toString(instream);
				instream.close();
				Log.d("post", "URL: " + url);
				Log.i("get", "Result from post JsonPost : " + result[0] + " : "	+ result[1]);
			}
		} catch (Exception e) {
			Log.e("NGVL", "Falha ao acessar Web service", e);
			result[0] = "0";
			result[1] = e.toString();
		}
		
		return result;
	}

	public final String[] post(String url, String json) {
		String[] result = new String[2];
		try {

			HttpPost httpPost = new HttpPost(new URI(url));
			httpPost.setHeader("Content-type", "application/json");
			StringEntity sEntity = new StringEntity(json, "UTF-8");
			httpPost.setEntity(sEntity);

			HttpResponse response = HttpClientSingleton.getHttpClientInstace().execute(httpPost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				result[0] = String.valueOf(response.getStatusLine().getStatusCode());
				InputStream instream = entity.getContent();
				result[1] = toString(instream);
				instream.close();
				Log.d("post", "URL: " + url);
				Log.d("post", "Result from post JsonPost : " + result[0] + " : " + result[1]);
			}

		} catch (Exception e) {
			Log.e("NGVL", "Falha ao acessar Web service", e);
			result[0] = "0";
			result[1] = "Falha de rede!";
		}
		
		return result;
	}
	
	public String[] post(String url, JSONObject json) throws JSONException {
		String[] result = new String[2];
		
        HttpClient httpclient = new DefaultHttpClient();

        try { 
            HttpPost httppost = new HttpPost(url);

            List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);    
            nvp.add(new BasicNameValuePair("json", json.toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nvp, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost); 

            if (response != null) {
            	result[0] = String.valueOf(response.getStatusLine().getStatusCode());
                InputStream is = response.getEntity().getContent();
				result[1] = toString(is);
				is.close();
				Log.d("post", "URL: " + url);
				Log.d("post", "Result from post JsonPost : " + result[0] + " : " + result[1]);
            }

        } catch (Exception e) {
			Log.e("NGVL", "Falha ao acessar Web service", e);
			result[0] = "0";
			result[1] = "Falha de rede!";
		}
		
		return result;
	}
	
	

	private String toString(InputStream is) throws IOException {

		byte[] bytes = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int lidos;
		while ((lidos = is.read(bytes)) > 0) {
			baos.write(bytes, 0, lidos);
		}
		
		return new String(baos.toByteArray());
	}
}
