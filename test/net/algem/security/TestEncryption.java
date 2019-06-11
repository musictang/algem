/*
 * @(#)TestEncryption.java	2.8.p 30/10/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package net.algem.security;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.p 30/10/13
 * @since 2.8.p 30/10/13
 */
public class TestEncryption {

	public TestEncryption() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void encrypt() throws NoSuchAlgorithmException, InvalidKeySpecException {
		PasswordEncryptionService service = new PasswordEncryptionService();
		String mypass = "bug";
		byte[] salt = service.generateSalt();
		byte[] mypassE = service.getEncryptedPassword(mypass, salt);

		assertFalse(service.authenticate("buG", mypassE, salt));
		assertTrue(service.authenticate("bug", mypassE, salt));
		salt = service.generateSalt();
		assertFalse(service.authenticate("bug", mypassE, salt));
	}
}
