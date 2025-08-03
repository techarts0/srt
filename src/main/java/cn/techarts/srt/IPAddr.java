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

package cn.techarts.srt;

public enum IPAddr {
	NO(0),
	V4(1),
	V6(2);
	
	private int value;
	
	IPAddr(int value){
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public int getVersion() {
		return getValue();
	}
	
	public static IPAddr to(int value) {
		if(value > 2) return NO;
		if(value == 0) return NO;
		return value == 1 ? V4 : V6;
	}
	
	/**Get the length of IP address according to the version.*/
	public int getLength() {
		if(value > 2) return 0;
		if(value == 0) return 0;
		return value == 1 ? 4 : 16;
	}
	
	public byte bits() {
		return (byte)(((byte)value) << 6);
	}
}