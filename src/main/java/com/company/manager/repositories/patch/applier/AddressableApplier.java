package com.company.manager.repositories.patch.applier;

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;

import com.company.manager.controllers.exceptions.UnprocessableEntityException;
import com.company.manager.controllers.params.patch.Patch;
import com.company.manager.models.Addressable;
import com.company.manager.repositories.AddressableRepository;

public abstract class AddressableApplier<T extends Addressable> implements Applier{
	private static final String PATH_PHONE = "/phone";
	private static final String PATH_EMAIL = "/email";
	private static final String PATH_NAME = "/name";
	
	private final Addressable addressable;
	private final AddressableRepository<T> addressableRepository;

	public AddressableApplier(Addressable addressable, AddressableRepository<T> addressableRepository){
		this.addressable = addressable;
		this.addressableRepository = addressableRepository;
	}

	@Override
	public void apply(Patch<Object> patch) {
		if(patch.getPath().equals(PATH_NAME)){
			assertValue(patch, 0, 80);
			switch(patch.getOp()){
			case Replace:
				addressable.setName(patch.getValue().toString());
				break;
			case Test:
				if(!this.addressable.getName().equals(patch.getValue().toString())){
					throw new UnprocessableEntityException();
				}
				break;
			default:
				throw new UnprocessableEntityException();
			}
		}else if(patch.getPath().equals(PATH_EMAIL)){
			assertValue(patch, 0, 254);
			switch(patch.getOp()){
			case Replace:
				EmailValidator emailValidator = new EmailValidator();
				if(emailValidator.isValid(patch.getValue().toString(), null)){
					T previous = addressableRepository.searchPrevious(patch.getValue().toString());
					if(previous != null && !previous.getId().equals(this.addressable.getId())){
						throw new UnprocessableEntityException();
					}
					addressable.setEmail(patch.getValue().toString());
				}else{
					throw new UnprocessableEntityException();
				}
				break;
			case Test:
				if(!this.addressable.getEmail().equals(patch.getValue().toString())){
					throw new UnprocessableEntityException();
				}
				break;
			default:
				throw new UnprocessableEntityException();
			}
		}else if(patch.getPath().equals(PATH_PHONE)){
			switch(patch.getOp()){
			case Replace:
				assertValue(patch, 0, 50);
				addressable.setPhone(patch.getValue().toString());
				break;
			case Remove:
				addressable.setPhone(null);
				break;
			case Test:
				if(!this.addressable.getPhone().equals(patch.getValue().toString())){
					throw new UnprocessableEntityException();
				}
				break;
			default:
				throw new UnprocessableEntityException();
			}
		}
	}

	private void assertValue(Patch<Object> patch, int min, int max) {
		if(patch.getValue() != null){
			int length = patch.getValue().toString().length();
			if(length > min && length <= max){
				return;
			}
		}
		throw new UnprocessableEntityException();
	}

}
