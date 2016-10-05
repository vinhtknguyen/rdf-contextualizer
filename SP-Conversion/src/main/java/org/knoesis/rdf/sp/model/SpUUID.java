package org.knoesis.rdf.sp.model;

import org.knoesis.rdf.sp.utils.Constants;

public class SpUUID {
	private long initUUIDNumber = System.currentTimeMillis();;
	private String initUUIDPrefix = Constants.SP_UUID_PREFIX;
	private String spDelimiter = Constants.SP_START_DELIMITER;
	
	public void setSPDelimiter(String delimiter){
		if (delimiter != null) spDelimiter = delimiter;
	}
	
	public SpUUID(){
		initUUIDNumber = System.currentTimeMillis();
		initUUIDPrefix = Constants.SP_UUID_PREFIX;
		spDelimiter = Constants.SP_START_DELIMITER;
		
	}
	
	public SpUUID(long _initUUIDNumber, String _initUUIDPrefix){
		initUUIDNumber = _initUUIDNumber;
		initUUIDPrefix = _initUUIDPrefix;
		spDelimiter = Constants.SP_START_DELIMITER;
	}
	
	public String getSPDelimiter(){
		return spDelimiter;
	}
	
	public void setInitUUIDPrefix(String pre){
		initUUIDPrefix = pre;
	}
	
	public String getInitUUIDPrefix(){
		return initUUIDPrefix;
	}
	
	public void setInitUUIDNumber(long num){
		initUUIDNumber = num;
	}

	public long getInitUUIDNumber(){
		return initUUIDNumber;
	}
	
	public String getNextUUID(){
		StringBuilder uuid = new StringBuilder(spDelimiter);
		uuid.append(Constants.SP_ID);
		uuid.append(Constants.SP_MID_DELIMITER);
		uuid.append(initUUIDPrefix);
		uuid.append(Constants.SP_MID_DELIMITER);
		uuid.append(initUUIDNumber);
		uuid.append(Constants.SP_END_DELIMITER);
		initUUIDNumber++;
		
		return uuid.toString();
	}

}
