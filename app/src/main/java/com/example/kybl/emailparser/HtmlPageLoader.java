package com.example.kybl.emailparser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class HtmlPageLoader {

    private URL url;

    public HtmlPageLoader(URL url) {
        this.url = url;
    }

    public String getHTMLFromWebPage() {
        StringBuilder out = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);

            InputStream in = connection.getInputStream();

            Reader reader = new InputStreamReader(in);

            char[] buffer = new char[1024];
            int read;

            while ((read = reader.read(buffer)) != -1) {
                out.append(buffer, 0, read);
            }
            return out.toString();
        } catch (ProtocolException e1) {
            //e1.printStackTrace();
        } catch (IOException e1) {
            //e1.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return out.toString();
    }
}
