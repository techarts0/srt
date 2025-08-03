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

import cn.techarts.srt.IPAddr;
import cn.techarts.srt.RevocationMode;

public class SRTHeader {
	protected int version;
	protected int extend;
	protected int ipEnabled;
	protected boolean uaEnabled;
	protected boolean dataAssociated;
	protected int revocationMode;
	protected boolean cuc;
	
	protected void parseHeader(byte b0, byte b1) {
		this.version= b0 >>> 5;
		this.extend = (b0 >> 1) & 15;
		this.uaEnabled = (b0 & 1) > 0;
		this.ipEnabled = (b1 & 192) >> 6;
		this.dataAssociated = (b1 & 32) > 0;
		this.revocationMode = (b1 >> 3) & 3;
		this.cuc = ((b1 >> 2) & 1) > 0;
	}
	
	public RevocationMode getRevocationMode() {
		return RevocationMode.to(this.revocationMode);
	}
	
	public IPAddr getIpEnabled() {
		return IPAddr.to(this.ipEnabled);
	}
	
	protected byte[] buildHeader(byte extend, 
					 	boolean uaEnabled, 
					 	byte ipEnabled, 
					 	boolean dataAssociated, 
					 	byte revocationMode, 
					 	boolean cuc) {
		
		var result = new byte[2];
		
		result[0] |=  extend << 1;
		
		if(uaEnabled) result[0] |= 1;
		
		result[1] |= ipEnabled << 6;
		
		if(dataAssociated) result[1] |= 64;
		
		result[1] |= revocationMode << 3;
		
		if(cuc) result[1] |= 4;
		
		return result;
	}

	protected int getIPLength() {
		if(ipEnabled > 2) return 0;
		if(ipEnabled == 0) return 0;
		return ipEnabled == 1 ? 4 : 16;
	}
}