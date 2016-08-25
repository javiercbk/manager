package com.company.manager.controllers.validators.patch;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.company.manager.controllers.params.patch.Patch;


@SuppressWarnings("rawtypes")
public class PatchListValidator implements ConstraintValidator<PatchList, List<Patch>>{
	//private PatchList annotation;

	@Override
	public void initialize(PatchList annotation) {
		//this.annotation = annotation;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isValid(List<Patch> value, ConstraintValidatorContext context) {
		if(value == null ||  value.isEmpty()){
			return false;
		}
		PatchOperationValidator patchValidator = new PatchOperationValidator();
		for(Patch patch : value){
			if(!patchValidator.isValid(patch, context)){
				return false;
			}
		}
		return true;
	}

}
