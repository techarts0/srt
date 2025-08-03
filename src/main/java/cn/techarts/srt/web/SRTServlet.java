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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import cn.techarts.srt.RevocationMode;
import cn.techarts.srt.SRTokenizer;
import cn.techarts.srt.core.Session;
import cn.techarts.srt.impl.GwmMysqlBasedTokenizer;
import cn.techarts.srt.impl.GwmRedisBasedTokenizer;
import cn.techarts.srt.impl.PssMysqlBasedTokenizer;
import cn.techarts.srt.impl.PssRedisBasedTokenizer;
import cn.techarts.srt.impl.UcmSRTokenizer;
import cn.techarts.srt.util.Persister;

public class SRTServlet extends HttpServlet{
	public static final String URL_ISSUE = "/src/issue";
	public static final String URL_VERIFY = "/src/verify";
	public static final String KEY_PERSISTER = "srt.persister";
	public static final String KEY_CONFIG = "srt.configuration";
	
	protected Session getSession(HttpServletRequest request) {
		var ip = request.getParameter("ip");
		var ua = request.getParameter("ua");
		var uid = request.getParameter("uid");
		return new Session(uid, ip, ua);
	}
	
	protected SRTokenizer initPssTokenizer(ServletContext ctx) {
		var persister = ctx.getAttribute(KEY_PERSISTER);
		if(persister == null) {
			return new PssRedisBasedTokenizer();
		}else {
			return new PssMysqlBasedTokenizer((Persister)persister);
		}
	}
	
	protected SRTokenizer initGwmTokenizer(ServletContext ctx) {
		var persister = ctx.getAttribute(KEY_PERSISTER);
		if(persister == null) {
			return new GwmRedisBasedTokenizer();
		}else {
			return new GwmMysqlBasedTokenizer((Persister)persister);
		}
	}
	
	protected SRTokenizer initSRTokenizer(RevocationMode mode, ServletContext ctx) {
		if(RevocationMode.UCM.equals(mode)) { //UCM
			return new UcmSRTokenizer();
		}else if(RevocationMode.PSS.equals(mode)){ //PSS
			return initPssTokenizer(ctx);
		}else { //GWM
			return initGwmTokenizer(ctx);
		}
	}
}