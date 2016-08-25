package com.company.manager.controllers.params.office;

import javax.validation.constraints.Size;

public class OfficeSearchParams {
	@Size(max=100)
	private String location;
}
