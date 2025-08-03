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

import cn.techarts.srt.RevocationMode;
import cn.techarts.srt.ValidationMode;
import cn.techarts.srt.util.Cryptor;

public class Configuration {
	private boolean cuc;
	private byte extend;
	private int duration;
	private byte[] secretKey;
	private int revocationMode;
	private int validationMode;
	
	public static final String TEST_KEY = "83ee04d15080db21cc46ed5849c38c7d";
	
	public Configuration() {}
	
	public Configuration(String key, String duration, String extend, String cuc, String revocationMode, String validationMode) {
		this.cuc = "0".equals(cuc);
		this.extend = Byte.parseByte(extend);
		this.secretKey = Cryptor.toBytes(key);
		this.duration = Integer.parseInt(duration);
		this.revocationMode = Integer.parseInt(revocationMode);
		this.validationMode = Integer.parseInt(validationMode);
	}
	
	public Configuration(String key, int duration, int extend,  boolean cuc, int revocationMode, int validationMode) {
		this.cuc = cuc;
		this.duration = duration;
		this.extend = (byte)extend;		
		this.secretKey = Cryptor.toBytes(key);
		this.revocationMode = revocationMode;
		this.validationMode = validationMode;
	}
	
	/**
	 * Set series default values for test.
	 */
	public boolean enableTestMode() {
		if(secretKey != null) return false;
		secretKey = Cryptor.toBytes(TEST_KEY);
		this.cuc =true;
		this.extend = 3;
		this.duration = 3600;
		revocationMode = RevocationMode.PSS.getValue();
		validationMode = ValidationMode.STRICT.getValue();
		return true;
	}
	
	public boolean isCuc() {
		return cuc;
	}
	
	public void setCuc(boolean cuc) {
		this.cuc = cuc;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public byte[] getSecretKey() {
		return secretKey;
	}
	
	public void setSecretKey(byte[] secretKey) {
		this.secretKey = secretKey;
	}
	
	public int getRevocationMode() {
		return revocationMode;
	}
	
	public RevocationMode toRevocationMode() {
		return RevocationMode.to(revocationMode);
	}
	
	public boolean isUserCollaborationMode() {
		return revocationMode == RevocationMode.UCM.getValue();
	}
	
	public boolean isPerSessionStateMode() {
		return revocationMode == RevocationMode.PSS.getValue();
	}
	
	public boolean isGlobalWhitelistMode() {
		return revocationMode == RevocationMode.PSS.getValue();
	}
	
	public void setRevocationMode(int revocationMode) {
		this.revocationMode = revocationMode;
	}
	
	public void setRevocationMode(RevocationMode revocationMode) {
		this.revocationMode = revocationMode.getValue();
	}
	
	public ValidationMode getValidationMode() {
		return ValidationMode.to(validationMode);
	}
	
	public void setValidationMode(int validationMode) {
		this.validationMode = validationMode;
	}
	
	public void setValidationMode(ValidationMode validationMode) {
		this.validationMode = validationMode.getValue();
	}
	
	public boolean isUAEnabled() {
		return validationMode != 1;
	}
	
	public boolean isIPEnabled() {
		return validationMode == 0;
	}
	
	public byte[] encrypt(byte[] content) {
		return Cryptor.encrypt(content, secretKey);
	}
	
	public byte[] decrypt(byte[] content) {
		return Cryptor.decrypt(content, secretKey);
	}

	public byte getExtend() {
		return extend;
	}

	public void setExtend(byte extend) {
		this.extend = extend;
	}
	
	public byte getRealExcetend(byte extend) {
		if(this.extend == 14) return extend; //Customized
		return this.extend; //0(Forbidden), 1 ~ 13, 15(Forever) 
	}
}