package com.neilmao.core;


import com.neilmao.tool.CharsetEncoding;
import com.neilmao.tool.HTMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: neilmao
 * Date: 22/08/2014
 */
public class TigTagSpider extends AbstractSpider {

    private static Log LOG = LogFactory.getLog(TigTagSpider.class);

    private final CharsetEncoding ENCODING = CharsetEncoding.GBK;

    private final String VERIFICATION_IMAGE_FILENAME = "verificationImage.png";

    private String idhash;
    private String referer;

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
            html = getHTMLFromResponse(response, ENCODING);
        } catch (IOException ex) {
            LOG.error("Logging failed:" + ex.toString());
            return false;
        }
        //LOG.info(html);
        String redirectLink =  HTMLUtils.extractBetween(html, "member.php?", true, "'", false);
        try {
            response = getRequest(host + redirectLink, null);
            html = getHTMLFromResponse(response, ENCODING);
        } catch (IOException ex) {
            LOG.error("Redirecting failed:" + ex.toString());
            return false;
        }
        //LOG.info(html);
        // extract idhash
        idhash = HTMLUtils.extractBetween(html, "onclick=\"updateseccode('", false, "'", false);
        // load image html
        String imgReqLink = host + "misc.php?mod=seccode&action=update&idhash=" + idhash +
                "&inajax=1&ajaxtarget=seccode_" + idhash;
        try {
            response = getRequest(imgReqLink, "referer=" + host + redirectLink);
            html = getHTMLFromResponse(response, ENCODING);
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
        referer = host + redirectLink;

        RequestConfig config = RequestConfig.custom().
                setConnectionRequestTimeout(DEFAULT_DOWNLOAD_TIMEOUT).
                setSocketTimeout(DEFAULT_DOWNLOAD_TIMEOUT).
                setConnectTimeout(DEFAULT_DOWNLOAD_TIMEOUT).
                build();

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeaders.REFERER, referer);

        try {
            downloadFile(imgLink, null, VERIFICATION_IMAGE_FILENAME, config, headers);
            LOG.info("Verification image fetched.");
        } catch (IOException e) {
            LOG.error("Downloading verification image failed.");
            return false;
        }

        return true;
    }

    public boolean typeVerificationCode(String code) throws IOException {
        // send xhr to check code
        String checkURL = host + "misc.php?mod=seccode&action=check&inajax=1&&idhash=" + idhash + "&secverify=" + code;

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeaders.REFERER, referer);

        HttpResponse response = getRequest(checkURL, null, null, headers);

        String html = getHTMLFromResponse(response, ENCODING);
        if (html.contains("[succeed]"))
            return true;
        else
            return false;
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
        return this.ENCODING;
    }
}
