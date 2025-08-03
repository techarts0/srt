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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import com.dynatrace.hash4j.hashing.Hashing;

import cn.techarts.srt.ValidationMode;

public class Session {
	private long ua;
	private byte[] ip;
	private byte[] uid;
	private String suid;
	
	public Session(String uid, String ip, String ua) {
		this.setUa(ua);
		this.setIp(ip);
		this.setUid(uid);
		this.suid = uid;
	}
	
	public byte[] getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ipToBytes(ip);
	}
	public long getUa() {
		return ua;
	}
	public void setUa(String ua) {
		var hash = Hashing.xxh3_64();
		this.ua = hash.hashStream().putString(ua).getAsLong();
	}
	public byte[] getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid.getBytes(StandardCharsets.US_ASCII);
	}
	
	public int getUidLength() {
		return uid != null ? uid.length : 0;
	}
	
	public int getIPLength(ValidationMode mode) {
		if(!ValidationMode.STRICT.equals(mode)) return 0;
		return this.ip != null ? this.ip.length : 0;
	}
	
	public int getIPEnabled(int ipLength) {
		return ipLength == 16 ? 2 : ipLength == 0 ? 0 : 1;
	}
	
	public int getUALength(ValidationMode mode) {
		return ValidationMode.LOOSE.equals(mode) ? 0 : 8;
	}
	
	public String getUidString() {
		return suid;
	}
	
	public static byte[] ipToBytes(String ipString) {
        try {
            var inetAddress = InetAddress.getByName(ipString);
            return inetAddress.getAddress();
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address: " + ipString, e);
        }
    }
	
	public void getUAFingerPrint(String ua) {
		var hash = Hashing.xxh3_64();
		hash.hashStream().putString(ua).getAsLong();
	}
}