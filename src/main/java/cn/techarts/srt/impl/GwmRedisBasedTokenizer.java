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

package cn.techarts.srt.impl;

import java.util.List;

import cn.techarts.srt.AbstractSRTokenizer;
import cn.techarts.srt.core.Configuration;
import cn.techarts.srt.core.MicroState;
import cn.techarts.srt.core.Session;
import cn.techarts.srt.util.Cacher;

public class GwmRedisBasedTokenizer extends AbstractSRTokenizer {
	
	public byte getExtend(Session session, Configuration config) {
		return config.getExtend();
	}
	
	@Override
	public void setState(byte[] uid, long salt, String hash) {
		if(salt == 0) return;
		Cacher.saveString(1, salt + "", "0", 0);
	}

	@Override
	public MicroState getState(byte[] uid, long salt) {
		var key = Cacher.getString(1, salt + "");
		if(key == null || key.isEmpty()) return null;
		return new MicroState(Long.parseLong(key));
	}

	@Override
	public void revocate(byte[] uid) {
		return;
	}

	@Override
	public void revocate(byte[] uid, long salt) {
		Cacher.removeString(1, salt + "");
	}

	@Override
	public void setState(MicroState state) {
		if(state == null || state.getSalt() == 0) return;
		Cacher.saveString(1, (state.getSalt() + ""), "0", 0);
	}

	@Override
	public List<MicroState> getStates(byte[] uid) {
		return List.of();
	}
}