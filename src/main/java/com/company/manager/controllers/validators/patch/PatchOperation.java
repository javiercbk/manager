package com.company.manager.controllers.validators.patch;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = {PatchOperationValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface PatchOperation {
	String message() default "Invalid patch Operation";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
