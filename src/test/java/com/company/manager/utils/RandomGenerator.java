package com.company.manager.utils;

import java.util.UUID;

public class RandomGenerator {
	private RandomGenerator(){}
	
	public static String randomString(int length){
		StringBuilder randomStringBuilder = new StringBuilder(UUID.randomUUID().toString());
		while(randomStringBuilder.length() < length){
			randomStringBuilder.append(UUID.randomUUID().toString());
		}
		return randomStringBuilder.toString().substring(0, length - 1);
	}
}
