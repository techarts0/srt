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

import java.time.Instant;
import java.util.Arrays;
import cn.techarts.srt.RevocationMode;
import cn.techarts.srt.util.ByteBuf;
import cn.techarts.srt.util.Converter;

public class SRToken extends SRTHeader{
	private int timestamp;
	private byte[] salt;
	private byte[] uid;
	private long ua;
	private byte[] ip;
	private byte[] data;
	
	private long randomSalt;
	private String contextHash;
	
	private static final int VERSION = 0;
	
	public SRToken() {}
	
	public SRToken(byte[] content, Configuration config) {
		this.decode(content, config);
	}
	
	public void decode(byte[] content, Configuration config) {
		this.parseHeader(content[0], content[1]);
		timestamp = Converter.toInt(content[2], content[3], 
		   						    content[4], content[5]);
		this.salt = new byte[8];
		this.uid = new byte[content[14]];
		System.arraycopy(content, 6, this.salt, 0, 8);//6~13
		System.arraycopy(content, 15, this.uid, 0, content[14]);
		var idx = 15 + content[14]; //End index of UID claim
		
		if(uaEnabled) {
			ua = Converter.toLong(content[idx], content[idx + 1], 
			 				      content[idx + 2], content[idx + 3],
								  content[idx + 4], content[idx + 5],
								  content[idx + 6], content[idx + 7]);
			idx += 8;
		}
		
		if(ipEnabled > 0) {
			var length = this.getIPLength();
			this.ip = new byte[length];
			System.arraycopy(content, idx, this.ip, 0, length);
			idx += length;
		}
		
		if(dataAssociated) {
			var length = content.length - idx;
			if(length == 0) return;
			this.data = new byte[length];
			System.arraycopy(content, idx, this.data, 0, content.length - idx);
		}
		if(RevocationMode.PSS.is(revocationMode)) {
			this.contextHash = MicroState.calcContextHash(content);
		}
	}
	
	//Binary Encoding without encryption
	public byte[] encode(Session session, Configuration config, byte extend, byte[] data) {
		var ipLength = session.getIPLength(config.getValidationMode());
		var ipEnabled = session.getIPEnabled(ipLength);
		var dataAssociated = (data != null && data.length > 0);
		
		var length = 15 + session.getUidLength() 
						+ session.getUALength(config.getValidationMode()) 
						+ ipLength + (dataAssociated ? data.length : 0);
		
		var result = new ByteBuf(length);
		
		var times = config.getRealExcetend(extend);
		
		var header = buildHeader(times, 
								 config.isUAEnabled(), 
								 (byte)ipEnabled, dataAssociated, 
								 (byte)config.getRevocationMode(), 
								 config.isCuc());
		
		result.append(header);
		result.appendInt(seconds2020());
		randomSalt = MicroState.generateSalt();
		result.appendLong(this.randomSalt);
		result.appendByte((byte)session.getUidLength());
		result.append(session.getUid());
		if(config.isUAEnabled()) {
			result.appendLong(session.getUa());
		}
		if(config.isIPEnabled()) {
			result.append(session.getIp());
		}
		if(dataAssociated) {
			result.append(data);
		}
		return result.toBytes();
	}

	public int getVersion() {
		return version;
	}
	
	public boolean isVersionSupported() {
		return this.version == VERSION;
	}
	
	public boolean checkUid(byte[] uid) {
		return Arrays.equals(this.uid, uid);
	}
	
	public boolean checkIP(byte[] ip) {
		if(ipEnabled == 0) return true;
		return Arrays.equals(this.ip, ip);
	}
	
	public boolean checkUA(long ua) {
		if(!uaEnabled) return true;
		return this.ua == ua;
	}
	
	public byte[] getUid() {
		return this.uid;
	}
	
	public int getExtend() {
		return extend;
	}

	public void setExtend(int extend) {
		this.extend = extend;
	}

	public boolean isUaEnabled() {
		return uaEnabled;
	}

	public void setUaEnabled(boolean uaEnabled) {
		this.uaEnabled = uaEnabled;
	}

	public boolean isDataAssociated() {
		return dataAssociated;
	}

	public void setDataAssociated(boolean dataAssociated) {
		this.dataAssociated = dataAssociated;
	}

	public boolean isCuc() {
		return cuc;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public byte[] getSalt() {
		return salt;
	}

	public long getLongSalt() {
		return Converter.toLong(salt);
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	public long getUa() {
		return ua;
	}

	public void setUa(long ua) {
		this.ua = ua;
	}
	
	public byte[] getAssociatedData() {
		return data;
	}

	public void setAssociatedData(byte[] associatedData) {
		this.data = associatedData;
	}	
	
	public long getRandomSalt() {
		return this.randomSalt;
	}
	
	public boolean expired(int duration) {
		var period = seconds2020() - timestamp;
		if(period <= duration) return false;
		if(this.extend == 0) return true;
		return (extend * duration < period);
	}
	
	public String getContextHash() {
		return this.contextHash;
	}
	
	/**
	 * Returns the seconds from 2020-01-01 0:0:0
	 */
	public static int seconds2020() {
		return (int)(Instant.now().getEpochSecond() - 1577808000L);
	}
}