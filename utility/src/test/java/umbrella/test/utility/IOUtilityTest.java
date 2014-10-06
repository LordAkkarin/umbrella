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
package umbrella.test.utility;

import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import umbrella.utility.IOUtility;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@RunWith (MockitoJUnitRunner.class)
public class IOUtilityTest {

	/**
	 * Tests the closeQuietly method.
	 */
	@Test
	public void testCloseQuietly () {
		// try to close null
		IOUtility.closeQuietly (null);

		// create a test object
		TestClass testClass = new TestClass ();

		// verify
		Assert.assertFalse ("Test Object is already closed", testClass.closed);

		// close properly
		IOUtility.closeQuietly (testClass);

		// verify
		Assert.assertTrue ("Test Object has not been closed", testClass.closed);

		// try to close again (causes an error)
		IOUtility.closeQuietly (testClass);
	}

	/**
	 * A closable test object.
	 */
	public static class TestClass implements Closeable {

		/**
		 * Indicates whether the test class has been closed.
		 */
		private boolean closed = false;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close () throws IOException {
			if (this.closed) throw new IOException ("Test Exception");
			this.closed = true;
		}
	}
}
