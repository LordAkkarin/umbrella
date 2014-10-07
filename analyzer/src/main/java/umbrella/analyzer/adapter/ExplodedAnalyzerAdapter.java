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
import org.objectweb.asm.ClassReader;
import umbrella.analyzer.ClassReport;
import umbrella.utility.IOUtility;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class ExplodedAnalyzerAdapter extends AbstractAnalyzerAdapter {

	/**
	 * Stores the parent directory.
	 */
	private final File parent;

	/**
	 * Constructs a new ExplodedAnalyzerAdapter instance.
	 * @param file The file.
	 * @param priority The priority.
	 */
	public ExplodedAnalyzerAdapter (@NonNull File file, @NonNull Priority priority) {
		super (priority);
		this.parent = file;
	}

	/**
	 * Constructs a new ExplodedAnalyzerAdapter instance.
	 * @param file The file.
	 */
	public ExplodedAnalyzerAdapter (@NonNull File file) {
		this (file, Priority.NORMAL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean classExists (String classPath) {
		return this.getClassPath (classPath).exists ();
	}

	/**
	 * Returns the class path.
	 * @param classPath The class path.
	 * @return The path.
	 */
	public File getClassPath (@NonNull String classPath) {
		return new File (this.parent, classPath.replace ('/', File.separatorChar) + ".class");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClassReport getReport (String classPath) throws Exception {
		// log
		getLogger ().trace ("Generating report for class \"" + classPath + "\" within adapter " + this.getClass ().getName () + ".");

		// initialize variable
		FileInputStream inputStream = null;

		// process
		try {
			// open file
			inputStream = new FileInputStream (this.getClassPath (classPath));

			// create class reader
			ClassReader reader = new ClassReader (inputStream);

			// generate report
			return (new ClassReport (reader));
		} finally {
			IOUtility.closeQuietly (inputStream);
		}
	}
}
