package com.company.manager.controllers.validators.patch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.company.manager.controllers.params.patch.Patch;


public class PatchOperationValidator implements ConstraintValidator<PatchOperation, Patch<Object>>{
	//private PatchOperation annotation;

	@Override
	public void initialize(PatchOperation annotation) {
		//this.annotation = annotation;
	}

	@Override
	public boolean isValid(Patch<Object> value, ConstraintValidatorContext context) {
		if(value == null) {
			return false;
		}
		if(value.getPath() == null){
			return false;
		}
		if(value.getOp() == null){
			return false;
		}
		switch(value.getOp()){
		case Remove:
			//nothing to validate
			break;
		case Move:
			//use copy
		case Copy:
			if(value.getFrom() == null){
				return false;
			}
			break;
		default:
			//add, test, replace
			if(value.getValue() == null){
				return false;
			}
			break;
		}
		return true;
	}

}
