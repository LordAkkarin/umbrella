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
package umbrella.analyzer.adapter;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class JavaAnalyzerAdapter extends DelegatedAnalyzerAdapter {

	/**
	 * Constructs a new JavaAnalyzerAdapter instance.
	 * @param home The java home.
	 */
	public JavaAnalyzerAdapter (@NonNull File home) throws IOException {
		super (getAdapterList (home), Priority.NORMAL);
	}

	/**
	 * Returns a list of Java analyzer adapters.
	 * @param home The java home.
	 * @return A list of adapters.
	 * @throws IOException
	 */
	protected static List<IAnalyzerAdapter> getAdapterList (File home) throws IOException {
		// check existence
		if (!home.exists () || !home.isDirectory ()) throw new IOException ("Java home is invalid: No such directory");

		// get lib directory
		File libraryDirectory = new File (home, "lib");

		// check existence
		if (!libraryDirectory.exists () || !libraryDirectory.isDirectory ()) throw new IOException ("Java home is invalid: No library directory");

		// create list
		ArrayList<IAnalyzerAdapter> adapters = new ArrayList<> ();

		// append known libraries
		adapters.add (new JarAnalyzerAdapter (new JarFile (new File (libraryDirectory, "charsets.jar"))));
		adapters.add (new JarAnalyzerAdapter (new JarFile (new File (libraryDirectory, "deploy.jar"))));
		adapters.add (new JarAnalyzerAdapter (new JarFile (new File (libraryDirectory, "javaws.jar"))));
		adapters.add (new JarAnalyzerAdapter (new JarFile (new File (libraryDirectory, "jce.jar"))));
		adapters.add (new JarAnalyzerAdapter (new JarFile (new File (libraryDirectory, "jfr.jar"))));
		try { adapters.add (new JarAnalyzerAdapter (new JarFile (new File (libraryDirectory, "jfxswt.jar")))); } catch (IOException ignore) { } // available as of Java 8
		adapters.add (new JarAnalyzerAdapter (new JarFile (new File (libraryDirectory, "jsse.jar"))));
		adapters.add (new JarAnalyzerAdapter (new JarFile (new File (libraryDirectory, "management-agent.jar"))));
		adapters.add (new JarAnalyzerAdapter (new JarFile (new File (libraryDirectory, "plugin.jar"))));
		adapters.add (new JarAnalyzerAdapter (new JarFile (new File (libraryDirectory, "resources.jar"))));
		adapters.add (new JarAnalyzerAdapter (new JarFile (new File (libraryDirectory, "rt.jar")), Priority.HIGH));

		// return finished list
		return adapters;
	}
}
