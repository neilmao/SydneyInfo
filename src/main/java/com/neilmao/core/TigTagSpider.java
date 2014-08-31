package com.neilmao.core;


import com.neilmao.tool.CharsetEncoding;
import com.neilmao.tool.HTMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: neilmao
 * Date: 22/08/2014
 */
@Component
public class TigTagSpider extends AbstractSpider {

    private static Log LOG = LogFactory.getLog(TigTagSpider.class);

    private final CharsetEncoding encoding = CharsetEncoding.GBK;

    public TigTagSpider() {
        init();
    }

    @Override
    public void init() {
        super.init("/TigTagSettings.properties");
    }

    @Override
    public boolean login() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("fastloginfield", "username");
        params.put("username", username);
        params.put("password", password);
        params.put("quickforward", "yes");
        params.put("handlekey", "ls");

        String html;
        HttpResponse response;

        try {
            response =  postRequest(host + login, params);
            html = getHTMLFromResponse(response, encoding);
        } catch (IOException ex) {
            LOG.error("Logging failed:" + ex.toString());
            return false;
        }
        //LOG.info(html);
        String redirectLink =  HTMLUtils.extractBetween(html, "member.php?", true, "'", false);
        try {
            response = getRequest(host + redirectLink, null);
            html = getHTMLFromResponse(response, encoding);
        } catch (IOException ex) {
            LOG.error("Redirecting failed:" + ex.toString());
            return false;
        }
        //LOG.info(html);
        // extract idhash
        String idhash = HTMLUtils.extractBetween(html, "onclick=\"updateseccode('", false, "'", false);
        // load image html
        String imgReqLink = host + "misc.php?mod=seccode&action=update&idhash=" + idhash +
                "&inajax=1&ajaxtarget=seccode_" + idhash;
        try {
            response = getRequest(imgReqLink, "referer=" + host + redirectLink);
            html = getHTMLFromResponse(response, encoding);
        } catch (IOException e) {
            LOG.error("Failed to send request to get image link.");
        }

        // download verification image
        String imgLink = HTMLUtils.extractBetween(html, "misc.php?", true, "\"", false);

        if (imgLink == null) {
            LOG.error("Failed to find verification image link.");
            return false;
        }

        imgLink = host + imgLink;
        String referer = host + redirectLink;

        RequestConfig config = RequestConfig.custom().
                setConnectionRequestTimeout(default_download_timeout).
                setSocketTimeout(default_download_timeout).
                setConnectTimeout(default_download_timeout).
                build();

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeaders.REFERER, referer);

        try {
            downloadFile(imgLink, null, "img.png", config, headers);
            LOG.info("Verification image fetched.");
        } catch (IOException e) {
            LOG.error("Downloading verification image failed.");
            return false;
        }

        // type verification code
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String code = reader.readLine();

        return true;
    }

    @Override
    public void execute() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistData() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistImage() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected CharsetEncoding getEncoding() {
        return this.encoding;
    }
}
