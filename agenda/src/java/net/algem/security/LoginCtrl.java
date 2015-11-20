/*
 * @(#)LoginCtrl.java	1.0.0 11/02/13
 *
 * Copyright (c) 2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.security;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for login operations.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.0
 * @since 1.0.0 11/02/13
 */
@Controller
public class LoginCtrl {

	@Autowired
	private UserDao dao;
	@Autowired
	private UserValidator validator;

	public LoginCtrl() {
	}

	public LoginCtrl(UserDao dao) {
		this.dao = dao;
	}

	public void setValidator(UserValidator validator) {
		this.validator = validator;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/dologin.html")
	public String showLogin(@ModelAttribute("user") User user, Model model) {
//    model.addAttribute("login", "jm");
		model.addAttribute("userlist", dao.findAll());
		model.addAttribute("profiles", Profile.values());
		return "login";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/login.html")
	public String login(@ModelAttribute("user") User user, BindingResult result, Model model) {
		testBefore();
		validator.validate(user, result);
		if (result.hasErrors()) {
			model.addAttribute("profiles", Profile.values());
			return "login";
		}
		User found = null;
		try {
			found = dao.find(user.getLogin());
		} catch (DataAccessException e) {
			model.addAttribute("unknown", "Unknown user");
		}

		if (found == null) {
			model.addAttribute("profiles", Profile.values());
			return "login";
		}
		model.addAttribute("user", found);
		List<Map<String, Boolean>> list = dao.listMenuAccess(found.getId());
		model.addAttribute("acl", list);
		return "welcome";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user.html")
	public String showUserACL(HttpServletRequest request, Model model) {
		User found = null;
		found = dao.findById(Integer.parseInt(request.getParameter("id")));
		model.addAttribute("user", found);
		List<Map<String, Boolean>> list = dao.listMenuAccess(found.getId());
		model.addAttribute("acl", list);
		return "welcome";

	}

	public void testBefore() {
		System.out.println("test before");
	}
}
