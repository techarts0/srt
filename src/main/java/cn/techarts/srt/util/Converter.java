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

/**
 * Convert the specific type to another. For an incorrect input parameter,<p>
 * 1. returns 0 or 0.0 if the target type is a numeric, or<br>
 * 2. returns null if the target type is a date string. 
 * @author rocwon@gmail.com
 */
public final class Converter {
	public static int toInt(byte[] bytes) {
		if(bytes == null) return 0;
		if(bytes.length != 4) return 0;
		return  (bytes[3] & 0xFF) |
	            (bytes[2] & 0xFF) << 8 |
	            (bytes[1] & 0xFF) << 16 |
	            (bytes[0] & 0xFF) << 24;
	}
	
	
	public static int toInt(byte b0, byte b1, byte b2, byte b3) {
		return  (b3 & 0xFF) |
	            (b2& 0xFF) << 8 |
	            (b1 & 0xFF) << 16 |
	            (b0 & 0xFF) << 24;
	}
	
	
	public static int toIntLE(byte[] bytes) {
		if(bytes == null) return 0;
		if(bytes.length != 4) return 0;
		return  (bytes[0] & 0xFF)       |
	            (bytes[1] & 0xFF) << 8  |
	            (bytes[2] & 0xFF) << 16 |
	            (bytes[3] & 0xFF) << 24;
	}
	
	public static long toLong(byte[] bytes) {
		var result = 0L;
		if(bytes == null) return result;
		if(bytes.length != 8) return result;
		result <<= 8; 
		result |= (bytes[0] & 0xff);
		result <<= 8; 
		result |= (bytes[1] & 0xff);
		result <<= 8; 
		result |= (bytes[2] & 0xff);
		result <<= 8; 
		result |= (bytes[3] & 0xff);
		result <<= 8; 
		result |= (bytes[4] & 0xff);
		result <<= 8; 
		result |= (bytes[5] & 0xff);
		result <<= 8; 
		result |= (bytes[6] & 0xff);
		result <<= 8; 
		result |= (bytes[7] & 0xff);
		return result;
	}
	
	public static long toLong(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7) {
		var result = 0L;
		result <<= 8; 
		result |= (b0 & 0xff);
		result <<= 8; 
		result |= (b1 & 0xff);
		result <<= 8; 
		result |= (b2 & 0xff);
		result <<= 8; 
		result |= (b3 & 0xff);
		result <<= 8; 
		result |= (b4 & 0xff);
		result <<= 8; 
		result |= (b5 & 0xff);
		result <<= 8; 
		result |= (b6 & 0xff);
		result <<= 8; 
		result |= (b7 & 0xff);
		return result;
	}
	
	public static long toLongLE(byte[] bytes) {
		if(bytes == null) return 0L;
		if(bytes.length != 8) return 0L;
		return 	(bytes[0] & 0xFF)       |
		        (bytes[1] & 0xFF) << 8  |
		        (bytes[2] & 0xFF) << 16 |
		        (bytes[3] & 0xFF) << 24 |
		        (bytes[4] & 0xFF) << 32 |
	            (bytes[5] & 0xFF) << 40 |
	            (bytes[6] & 0xFF) << 48 |
	            (bytes[7] & 0xFF) << 56;
	}
	
	public static short toShort(byte[] bytes) {
		if(bytes == null) return 0;
		if(bytes.length != 2) return 0;
		var result = (bytes[1] & 0xFF) |
		         (bytes[0] & 0xFF) << 8;		        
		return (short)result;
	}
	
	public static short toShortLE(byte[] bytes) {
		if(bytes == null) return 0;
		if(bytes.length != 2) return 0;
		var result = (bytes[0] & 0xFF) |
		         (bytes[1] & 0xFF) << 8;		        
		return (short)result;
	}
	
	/**
	 * Right to left. For example:<p>
	 * 0x4b -> 01001011 ->{true, true, false, true, false, false, true, false} 
	 */
	public static boolean[] toBooleans(byte arg) {
		var result = new boolean[8];
		for(int i = 0; i < 8; i++) {
			var b = (arg >> i) & 0x01;
			result[i] = b == 1;
		}
		return result;	
	}
	
	public static byte[] toBytes(long val) {
		byte[] result = new byte[8];      
		result[0] = (byte)((val >> 56) & 0xff);
		result[1] = (byte)((val >> 48) & 0xff);
		result[2] = (byte)((val >> 40) & 0xff);
		result[3] = (byte)((val >> 32) & 0xff);
		result[4] = (byte)((val >> 24) & 0xff);
		result[5] = (byte)((val >> 16) & 0xff);
		result[6] = (byte)((val >> 8) & 0xff);
		result[7] = (byte)(val & 0xff);
		return result;
	}
	
	public static byte[] toBytesLE(long val) {
		var result = new byte[8];
		result[0] = (byte)val;
		result[1] = (byte)(val >> 8);
		result[2] = (byte)(val >> 16);
		result[3] = (byte)(val >> 24);
		result[4] = (byte)(val >> 32);
		result[5] = (byte)(val >> 40);
		result[6] = (byte)(val >> 48);
		result[7] = (byte)(val >> 56);
		return result;
	}
	
	public static byte[] toBytes(int val) {
		var result = new byte[4];
		result[0] = (byte)(val >> 24);
		result[1] = (byte)(val >> 16);
		result[2] = (byte)(val >> 8);
		result[3] = (byte)val;
		return result;
	}
	
	public static byte[] toBytesLE(int val) {
		var result = new byte[4];
		result[0] = (byte)val;
		result[1] = (byte)(val >> 8);
		result[2] = (byte)(val >> 16);
		result[3] = (byte)(val >> 24);
		return result;
	}
	
	public static byte[] toBytes(short val) {
		var result = new byte[2];
		result[0] = (byte)(val >> 8);
		result[1] = (byte)val;
		return result;
	}
	
	public static byte[] toBytesLE(short val) {
		var result = new byte[4];
		result[0] = (byte)val;
		result[1] = (byte)(val >> 8);
		return result;
	}
}