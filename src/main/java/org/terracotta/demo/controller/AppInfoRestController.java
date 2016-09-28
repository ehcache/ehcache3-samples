package org.terracotta.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Anthony Dahanne on 2016-09-28.
 */
@RestController
public class AppInfoRestController {


    @RequestMapping(value = "/info", produces = "application/json")
    public AppInfo appInfo() throws UnknownHostException {
        AppInfo appInfo  = new AppInfo(InetAddress.getLocalHost().getHostName());
        return appInfo;
    }



    class AppInfo {
        private String hostname;

        public AppInfo(String hostname) {
            this.hostname = hostname;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }
    }

}
