package com.neilmao.core;


import com.neilmao.tool.CharsetEncoding;
import com.neilmao.tool.HTMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
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
    public boolean login() {
        Map<String, String> params = new HashMap<>();
        params.put("fastloginfield", "username");
        params.put("username", username);
        params.put("password", password);
        params.put("quickforward", "yes");
        params.put("handlekey", "ls");

        String html;

        try {
            HttpResponse httpResponse =  postRequest(host + login, params);
            html = getHTMLFromResponse(httpResponse, encoding);
        } catch (IOException ex) {
            LOG.error("Logging failed:" + ex.toString());
            return false;
        }
        LOG.info(html);
        String redirectLink =  HTMLUtils.extractBetween(html, "member.php?", true, "'", false);
        try {
            HttpResponse httpResponse = getRequest(host + redirectLink, null);
            html = getHTMLFromResponse(httpResponse, encoding);
        } catch (IOException ex) {
            LOG.error("Redirecting failed:" + ex.toString());
            return false;
        }
        LOG.info(html);

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
}
