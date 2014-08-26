package com.neilmao.tool;

/**
 * Created with IntelliJ IDEA.
 * User: neilmao
 * Date: 26/08/2014
 * Time: 11:16 PM
 */
public enum CharsetEncoding {

    UTF8,

    GB2312,

    GBK;

    @Override
    public String toString() {
        switch (this) {
            case UTF8: return "UTF-8";
            case GB2312: return "gb2312";
            case GBK: return "gbk";
            default: return "";
        }
    }
}
