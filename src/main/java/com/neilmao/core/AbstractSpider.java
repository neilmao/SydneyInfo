package com.neilmao.core;

import com.neilmao.tool.CharsetEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: neilmao
 * Date: 22/08/2014
 */
public abstract class AbstractSpider implements Crawler {

    private static Log LOG = LogFactory.getLog(AbstractSpider.class);

    private HttpContext httpContext;
    private boolean active;

    protected String host;
    protected String login;
    protected String username;
    protected String password;
    private int timeout;

    protected Properties properties;

    public void start() {
        this.active = true;
    }

    public void stop() {
        this.active = false;
    }

    /*
     *  default settings for Spiders, override this method to change settings
     */
    protected void init(String configFile) {
        httpContext = new BasicHttpContext();
        CookieStore cookieStore = new BasicCookieStore();
        httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

        // apply default settings
        this.timeout = 10 * 1000;

        // load settings from config file
        loadConfig(configFile);
        this.host = properties.getProperty("host");
        this.login = properties.getProperty("login");
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
    }

    private void loadConfig(String configFile) {
        InputStream inputStream = getClass().getResourceAsStream(configFile);
        this.properties = new Properties();
        if (inputStream != null) {
            try {
                this.properties.load(inputStream);
            } catch (IOException ex) {
                LOG.error("Loading config file \"" + configFile + "\" failed.");
            }
        } else {
            LOG.warn("Missing config file \"" + configFile + "\".");
        }
    }

    /*
     * Send get request
     */
    protected HttpResponse getRequest(String link, String paramStr) throws IOException {
        if (paramStr != null)
            link = link + "?" + paramStr;
        HttpGet get = new HttpGet(link);
        return getHttpClient().execute(get, httpContext);
    }

   /*
    * Send post request
    */
    protected HttpResponse postRequest(String link, Map<String, String> params) throws IOException {
        HttpPost post = new HttpPost(link);
        if (params != null) {
            List<NameValuePair> nvps = new LinkedList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(nvps));
        }
        return getHttpClient().execute(post, httpContext);
    }

    /*
     *  Get an instance of HttpClient
     */
    private HttpClient getHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(timeout).
                setConnectionRequestTimeout(timeout).
                setSocketTimeout(timeout).
                build();
        return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }

    /*
     *  Extract HTML as string from response
     */
    protected String getHTMLFromResponse(HttpResponse response, CharsetEncoding encoding) throws IOException {
        Reader reader = new InputStreamReader(response.getEntity().getContent(), encoding.toString());
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String buffer;
        while ((buffer = bufferedReader.readLine()) != null) {
            sb.append(buffer).append("\n");
        }
        return sb.toString();
    }

    public HttpContext getHttpContext() {
        return httpContext;
    }
}
