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

public enum ValidationMode {
	STRICT(0),
	LOOSE(1),
	MOBILE(2);
	
	private int value;
	
	ValidationMode(int value){
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ValidationMode to(int value) {
		if(value > 2) return null;
		if(value == 0) return STRICT;
		return (value == 1) ? LOOSE : MOBILE;
	}	
}