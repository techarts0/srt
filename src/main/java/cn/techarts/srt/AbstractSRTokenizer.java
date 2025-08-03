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

package cn.techarts.srt;

import java.util.Base64;

import cn.techarts.srt.core.Configuration;
import cn.techarts.srt.core.MicroState;
import cn.techarts.srt.core.SRToken;
import cn.techarts.srt.core.Session;

public abstract class AbstractSRTokenizer implements SRTokenizer {
	@Override
	public String issue(Session session, Configuration config, byte[] data) {
		var token = new SRToken();
		var b64encoder = Base64.getUrlEncoder();
		var extend = getExtend(session, config);
		var content = token.encode(session, config, extend, data);
		var hash = MicroState.calcContextHash(content);
		setState(session.getUid(), token.getRandomSalt(), hash);
		return b64encoder.encodeToString(config.encrypt(content));
	}
	
	private SRToken decode(String token, Configuration config) {
		var decoder = Base64.getUrlDecoder();
		var content = decoder.decode(token);
		content = config.decrypt(content);
		return new SRToken(content, config);
	}

	@Override
	public Result verify(String token, Session session, Configuration config) {
		var srtoken = decode(token, config);
		// Step 1
		if(!srtoken.isVersionSupported()) return Result.ERR_VER;
		// Step 2
		if(srtoken.expired(config.getDuration())) return Result.EXPIRED;
		// Step 3
		if(config.isCuc() && !srtoken.checkUid(session.getUid())) {
			return Result.ERR_UID;
		}
		// Step 4
		if(!srtoken.checkIP(session.getIp())) return Result.ERR_IP;
		// Step 5
		if(!srtoken.checkUA(session.getUa())) return Result.ERR_UA;
		// End of UCM		
		if(config.isUserCollaborationMode()) return Result.OK;
		
		// The following is Micro-State validation
		var state = getState(srtoken.getUid(), srtoken.getLongSalt());
		if(state == null || state.getSalt() == 0) return Result.ERR_STATE;
		// Step 6
		if(config.isPerSessionStateMode()) {
			if(!state.checkHash(srtoken.getContextHash())) return Result.ERR_HASH;
		}
		// Step 7
		return state.checkSalt(srtoken.getLongSalt()) ?  Result.OK : Result.ERR_SALT;
	}
	
	@Override
	public void setState(byte[] uid, long salt, String hash) {
		this.setState(new MicroState(uid, salt, hash));
	}
}