/*
 * Copyright (C) 2024 techarts.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.techarts.srt.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import cn.techarts.srt.core.Configuration;
import cn.techarts.srt.util.Cacher;
import cn.techarts.srt.util.Persister;

/**
 * Application Lifecycle Listener implementation class InitListener
 *
 */
@WebListener
public class SRTInitListener implements ServletContextListener {
    /**
     * Default constructor. 
     */
    public SRTInitListener() {
       
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	var path = getResourcePath("config.properties");
    	var config = resolveProperties(path);
    	var storage = config.get("src.storage");
    	
    	if("rdbms".equalsIgnoreCase(storage)) {
	    	var url = config.get("jdbc.url");
	    	var driver = config.get("jdbc.driver");
	    	var user = config.get("jdbc.user");
	    	var password = config.get("jdbc.password");
	    	var persister = new Persister(driver, url, user, password);
	    	sce.getServletContext().setAttribute("srt.persister", persister);
    	}else if("redis".equalsIgnoreCase("redis")){
    		var host = config.get("redis.host");
    		var port = config.get("redis.port");
    		Cacher.init(host, port, config.get("redis.connections"));
    	}else {
    		throw new RuntimeException("Unsupported storage: " + storage);
    	}
    	
    	var key = config.get("srt.key");
    	var cuc = config.get("srt.cuc");
    	var expand = config.get("srt.expand");
    	var duration = config.get("srt.duration");
    	var revocationMode = config.get("srt.revocationMode");
    	var validationMode = config.get("srt.validationMode");
    	var settings = new Configuration(key, cuc, duration, expand, revocationMode, validationMode);
    	sce.getServletContext().setAttribute("srt.configuration", settings);
    }
    /**
	 * Properties configuration
	 */
	private static Map<String, String> resolveProperties(String file) {
		var config = new Properties();
		var result = new HashMap<String, String>(64);
		try(var in = new FileInputStream(file)) {
			config.load(in);
			for(var key : config.stringPropertyNames()) {
				result.put(key, config.getProperty(key));
			}
			return result;
		}catch(IOException e) {
			throw new RuntimeException("Failed to load config [" + file + "]", e);
		}
	}	
    
    private String getResourcePath(String resource) {
		var result = getClass().getResource("/".concat(resource));
		if(result != null && result.getPath() != null) return result.getPath();
		result = getClass().getResource("/WEB-INF/".concat(resource));
		if(result != null && result.getPath() != null) return result.getPath();
		throw new RuntimeException("Failed to find the resource: [" + resource + "]");
	}
}