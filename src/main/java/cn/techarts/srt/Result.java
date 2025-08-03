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

/**
 * The result of verification
 */
public enum Result {
	OK(0),
	/**Unsupported Version*/
	ERR_VER(-1),
	/**Token Expired*/
	EXPIRED(-2),
	/**IP Address Mismatched*/
	ERR_IP(-3),
	/**User-Agent Mismatched*/
	ERR_UA(-4),
	/**Invalid User-ID*/
	ERR_UID(-5),
	/**Invalid Random Salt*/
	ERR_SALT(-6),
	/**Invalid Context Hash*/
	ERR_HASH(-7),
	/**Invalid State*/
	ERR_STATE(-8);
	
	private int code;
	
	Result(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
}
