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

package cn.techarts.srt.core;

import java.security.SecureRandom;
import cn.techarts.srt.util.Cryptor;
import org.bouncycastle.jcajce.provider.digest.Blake3.Blake3_256;

public class MicroState {
	private  String uid;
	private long salt;
	private String hash;
	
	public MicroState() {}
	
	public MicroState(long salt) {
		this.salt = salt;
	}
	
	public MicroState(String uid, long salt, String hash) {
		this.uid = uid;
		this.salt = salt;
		this.hash = hash;
	}
	
	public MicroState(byte[] uid, long salt, String hash) {
		this.salt = salt;
		this.hash = hash;
		this.uid = new String(uid);
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public long getSalt() {
		return salt;
	}
	public void setSalt(long salt) {
		this.salt = salt;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public void setHash(byte[] payload) {
		this.hash = calcContextHash(payload);
	}
	
	/**HASH ALGORITHM: BLAKE3-256<br>
	 * Computes the hash of SRT binary payload.
	 */
	public static String calcContextHash(byte[] content) {
		var hasher = new Blake3_256();
		hasher.update(content);
		return Cryptor.toHex(hasher.digest(), false);
	}
	
	/**
	 * Generate a secure random salt
	 * @return A 64-bits random number
	 * */
	public static long generateSalt() {
		return new SecureRandom().nextLong(1, Long.MAX_VALUE);
	}
	
	/**
	 * Check the consistency of Context-Hash
	 */
	public boolean checkHash(byte[] payload) {
		var target = calcContextHash(payload);
		return this.getHash() == target;
	}
	
	/**
	 * Check the consistency of Context-Hash
	 */
	public boolean checkHash(String hash) {
		return this.getHash() == hash;
	}
	
	/**
	 * Check the consistency of Random Salt
	 */
	public boolean checkSalt(long salt) {
		return this.getSalt() == salt;
	}
}