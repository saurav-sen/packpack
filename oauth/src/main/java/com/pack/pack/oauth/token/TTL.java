package com.pack.pack.oauth.token;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

/**
 * 
 * @author Saurav
 *
 */
public class TTL implements DataSerializable {

	private int timeToLive;
	
	private TimeUnit timeUnit;
	
	public TTL() {
	}
	
	public TTL(int timeToLive, TimeUnit timeUnit) {
		this.timeToLive = timeToLive;
		this.timeUnit = timeUnit;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeInt(timeToLive);
		out.writeInt(timeUnit.ordinal());
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		timeToLive = in.readInt();
		timeUnit = TimeUnit.values()[in.readInt()];
	}
}