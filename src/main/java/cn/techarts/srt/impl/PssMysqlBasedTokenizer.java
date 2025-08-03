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
import cn.techarts.srt.util.Persister;

public class PssMysqlBasedTokenizer extends AbstractSRTokenizer {
	private Persister persister;
	
	private static final String SET_STATE = "insert into srt_token_state (uid, salt, hash) values(?,?,?)";
	private static final String GET_STATE = "select uid, salt, hash from srt_token_state where salt=?";
	private static final String GET_STATES = "select uid, salt, hash from srt_token_state where uid=?";
	private static final String REVOCATE = "delete from srt_token_state where salt=?";
	private static final String REVOCATE_ALL = "delete from srt_token_state where uid=?";
		
	public PssMysqlBasedTokenizer(Persister persister) {
		this.persister = persister;
	}
	
	public byte getExtend(Session session, Configuration config) {
		return config.getExtend();
	}
	
	@Override
	public void setState(byte[] uid, long salt, String hash) {
		if(uid == null || salt == 0 || hash == null) return;
		persister.update(SET_STATE, new String(uid), salt, hash);
	}

	/**@param uid Please ignore it.
	 * @param salt The primary key.
	 * */
	@Override
	public MicroState getState(byte[] uid, long salt) {
		return persister.get(GET_STATE, MicroState.class, salt);
	}

	@Override
	public void revocate(byte[] uid) {
		persister.update(REVOCATE_ALL, new String(uid));
	}

	@Override
	public void revocate(byte[] uid, long salt) {
		persister.update(REVOCATE, salt);
	}

	@Override
	public void setState(MicroState state) {
		if(state == null || state.getSalt() == 0 || state.getHash() == null) return;
		persister.update(SET_STATE, state.getUid(), state.getSalt(), state.getHash());
	}

	@Override
	public List<MicroState> getStates(byte[] uid) {
		var param = new String(uid);
		var result = persister.getAll(GET_STATES, MicroState.class, param);
		return result != null ? result : List.of();
	}
}