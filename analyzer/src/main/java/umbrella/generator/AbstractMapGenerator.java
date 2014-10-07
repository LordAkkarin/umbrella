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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import umbrella.analyzer.Analyzer;
import umbrella.map.GenericMap;
import umbrella.map.IMap;
import umbrella.utility.IOUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public abstract class AbstractMapGenerator implements IMapGenerator {

	/**
	 * Stores an internal logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (AbstractMapGenerator.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate (@NonNull ClassReader classReader, @NonNull IMap map) throws Exception {
		this.generate (classReader, map, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMap generate (@NonNull ClassReader classReader, Analyzer analyzer) throws Exception {
		// create a new map
		IMap map = new GenericMap ();

		// generate elements
		this.generate (classReader, map, analyzer);

		// return finished map
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMap generate (@NonNull ClassReader classReader) throws Exception {
		return this.generate (classReader, ((Analyzer) null));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate (@NonNull InputStream inputStream, @NonNull IMap map, Analyzer analyzer) throws Exception {
		this.generate (new ClassReader (inputStream), map, analyzer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate (@NonNull InputStream inputStream, @NonNull IMap map) throws Exception {
		this.generate (inputStream, map, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMap generate (@NonNull InputStream inputStream, Analyzer analyzer) throws Exception {
		return this.generate (new ClassReader (inputStream), analyzer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMap generate (@NonNull InputStream inputStream) throws Exception {
		return this.generate (inputStream, ((Analyzer) null));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate (@NonNull File file, @NonNull IMap map, Analyzer analyzer) throws Exception {
		// handle directories
		if (file.isDirectory ()) {
			// iterate over files
			for (File element : file.listFiles ()) {
				// append to map
				this.generate (element, map, analyzer);
			}

			// skip further execution
			return;
		}

		// define variables
		FileInputStream inputStream = null;

		// generate map
		try {
			// open stream
			inputStream = new FileInputStream (file);

			// generate
			this.generate (inputStream, map, analyzer);
		} finally {
			IOUtility.closeQuietly (inputStream);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate (@NonNull File file, @NonNull IMap map) throws Exception {
		this.generate (file, map, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMap generate (@NonNull File file, Analyzer analyzer) throws Exception {
		// create map
		IMap map = new GenericMap ();

		// generate elements
		this.generate (file, map, analyzer);

		// return finished map
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMap generate (@NonNull File file) throws Exception {
		return this.generate (file, ((Analyzer) null));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate (@NonNull JarFile file, @NonNull IMap map, Analyzer analyzer) throws Exception {
		// store time
		long startTime = System.currentTimeMillis ();

		// iterate over class file elements
		Enumeration<JarEntry> entries = file.entries ();

		while (entries.hasMoreElements ()) {
			// grab current element
			JarEntry entry = entries.nextElement ();

			// skip non-class elements
			if (!entry.getName ().endsWith (".class")) continue;

			// prepare stream
			InputStream entryStream = null;

			// process entry
			try {
				// open stream
				entryStream = file.getInputStream (entry);

				// generate into map
				this.generate (entryStream, map, analyzer);
			} finally {
				IOUtility.closeQuietly (entryStream);
			}
		}

		// log time consumption
		getLogger ().debug ("Map generation for jar file took " + (System.currentTimeMillis () - startTime) + " ms.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate (@NonNull JarFile file, @NonNull IMap map) throws Exception {
		this.generate (file, map, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMap generate (@NonNull JarFile file, Analyzer analyzer) throws Exception {
		// create map
		IMap map = new GenericMap ();

		// generate elements
		this.generate (file, map, analyzer);

		// return generated map
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMap generate (@NonNull JarFile file) throws Exception {
		return this.generate (file, ((Analyzer) null));
	}
}
