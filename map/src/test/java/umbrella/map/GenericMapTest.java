package umbrella.map;/*
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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import umbrella.map.instruction.GenericFieldNameInstruction;
import umbrella.map.instruction.GenericInvokeDynamicMethodNameInstruction;
import umbrella.map.instruction.GenericMethodNameInstruction;
import umbrella.map.instruction.GenericTypeNameInstruction;

import java.io.File;
import java.io.IOException;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@RunWith (MockitoJUnitRunner.class)
public class GenericMapTest {

	/**
	 * Tests map loading.
	 */
	@Test
	public void testLoad () {
		// load map
		GenericMap map = new GenericMap (GenericMapTest.class.getResourceAsStream ("/test.map"));

		// verify results
		Assert.assertEquals ("Type did not resolve correctly", "A", map.mapTypeName ("Test"));
		Assert.assertEquals ("Field did not resolve correctly", "a", map.mapFieldName ("Test", "field1", "Ljava/lang/String"));
		Assert.assertEquals ("Method did not resolve correctly", "a", map.mapMethodName ("Test", "method1", "V"));
		Assert.assertEquals ("Invoke Dynamic Method did not resolve correctly", "a", map.mapInvokeDynamicMethodName ("dynamic1", "I"));
	}

	/**
	 * Tests saving and loading.
	 * @throws IOException Occurs if reading or writing from/to the file is not possible.
	 */
	@Test
	public void testSaveLoad () throws IOException {
		// create map
		GenericMap map = new GenericMap ();

		// append a few elements
		map.addInstruction (new GenericTypeNameInstruction ("Test"), new GenericTypeNameInstruction ("A"));
		map.addInstruction (new GenericFieldNameInstruction ("Test", "field1", "Ljava/lang/String"), new GenericFieldNameInstruction ("A", "a", "L/java/lang/String"));
		map.addInstruction (new GenericMethodNameInstruction ("Test", "method1", "V"), new GenericMethodNameInstruction ("A", "a", "V"));
		map.addInstruction (new GenericInvokeDynamicMethodNameInstruction ("dynamic1", "I"), new GenericInvokeDynamicMethodNameInstruction ("a", "I"));

		// create file reference
		File mapFile = new File ("test.map");

		// delete map (if existant)
		mapFile.delete ();

		// save map
		map.save (mapFile);

		// load file
		GenericMap map1 = new GenericMap (mapFile);

		// verify results
		Assert.assertEquals ("Type did not resolve correctly", "A", map1.mapTypeName ("Test"));
		Assert.assertEquals ("Field did not resolve correctly", "a", map1.mapFieldName ("Test", "field1", "Ljava/lang/String"));
		Assert.assertEquals ("Method did not resolve correctly", "a", map1.mapMethodName ("Test", "method1", "V"));
		Assert.assertEquals ("Invoke Dynamic Method did not resolve correctly", "a", map1.mapInvokeDynamicMethodName ("dynamic1", "I"));

		// mark file for deletion
		mapFile.deleteOnExit ();
	}

	/**
	 * Tests mappings.
	 */
	@Test
	public void testMapping () {
		// create map
		GenericMap map = new GenericMap ();

		// append a few elements
		map.addInstruction (new GenericTypeNameInstruction ("Test"), new GenericTypeNameInstruction ("A"));
		map.addInstruction (new GenericFieldNameInstruction ("Test", "field1", "Ljava/lang/String"), new GenericFieldNameInstruction ("A", "a", "L/java/lang/String"));
		map.addInstruction (new GenericMethodNameInstruction ("Test", "method1", "V"), new GenericMethodNameInstruction ("A", "a", "V"));
		map.addInstruction (new GenericInvokeDynamicMethodNameInstruction ("dynamic1", "I"), new GenericInvokeDynamicMethodNameInstruction ("a", "I"));

		// map
		Assert.assertEquals ("Type did not resolve correctly", "A", map.mapTypeName ("Test"));
		Assert.assertEquals ("Field did not resolve correctly", "a", map.mapFieldName ("Test", "field1", "Ljava/lang/String"));
		Assert.assertEquals ("Method did not resolve correctly", "a", map.mapMethodName ("Test", "method1", "V"));
		Assert.assertEquals ("Invoke Dynamic Method did not resolve correctly", "a", map.mapInvokeDynamicMethodName ("dynamic1", "I"));
	}
}
