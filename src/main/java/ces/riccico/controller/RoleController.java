package ces.riccico.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import ces.riccico.models.Roles;
import ces.riccico.service.RoleService;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
public class RoleController {

	@Autowired
	RoleService rs;
	

	@RequestMapping(value = "/role", method = RequestMethod.GET)
	public List<Roles> getAll() {
		try {
			List<Roles> listRole = new ArrayList<Roles>();
			listRole = rs.findAll();
			return listRole;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}
	
	@RequestMapping(value = "/role/new", method = RequestMethod.POST)
	public void addAdmin(@RequestBody Roles model) {
		try {
			rs.save(model);
		} catch (Exception e) {
			System.out.println("addRole: " + e);
		}
	}
	
	@RequestMapping(value = "/role/edit", method = RequestMethod.POST)
	public void editAdmin(@RequestBody Roles model) {
		try {
			Optional<Roles> role = rs.findById(model.getIdRole());
			if (role.isPresent()) {
				role.get().setRoleName(model.getRoleName());
				rs.save(role.get());
			}
		} catch (Exception e) {
			System.out.println("editRole: " + e);
		}
	}
	
	@RequestMapping(value = "/role/delete", method = RequestMethod.POST)
	public void deleteCustomer(@RequestBody Roles model) {
		try {
			Optional<Roles> role = rs.findById(model.getIdRole());
			if (role.isPresent()) {
				rs.deleteById(role.get().getIdRole());
			}
		} catch (Exception e) {
			System.out.println("deleteCustomer: " + e);
		}
	}
	
}
