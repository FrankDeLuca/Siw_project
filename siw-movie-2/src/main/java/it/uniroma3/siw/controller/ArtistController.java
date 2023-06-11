package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validation.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.FileUploadUtil;
import jakarta.validation.Valid;

@Controller
public class ArtistController {

@Autowired ArtistRepository artistRepository;
@Autowired ArtistValidator artistValidator;
@Autowired CredentialsService credentialsService;
@Autowired ArtistService artistService;

	
	
	@GetMapping("/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "admin/formNewArtist.html";
	}
	
	@GetMapping("/admin/modifyArtist/{id}")
	public String formNewArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistService.getArtist(id));
		return "/admin/formModifyArtist.html";
	}
	
	@PostMapping("/admin/formModifiedArtist/{id}")
	public String modify(@PathVariable("id") Long id, Model model, @RequestParam("name") String name, @RequestParam("surname") String surname, @RequestParam("birth") LocalDate birth, @Param("death") LocalDate death) {
		if(death != null) {
			this.artistService.modifyArtist(id, name, surname, birth, death);
		} else {
			this.artistService.modifyArtistsWithBirth(id, name, surname, birth);
		}
		model.addAttribute("artists", this.artistService.getAllArtists());
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			model.addAttribute("user", (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return "admin/manageArtists.html";
		} 
		return "artists.html";
	}
	
	@GetMapping("/admin/manageArtists/{id}")
	public String manageArtists(@PathVariable("id") Long id, Model model) {
		this.artistService.manageArtists(id, model);
		return "admin/manageArtists.html";
	}
	
	@PostMapping("/artists")
	public String newArtist(@Valid @ModelAttribute("artist")Artist artist, BindingResult bindingResult, @RequestParam("image") MultipartFile multipartFile, Model model) throws IOException {
		this.artistValidator.validate(artist, bindingResult);
		if(!bindingResult.hasErrors()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			artist.setPhotos(fileName);
			this.artistService.saveArtist(artist);
			model.addAttribute("artist", artist);
			String uploadDir = "artist-photos/" + artist.getId();
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			return "artist.html";
		} else {
			return "admin/formNewArtist.html";
		}
	}
	
	@GetMapping("/artists/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistService.getArtist(id));
		return "artist.html";
	}
	
	@GetMapping("/artists")
	public String showArtists(Model model) {
		model.addAttribute("artists", this.artistService.getAllArtists());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
	        return "artists.html";
		}
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			model.addAttribute("user", (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return "admin/manageArtists.html";
		} 
		return "artists.html";
	}
	
	@GetMapping("/admin/formSearchArtist")
	public String formSearchArtists() {
		return "admin/formSearchArtists.html";
	}
	
}
