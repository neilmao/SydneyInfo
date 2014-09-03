package com.neilmao.web.spider;

import com.neilmao.core.TigTagSpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: neilmao
 * Date: 3/09/2014
 * Time: 9:20 PM
 */
@Controller
@RequestMapping("/spider/tigtag")
public class TigTagSpiderController {

    @Autowired
    private TigTagSpider tigTagSpider;

    @RequestMapping(method = RequestMethod.GET)
    public String typeCode() {
        return "code";
    }

}
