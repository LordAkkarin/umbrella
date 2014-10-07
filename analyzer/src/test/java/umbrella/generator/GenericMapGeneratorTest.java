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
package umbrella.generator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import umbrella.generator.name.MapNameGenerator;
import umbrella.map.IMap;
import umbrella.map.instruction.utility.MapInstructionCategory;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@RunWith (MockitoJUnitRunner.class)
public class GenericMapGeneratorTest {

	/**
	 * Tests map generation.
	 */
	@Test
	public void test () throws Exception {
		// create a new generator instance
		GenericMapGenerator generator = new GenericMapGenerator (MapNameGenerator.DEFAULT);

		// generate for a test class
		IMap map = generator.generate (GenericMapGeneratorTest.class.getResourceAsStream ("Test.class"));

		// verify elements
		Assert.assertEquals ("Field instruction list does not contain exactly 7 instructions", 7, map.getInstructionMap (MapInstructionCategory.FIELD_NAME).size ());
		Assert.assertEquals ("Method instruction list does not contain exactly 7 instructions", 7, map.getInstructionMap (MapInstructionCategory.METHOD_NAME).size ());
		Assert.assertEquals ("Type instruction list does not contain exactly 5 instructions", 5, map.getInstructionMap (MapInstructionCategory.TYPE_NAME).size ());
	}
}
