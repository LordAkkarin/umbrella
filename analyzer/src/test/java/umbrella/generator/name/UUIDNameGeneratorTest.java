/*
 * Copyright 2014 Johannes Donath <johannesd@evil-co.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package umbrella.generator.name;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@RunWith (MockitoJUnitRunner.class)
public class UUIDNameGeneratorTest {

	/**
	 * Defines the type format.
	 */
	public static final String TYPE_FORMAT = "^([A-Z0-9]){32}$";

	/**
	 * Defines the member format.
	 */
	public static final String MEMBER_FORMAT = "^([a-z0-9]){32}$";

	/**
	 * Tests the name generation.
	 */
	@Test
	public void test () {
		// create generator
		UUIDNameGenerator generator = new UUIDNameGenerator ();

		// verify a couple of type names
		for (int i = 0; i < 128; i++) Assert.assertTrue ("Type Name does not match the expected format", generator.generateTypeName (null).matches (TYPE_FORMAT));

		// verify a couple of field names
		for (int i = 0; i < 128; i++) Assert.assertTrue ("Field Name does not match the expected format", generator.generateFieldName (null).matches (MEMBER_FORMAT));

		// verify a couple of method names
		for (int i = 0; i < 128; i++) Assert.assertTrue ("Field Name does not match the expected format", generator.generateMethodName (null).matches (MEMBER_FORMAT));

		// verify a couple of invoke dynamic methods
		for (int i = 0; i < 128; i++) Assert.assertTrue ("Field Name does not match the expected format", generator.generateInvokeDynamicMethodName (null).matches (MEMBER_FORMAT));
	}
}
