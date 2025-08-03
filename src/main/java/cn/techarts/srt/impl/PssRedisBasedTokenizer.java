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

import java.util.ArrayList;
import java.util.List;

import cn.techarts.srt.AbstractSRTokenizer;
import cn.techarts.srt.core.Configuration;
import cn.techarts.srt.core.MicroState;
import cn.techarts.srt.core.Session;
import cn.techarts.srt.util.Cacher;

public class PssRedisBasedTokenizer extends AbstractSRTokenizer {
	
	public byte getExtend(Session session, Configuration config) {
		return config.getExtend();
	}
	
	@Override
	public void setState(byte[] uid, long salt, String hash) {
		if(uid == null || salt == 0 || hash == null) return;
		Cacher.setMapItem(1, new String(uid), (salt + ""), hash);
	}

	@Override
	public MicroState getState(byte[] uid, long salt) {
		var key = new String(uid);
		var hash = Cacher.getMapItem(1, key, (salt + ""));
		return new MicroState(key, salt, hash);
	}

	@Override
	public void revocate(byte[] uid) {
		Cacher.remove(1, new String(uid));
	}

	@Override
	public void revocate(byte[] uid, long salt) {
		Cacher.removeMapItem(1, new String(uid), salt + "");
	}

	@Override
	public void setState(MicroState state) {
		if(state == null || state.getSalt() == 0 || state.getHash() == null) return;
		Cacher.setMapItem(1, state.getUid(), state.getSalt() + "", state.getHash());
	}

	@Override
	public List<MicroState> getStates(byte[] uid) {
		var key = new String(uid);
		var states = Cacher.getMap(1, key);
		if(states == null) return List.of();
		if(states.isEmpty()) return List.of();
		var result = new ArrayList<MicroState>();
		for(var state : states.entrySet()) {
			var salt = Long.parseLong(state.getKey());
			result.add(new MicroState(key, salt, state.getValue()));
		}
		return result;
	}
}