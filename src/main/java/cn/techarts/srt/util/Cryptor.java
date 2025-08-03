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

package cn.techarts.srt.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;

/**
 * @author rocwon@gmail.com
 */
public final class Cryptor
{
	private static final String A_KEYPAIR = "RSA";
	private static final String A_SIGN = "SHA256withRSA";
	private static final String A_KEY = "AES";
	private static final int IV_SIZE = 12, TAG_SIZE = 128;
	private static final String A_SYMMTRIC = "AES/GCM/NoPadding";
	
	
	/**
	 *Convert bytes array to a hex string 
	 */
	public static String toHex(byte[] source, boolean upperCase) {
		var result = new StringBuilder(32);
		for(byte b : source) {
			int val = ((int)b) & 0xFF;
			if (val < 16) result.append("0");
			result.append(Integer.toHexString(val));
		}
		var encrypted = result.toString();
		return upperCase ? encrypted.toUpperCase() : encrypted;
    }
	
	/**
	 * Convert a hex string to bytes array
	 */
	public static byte[] toBytes(String hex) {
        if (Empty.is(hex)) return null;
        var hexLength = hex.length();
        var chars = hex.toCharArray();
        var result = new byte[hexLength / 2];
        for(int i = 0; i < result.length; i++) {
        	var hc = String.valueOf(chars[i * 2]);
        	var lc = String.valueOf(chars[i * 2 + 1]);
        	var hi = Integer.parseInt(hc, 16);
        	var li = Integer.parseInt(lc, 16);
        	result[i] = (byte)(hi * 16 + li);
        }
        return result;
	}
	
	//-------------------------------HASH(MD5, SHA-1, SHA256)-------------------------------------------
	
	/**Message Degist (Hash)*/
	public static String degist( String source, String algorithm){
		try{
			if(Empty.is(source)) return null;
			var mda = MessageDigest.getInstance(algorithm);
			byte[] original = source.getBytes("utf-8");
			mda.update(original);
			return toHex(mda.digest(original), false);
		}catch( Exception e){
			throw new RuntimeException( "Fail to encrypt [" + source + "].", e);
		}
	}
	
	//-----------------------------Symmetric encryption(AES)-------------------------------------
	
	public static byte[] getKey() {
		try {
			var gen = KeyGenerator.getInstance(A_KEY);
			gen.init(128); //
			return gen.generateKey().getEncoded();
		}catch(Exception e) {
			throw new RuntimeException("Failed to generate a key", e);
		}
	}
	
	/**
	 * @return Returns the generated key as hex string
	 */
	public static String getHexKey() {
		return toHex(getKey(), false);
	}
	
	private static byte[] getNonce() {
		var result = new byte[IV_SIZE];
		new SecureRandom().nextBytes(result);
		return result;
	}
	
	/**
	 * Supported Algorithm: AES-GCM
	 * @return Returns null if the key is invalid
	 */
	public static byte[] encrypt(byte[] source, byte[] key) {
		if(Empty.is(source) || Empty.is(key)) return null;
		try {
			var nonce = getNonce();
			var secretKey = new SecretKeySpec(key, A_KEY);
			var gcmSpec = new GCMParameterSpec(TAG_SIZE, nonce);
			Cipher cipher = Cipher.getInstance(A_SYMMTRIC);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
			var cipherBytes = cipher.doFinal(source);
			var length =cipherBytes.length;
			var result = new byte[IV_SIZE + length];
			System.arraycopy(nonce, 0, result, 0, IV_SIZE);
            System.arraycopy(cipherBytes, 0, result, IV_SIZE, length);
			return result;
		}catch(Exception e) {
			throw new RuntimeException("Failed to encrypt [" + source + "]", e);
		}
	}
		
	/**
	 * Supported Algorithm: AES-GCM
	 * @return Returns null if the key is invalid
	 */
	public static byte[] decrypt(byte[] target, byte[] key) {
		if(Empty.is(target) || Empty.is(key)) return null;
		try {
			
			 var iv = Arrays.copyOfRange(target, 0, IV_SIZE);
			 var cipherBytes = Arrays.copyOfRange(target, IV_SIZE, target.length);
			var secretKey = new SecretKeySpec(key, A_KEY);
			var cipher = Cipher.getInstance(A_SYMMTRIC);
			var gcmSpec = new GCMParameterSpec(TAG_SIZE, iv);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
			return cipher.doFinal(cipherBytes);
		}catch(Exception e) {
			throw new RuntimeException("Failed to decrypt [" + target + "]", e);
		}
	}
	
	//-------------------------------------Signature (SHA1withRSA)-------------------------------------------------
	/**
	 * Supported Algorithm: SHA1withRSA
	 */
	public static KeyPair getKeyPair() {
		try {
			var generator = KeyPairGenerator.getInstance(A_KEYPAIR);
			generator.initialize(512);
			return generator.genKeyPair();
		}catch(Exception e) {
			throw new RuntimeException("Failed to generate key pair", e);
		}
	}
	
	/**
	 * Supported Algorithm: SHA1withRSA
	 */
	public static byte[] getPrivateKey(KeyPair keys) {
		if(keys == null) return null;
		var privateKey = keys.getPrivate();
		return privateKey != null ? privateKey.getEncoded() : null;
	}
	
	/**
	 * Supported Algorithm: SHA1withRSA
	 */
	public static byte[] getPublicKey(KeyPair keys) {
		if(keys == null) return null;
		var publicKey = keys.getPublic();
		return publicKey != null ? publicKey.getEncoded() : null;
	}
	
	/**
	 * Supported Algorithm: SHA1withRSA
	 */
	public static String sign(String source, PrivateKey privateKey) {
	    if(source == null || privateKey == null) return null;
		try {
	        var signature = Signature.getInstance(A_SIGN);
	        signature.initSign(privateKey);
	        signature.update(source.getBytes("UTF-8"));
	        return toHex(signature.sign(), false);
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to signature [" + source + "]", e);
	    }
	}
	
	/**
	 * Supported Algorithm: SHA1withRSA
	 */
	public static boolean verify(String source, String sign, PublicKey publicKey) {
	    if(source == null || sign == null || publicKey == null) return false;
	    try {
	        var signature = Signature.getInstance(A_SIGN);
	        signature.initVerify(publicKey);
	        signature.update(source.getBytes("UTF-8"));
	        return signature.verify(toBytes(sign));
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	/**
	 * Encrypt the data with the specific key based on HMAC0SHA1
	 */
    public static byte[] hmac(byte[] data, byte[] key){
    	if(data == null || key == null) {
    		throw new RuntimeException("data or key is null.");
    	}
    	try {
	    	var hmac = Mac.getInstance("HmacSHA1");
	    	hmac.init(new SecretKeySpec(data, "RAW"));
        return hmac.doFinal(key);
    	}catch(GeneralSecurityException e) {
    		throw new RuntimeException("HMAC encrypt is failed.", e);
    	}
    }   
}