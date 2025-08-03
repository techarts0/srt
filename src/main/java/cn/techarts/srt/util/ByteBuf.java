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

import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * ZERO-COPY :)<br>
 * 
 * It's a wrapper of JDK ByteBuffer but easier to use.
 * @author rocwon@gmail.com
 */
public final class ByteBuf {
	
	private ByteBuffer buffer = null;
	
	public ByteBuf(int capacity) {
		this.buffer = ByteBuffer.allocateDirect(capacity);
	}
	
	public ByteBuf(ByteBuffer buffer) {
		if(buffer == null) {
			throw new RuntimeException("Null buffer");
		}
		var pos = buffer.position();
		try {
			buffer.reset();
		}catch(InvalidMarkException e){
			buffer.position(0);//First time
		}
		buffer.limit(pos);
		this.buffer = buffer;
	}
	
	/**
	 * {@link ByteBuffer.remaining} <br>
	 * The remaining valid data length excluding consumed bytes.
	 */
	public int remaining() {
		return buffer.remaining();
	}
	
	/**
	 * {@link ByteBuffer.position}<br>
	 * The start index number of the remaining valid bytes.
	 */
	public int current() {
		return buffer.position();
	}
	
	/**
	 * {@link ByteBuffer.position}<br>
	 * Move the current pointer to the specific position.<br>
	 * IMPORTANT!!! It's very dangers to handle this pointer manually.
	 */
	public boolean current(int pos) {
		if(pos < 0) return false;
		if(pos >= buffer.limit()) return false;
		this.buffer.position(pos);
		return true;
	}
	
	/**
	 * {@link ByteBuffer.capacity}<br>
	 * The total length of the bytes array.
	 */
	public int capacity() {
		return this.buffer.capacity();
	}
	
	/**
	 * The remaining valid bytes length is great than your expectation at least.
	 */
	public boolean test(int expectation) {
		return buffer.remaining() >= expectation;
	}
	
	/**
	 * {@link ByteBuffer.get}<br>
	 * The read bytes will be discarded (invalid).
	 */
	public byte[] steal(int length) {
		if(buffer.remaining() < length) {
			return null; //Not enough
		}
		var result = new byte[length];
		this.buffer.get(result);
		compactBufferIfNecessary();
		return result;
	}
	
	/**
	 * {@link ByteBuffer.get}<br>
	 * Read the given bytes and moves the current pointer to next {@value skip}
	 */
	public byte[] steal(int length, int skip) {
		if(buffer.remaining() < length) {
			return null; //Not enough
		}
		var result = new byte[length];
		this.buffer.get(result);
		this.skip(skip);
		this.compactBufferIfNecessary();
		return result;
	}
	
	/**
	 * Read specific length of bytes but don't move the current pointer
	 */
	public byte[] lend(int pos, int length) {
		if(pos + length > buffer.limit()) return null;
		var result = new byte[length];
		this.buffer.mark();
		this.buffer.position(pos);
		this.buffer.get(result).reset();
		return result;
	}
	
	/**
	 * Read specific length of bytes but don't move the current pointer
	 */
	public byte[] lend2(int pos, int length) {
		if(pos + length > buffer.limit()) return null;
		var result = new byte[length];
		var mark = buffer.position();
		this.buffer.position(pos);
		this.buffer.get(result);
		this.buffer.position(mark);
		return result;
	}
	
	
	/**
	 * Read a byte but don't move the current pointer
	 */
	public byte lend(int pos) {
		this.buffer.mark();
		this.buffer.position(pos);
		var result = this.buffer.get();
		this.buffer.reset();
		return result;
	}
	
	/**
	 * {@link ByteBuffer.position}<br>
	 * Move the current pointer to {@value next}
	 */
	private void skip(int next) {
		var newPos = buffer.position() + next;
		if(newPos < buffer.limit()) {
			this.buffer.position(newPos);
		}
	}
	
	/**
	 * {@link ByteBuffer.compact}
	 */	
	private void compactBufferIfNecessary() {
		var free = buffer.capacity() - buffer.limit();
		if((free << 1) < buffer.position()) {
			this.buffer.compact();
		}
	}
	
	/**
	 * Please Note: if the ByteBuffer is not flipped, ZERO will be returned.
	 */
	private int free() {
		return buffer.capacity() - buffer.limit();
	}
	
	/**
	 * Very important!!!
	 * You MUST call the method before next reading data from SOCKET.
	 */
	public ByteBuffer setup() {
		//If the buffered bytes are consumed, clear it.
		if(buffer.position() + 1 == buffer.limit()) {
			return buffer.clear();
		}
		
		//If the free space is less than 1/8 of capacity, resize it.
		if((free() << 3) < buffer.capacity()) {
			resize(); //
		}else {
			this.buffer.mark(); //Start Point
			this.buffer.position(buffer.limit());
			this.buffer.limit(buffer.capacity());
		}
		return this.buffer;
	}
		
	/**
	 * Resize the buffer to 2 times of original capacity.
	 */
	private void resize() {
		var direct = buffer.isDirect();
		var capacity = buffer.capacity();
		var length = capacity << 1; // 2 times
		var newBuffer = allocate(direct, length);
		this.buffer = newBuffer.put(this.buffer);
	}
	
	//On JVM heap or physical memory
	private static ByteBuffer allocate(boolean direct, int capacity) {
		if(!direct) {
			return ByteBuffer.allocate(capacity);
		}else {
			return ByteBuffer.allocateDirect(capacity);
		}
	}
	
	//------------------------------------------------------
	
	public ByteBuf append(byte[] data) {
		if(data == null) return this;
		if(data.length == 0) return this;
		buffer.put(data);
		return this;
	}
	
	/**
	 * Append bytes where condition is true
	 */
	public ByteBuf appendOn(byte[] data, boolean condition) {
		if(!condition) return this;
		if(data == null) return this;
		if(data.length == 0) return this;
		buffer.put(data);
		return this;
	}
	
	public ByteBuf appendByte(byte data) {
		buffer.put(data);
		return this;
	}
	
	public ByteBuf appendShort(short data) {
		buffer.putShort(data);
		return this;
	}
	
	public ByteBuf appendInt(int data) {
		buffer.putInt(data);
		return this;
	}
	
	public ByteBuf appendLong(long data) {
		buffer.putLong(data);
		return this;
	}
	
	public ByteBuf appendFloat(float data) {
		buffer.putFloat(data);
		return this;
	}
	
	public ByteBuf appendDouble(double data) {
		buffer.putDouble(data);
		return this;
	}
	
	public ByteBuf appendChar(char data) {
		buffer.putChar(data);
		return this;
	}
	
	public ByteBuf append(String data, Charset charset) {
		if(data == null) return this;
		if(data.length() == 0) return this;
		buffer.put(data.getBytes(charset));
		return this;
	}
	
	public ByteBuf appendUTF8(String data) {
		return append(data, StandardCharsets.UTF_8);
	}
	
	public ByteBuf appendASCII(String data) {
		return append(data, StandardCharsets.US_ASCII);
	}
	
	public ByteBuffer toByteBuffer() {
		return this.buffer.flip();
	}
	
	public byte[] toBytes() {
		return this.lend2(0, buffer.limit());
	}
}