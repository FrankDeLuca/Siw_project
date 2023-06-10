package it.uniroma3.siw.controller.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.service.ArtistService;

@Component
public class ArtistValidator implements Validator{
	
	@Autowired
	private ArtistService artistService;

	@Override
	public boolean supports(Class<?> aClass) {
		return Artist.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		Artist artist = (Artist) o;
		if(artist.getName() != null && artist.getSurname() != null && artistService.existByNameAndSurname(artist.getName(), artist.getSurname())) {
			errors.reject("artist.duplicate");
		}
	}

}
