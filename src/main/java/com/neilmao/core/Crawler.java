package com.neilmao.core;

/**
 * Created with IntelliJ IDEA.
 * User: neilmao
 * Date: 22/08/2014
 */
public interface Crawler {

    public void init();

    public void start();

    public void stop();

    public void login();

    public void execute();

    public void persistData();

    public void persistImage();
}
