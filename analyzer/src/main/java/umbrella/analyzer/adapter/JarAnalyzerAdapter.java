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

import com.google.common.base.Preconditions;
import lombok.NonNull;
import org.objectweb.asm.ClassReader;
import umbrella.analyzer.ClassReport;
import umbrella.utility.IOUtility;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class JarAnalyzerAdapter extends AbstractAnalyzerAdapter {

	/**
	 * Stores the jar file.
	 */
	@NonNull
	private final JarFile file;

	/**
	 * Constructs a new JarAnalyzerAdapter instance.
	 * @param file The file.
	 * @param priority The priority.
	 */
	public JarAnalyzerAdapter (@NonNull JarFile file, @NonNull Priority priority) {
		super (priority);
		this.file = file;
	}

	/**
	 * Constructs a new JarAnalyzerAdapter instance.
	 * @param file The file.
	 */
	public JarAnalyzerAdapter (@NonNull JarFile file) {
		this (file, Priority.NORMAL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean classExists (@NonNull String classPath) {
		return (this.file.getEntry (classPath + ".class") != null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClassReport getReport (@NonNull String classPath) throws IOException {
		// log
		getLogger ().trace ("Generating report for class \"" + classPath + "\" within adapter " + this.getClass ().getName () + ".");

		// append class suffix
		classPath += ".class";

		// get entry
		ZipEntry entry = this.file.getEntry (classPath);

		// verify
		Preconditions.checkNotNull (entry, "entry");

		// create variables
		InputStream inputStream = null;

		// create report
		try {
			// open input stream
			inputStream = this.file.getInputStream (entry);

			// create class reader
			ClassReader classReader = new ClassReader (inputStream);

			// generate report
			return (new ClassReport (classReader));
		} finally {
			IOUtility.closeQuietly (inputStream);
		}
	}
}
