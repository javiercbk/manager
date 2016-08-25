package com.company.manager.repositories.patch.applier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.company.manager.controllers.exceptions.UnprocessableEntityException;
import com.company.manager.controllers.params.patch.OperationType;
import com.company.manager.controllers.params.patch.Patch;
import com.company.manager.models.Office;
import com.company.manager.repositories.OfficeRepository;

public class OfficeApplier implements Applier {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final String PATH_CLOSED = "/closed";
	private static final String PATH_OPENED = "/opened";
	private static final String PATH_LOCATION = "/location";
	private final Office office;
	private final OfficeRepository officeRepository;

	public OfficeApplier(Office office, OfficeRepository officeRepository) {
		this.office = office;
		this.officeRepository = officeRepository;
	}

	@Override
	public void apply(Patch<Object> patch) {
		Date date = null;
		if(patch.getValue() == null && !patch.getPath().equals(PATH_CLOSED) && !patch.getOp().equals(OperationType.Remove)){
			throw new UnprocessableEntityException();
		}
		if(patch.getPath().equals(PATH_LOCATION)){
			switch(patch.getOp()){
			case Replace:
				Office otherOffice = officeRepository.search(patch.getValue().toString());
				if(otherOffice != null && !otherOffice.getId().equals(this.office.getId())){
					throw new UnprocessableEntityException();
				}
				this.office.setLocation(patch.getValue().toString());
				break;
			case Test:
				if(!this.office.getLocation().equals(patch.getValue().toString())){
					throw new UnprocessableEntityException();
				}
				break;
			default:
				throw new UnprocessableEntityException();
			}
		}else if(patch.getPath().equals(PATH_OPENED)){
			switch(patch.getOp()){
			case Replace:
				date = parseDate(patch, sdf);
				this.office.setOpened(date);
				break;
			case Test:
				String format = sdf.format(this.office.getOpened());
				if(!format.equals(patch.getValue().toString())){
					throw new UnprocessableEntityException();
				}
				break;
			default:
				throw new UnprocessableEntityException();
			}
		}else if(patch.getPath().equals(PATH_CLOSED)){
			switch(patch.getOp()){
			case Replace:
				date = parseDate(patch, sdf);
				this.office.setClosed(date);
				break;
			case Test:
				String format = sdf.format(this.office.getClosed());
				if(!format.equals(patch.getValue().toString())){
					throw new UnprocessableEntityException();
				}
				break;
			case Remove:
				this.office.setClosed(null);
				break;
			default:
				throw new UnprocessableEntityException();
			}
		}
	}

	private Date parseDate(Patch<Object> patch, SimpleDateFormat sdf) {
		Date date = null;
		try{
			date = sdf.parse(patch.getValue().toString());
		}catch(ParseException pe){
			throw new UnprocessableEntityException();
		}
		return date;
	}

}
