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

import org.objectweb.asm.ClassReader;
import umbrella.analyzer.Analyzer;
import umbrella.map.IMap;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarFile;

/**
 * Defines a map generator.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface IMapGenerator {

	/**
	 * Appends to a map from a class reader.
	 * @param classReader The class reader.
	 * @param map The map.
	 * @param analyzer The analyzer.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public void generate (ClassReader classReader, IMap map, Analyzer analyzer) throws Exception;

	/**
	 * Appends to a map from a class reader.
	 * @param classReader The class reader.
	 * @param map The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public void generate (ClassReader classReader, IMap map) throws Exception;

	/**
	 * Generates a new map from a class reader.
	 * @param classReader The class reader.
	 * @param analyzer A pre-configured analyzer instance.
	 * @return The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public IMap generate (ClassReader classReader, Analyzer analyzer) throws Exception;

	/**
	 * Generates a new map from a class reader.
	 * @param classReader The class reader.
	 * @return The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public IMap generate (ClassReader classReader) throws Exception;

	/**
	 * Appends to a map from an input stream.
	 * @param inputStream The input stream.
	 * @param map The map.
	 * @param analyzer The analyzer.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public void generate (InputStream inputStream, IMap map, Analyzer analyzer) throws Exception;

	/**
	 * Appends to a map from an input stream.
	 * @param inputStream The input stream.
	 * @param map The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public void generate (InputStream inputStream, IMap map) throws Exception;

	/**
	 * Generates a new map from an input stream.
	 * @param inputStream The input stream.
	 * @param analyzer A pre-configured analyzer instance.
	 * @return The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public IMap generate (InputStream inputStream, Analyzer analyzer) throws Exception;

	/**
	 * Generates a new map from an input stream.
	 * @param inputStream The input stream.
	 * @return The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public IMap generate (InputStream inputStream) throws Exception;

	/**
	 * Appends to a map from a file or directory.
	 * @param file The class file or directory.
	 * @param map The map.
	 * @param analyzer The analyzer.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public void generate (File file, IMap map, Analyzer analyzer) throws Exception;

	/**
	 * Appends to a map from a file or directory.
	 * @param file The class file or directory.
	 * @param map The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public void generate (File file, IMap map) throws Exception;

	/**
	 * Generates a new map from a class file or directory.
	 * @param file The class file or directory.
	 * @param analyzer A pre-configured analyzer instance.
	 * @return The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public IMap generate (File file, Analyzer analyzer) throws Exception;

	/**
	 * Generates a new map from a class file or directory.
	 * @param file The class file or directory.
	 * @return The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public IMap generate (File file) throws Exception;

	/**
	 * Appends to a map from a jar file.
	 * @param file The jar file.
	 * @param map The map.
	 * @param analyzer The analyzer.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public void generate (JarFile file, IMap map, Analyzer analyzer) throws Exception;

	/**
	 * Appends to a map from a jar file.
	 * @param file The jar file.
	 * @param map The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public void generate (JarFile file, IMap map) throws Exception;

	/**
	 * Generates a new map from a jar file.
	 * @param file The jar file.
	 * @param analyzer A pre-configured analyzer instance.
	 * @return The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public IMap generate (JarFile file, Analyzer analyzer) throws Exception;

	/**
	 * Generates a new map from a jar file.
	 * @param file The jar file.
	 * @return The map.
	 * @throws Exception Occurs if generating the map fails.
	 */
	public IMap generate (JarFile file) throws Exception;
}
