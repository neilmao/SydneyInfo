package neilmao.core;

import neilmao.tool.CharsetEncoding;
import org.apache.commons.io.IOUtils;
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
import java.net.URLEncoder;
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

    protected final int DEFAULT_DOWNLOAD_TIMEOUT = 20 * 1000;
    protected final int DEFAULT_CONNECTION_TIMEOUT = 10 * 1000;
    protected final String SYSTEM_TEMP_FOLDER_KEY = "java.io.tmpdir";

    private HttpContext httpContext;
    private boolean active;

    protected String host;
    protected String login;
    protected String username;
    protected String password;

    protected Properties properties;

    public void start() {
        this.active = true;
    }

    public void stop() {
        this.active = false;
    }

    /**
     *  default settings for Spiders, override this method to change settings
     */
    protected void init(String configFile) {
        httpContext = new BasicHttpContext();
        CookieStore cookieStore = new BasicCookieStore();
        httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

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

    /**
     * Send get request with default settings
     */
    protected HttpResponse getRequest(String link, String paramStr) throws IOException {
        return getHttpClient(null).execute(getHttpGet(link, paramStr), httpContext);
    }

    /**
     * Send get request with customised settings
     */
    protected HttpResponse getRequest(String link, String paramStr, RequestConfig config, Map<String, String> headers) throws IOException {

        HttpGet get = getHttpGet(link, paramStr);

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                 get.setHeader(header.getKey(), header.getValue());
            }
        }

        return getHttpClient(config).execute(get, httpContext);
    }

   /**
    * Send post request with default settings
    */
    protected HttpResponse postRequest(String link, Map<String, String> params) throws IOException {
        return getHttpClient(null).execute(getHttpPost(link, params), httpContext);
    }

    /**
     * Send post request with customised settings
     */
    protected HttpResponse postRequest(String link, Map<String, String> params, RequestConfig config, Map<String, String> headers) throws IOException {

        HttpPost post = getHttpPost(link, params);

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                post.setHeader(header.getKey(), header.getValue());
            }
        }

        return getHttpClient(config).execute(post, httpContext);
    }

    private HttpPost getHttpPost(String link, Map<String, String> params) throws UnsupportedEncodingException {
        LOG.info("Sending Post request to " + link);
        HttpPost post = new HttpPost(link);
        if (params != null) {
            LOG.info("Params: " + params);
            List<NameValuePair> nvps = new LinkedList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(nvps));
        }
        return post;
    }

    private HttpGet getHttpGet(String link, String paramStr) throws UnsupportedEncodingException {
        LOG.info("Sending Get request to " + link);
        if (paramStr != null) {
            paramStr = URLEncoder.encode(paramStr, getEncoding().toString());
            LOG.info("Params: " + paramStr);
            link = link + "?" + paramStr;
        }
        return new HttpGet(link);
    }

    /**
     * Download a file from
     * @param link
     * and save to
     * @param fileName
     *
     * @param paramStr optional params
     */
    protected void downloadFile(String link, String paramStr, String fileName, RequestConfig config, Map<String, String> headers) throws IOException {
        LOG.info("Downloading file from " + link);

        FileOutputStream outputStream = new FileOutputStream(getTempFolder() + fileName);

        InputStream inputStreams = getRequest(link, paramStr, config, headers).getEntity().getContent();

        byte[] content = IOUtils.toByteArray(inputStreams);
        IOUtils.write(content, outputStream);
        LOG.info("File saved to " + getTempFolder() + fileName);
    }

    /**
     *  Get an instance of HttpClient
     */
    private HttpClient getHttpClient(RequestConfig config) {
        if (config == null) {
            config = RequestConfig.custom().
                    setConnectionRequestTimeout(DEFAULT_CONNECTION_TIMEOUT).
                    setConnectionRequestTimeout(DEFAULT_CONNECTION_TIMEOUT).
                    setSocketTimeout(DEFAULT_CONNECTION_TIMEOUT).build();
        }
        return HttpClients.custom().setDefaultRequestConfig(config).build();
    }

    /**
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

    protected abstract CharsetEncoding getEncoding();

    public HttpContext getHttpContext() {
        return httpContext;
    }

    public String getTempFolder() {
        return System.getProperty(SYSTEM_TEMP_FOLDER_KEY);
    }
}
