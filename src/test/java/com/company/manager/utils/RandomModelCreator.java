package com.company.manager.utils;

import java.util.List;

import com.google.common.collect.Lists;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

public class RandomModelCreator {
	
	private RandomModelCreator(){}
	
	public static <T> T randomEntity(Class<T> clazz){
		EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().build();
		return enhancedRandom.nextObject(clazz);
	}
	
	public static <T> List<T> randomEntities(Class<T> clazz, int size){
		List<T> randomEntities = Lists.newArrayList();
		for(int i = 0; i < size; i++){
			randomEntities.add(randomEntity(clazz));
		}
		return randomEntities;
	}
}
