package com.company.manager.controllers.params.patch;

import java.util.List;

import com.company.manager.controllers.validators.patch.PatchList;

@SuppressWarnings("rawtypes")
public class Patches{
	@PatchList
	private List<Patch> patches;
	
	public Patches(){}
	
	public Patches(List<Patch> patches){
		this.patches = patches;
	}

	public List<Patch> getPatches() {
		return patches;
	}

	public void setPatches(List<Patch> patches) {
		this.patches = patches;
	}	
}
