package com.company.manager.repositories.patch.applier;

import com.company.manager.controllers.params.patch.Patch;

public interface Applier {
	public void apply(Patch<Object> patch);
}
