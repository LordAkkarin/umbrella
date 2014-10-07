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
package umbrella;

import com.google.common.io.ByteStreams;
import lombok.NonNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.RemappingClassAdapter;
import umbrella.asm.UmbrellaRemapper;
import umbrella.map.IMap;
import umbrella.utility.IOUtility;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class Umbrella {

	/**
	 * Internal constructor.
	 */
	private Umbrella () { }

	/**
	 * Applies a map.
	 * @param classReader The class reader.
	 * @param classWriter The class writer.
	 * @param map The map.
	 */
	public static void apply (@NonNull ClassReader classReader, @NonNull ClassWriter classWriter, @NonNull IMap map) {
		// create mapper
		UmbrellaRemapper remapper = new UmbrellaRemapper (map);

		// create adapter
		RemappingClassAdapter classAdapter = new RemappingClassAdapter (classWriter, remapper);

		// apply
		classReader.accept (classAdapter, ClassReader.SKIP_DEBUG | ClassReader.EXPAND_FRAMES);
	}

	/**
	 * Applies a map.
	 * @param inputStream The input stream.
	 * @param outputStream The output stream.
	 * @param map The map.
	 * @throws IOException Occurs if reading from the input stream is not possible.
	 */
	public static void apply (@NonNull InputStream inputStream, @NonNull OutputStream outputStream, @NonNull IMap map) throws IOException {
		// create class writer
		ClassWriter classWriter = new ClassWriter (ClassWriter.COMPUTE_MAXS);

		// apply a the map
		apply (new ClassReader (inputStream), classWriter, map);

		// write data
		outputStream.write (classWriter.toByteArray ());
	}

	/**
	 * Applies a map.
	 * @param input The input file.
	 * @param output The output file.
	 * @param map The map.
	 * @throws IOException Occurs if reading from the input file or writing to the output file is not possible.
	 */
	public static void apply (@NonNull File input, @NonNull File output, @NonNull IMap map) throws IOException {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;

		// apply map
		try {
			// open streams
			inputStream = new FileInputStream (input);
			outputStream = new FileOutputStream (output);

			// apply map
			apply (inputStream, outputStream, map);
		} finally {
			IOUtility.closeQuietly (outputStream);
			IOUtility.closeQuietly (inputStream);
		}
	}

	/**
	 * Applies a map.
	 * @param input The input jar.
	 * @param output The output jar.
	 * @param map The map.
	 * @throws IOException
	 */
	public static void apply (@NonNull JarFile input, @NonNull File output, @NonNull IMap map) throws IOException {
		OutputStream outputStream = null;
		ZipOutputStream zipOutputStream = null;

		// apply map
		try {
			// open streams
			outputStream = new FileOutputStream (output);
			zipOutputStream = new ZipOutputStream (outputStream);

			// iterate over all elements
			Enumeration<JarEntry> entries = input.entries ();

			while (entries.hasMoreElements ()) {
				// grab element
				JarEntry entry = entries.nextElement ();

				// define stream
				InputStream inputStream = null;

				// copy entry
				try {
					// open stream
					inputStream = input.getInputStream (entry);

					// skip non-class files
					if (!entry.getName ().endsWith (".class")) {
						// write entry to jar
						zipOutputStream.putNextEntry (entry);

						// write data
						ByteStreams.copy (inputStream, zipOutputStream);

						// skip further execution
						continue;
					}

					// create new entry
					zipOutputStream.putNextEntry (new ZipEntry (map.mapTypeName (entry.getName ().substring (0, entry.getName ().lastIndexOf (".")))));

					// write patch
					apply (inputStream, zipOutputStream, map);
				} finally {
					IOUtility.closeQuietly (input);
				}
			}
		} finally {
			IOUtility.closeQuietly (zipOutputStream);
			IOUtility.closeQuietly (outputStream);
		}
	}
}
