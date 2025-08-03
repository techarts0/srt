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

public class UcmSRTokenizer extends AbstractSRTokenizer {
		
	
	public byte getExtend(Session session, Configuration config) {
		return config.getExtend();
	}
	
	@Override
	public void setState(byte[] uid, long salt, String hash) {
		return;
	}

	/**@param uid Please ignore it.
	 * @param salt The primary key.
	 * */
	@Override
	public MicroState getState(byte[] uid, long salt) {
		return null;
	}

	@Override
	public void revocate(byte[] uid) {
		return;
	}

	@Override
	public void revocate(byte[] uid, long salt) {
		return;
	}

	@Override
	public void setState(MicroState state) {
		return;
	}

	@Override
	public List<MicroState> getStates(byte[] uid) {
		return List.of();
	}
}