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
public class MapNameGeneratorTest {

	/**
	 * Tests the generator.
	 */
	@Test
	public void testDefault () {
		// create generator
		MapNameGenerator generator = MapNameGenerator.DEFAULT;

		// test a few specific names
		Assert.assertEquals ("A", generator.generateTypeName (null));
		Assert.assertEquals ("B", generator.generateTypeName ("A"));
		Assert.assertEquals ("AA", generator.generateTypeName ("Z"));
		Assert.assertEquals ("BA", generator.generateTypeName ("AZ"));
		Assert.assertEquals ("CA", generator.generateTypeName ("BZ"));
		Assert.assertEquals ("AAA", generator.generateTypeName ("ZZ"));
		Assert.assertEquals ("AAB", generator.generateTypeName ("AAA"));
		Assert.assertEquals ("AAC", generator.generateTypeName ("AAB"));
		Assert.assertEquals ("ABA", generator.generateTypeName ("AAZ"));
		Assert.assertEquals ("ACA", generator.generateTypeName ("ABZ"));
		Assert.assertEquals ("BAA", generator.generateTypeName ("AZZ"));
	}
}
