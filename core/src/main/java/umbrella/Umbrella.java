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

import com.google.common.base.Splitter;
import com.google.common.io.ByteStreams;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.RemappingClassAdapter;
import umbrella.analyzer.Analyzer;
import umbrella.analyzer.adapter.ExplodedAnalyzerAdapter;
import umbrella.analyzer.adapter.JarAnalyzerAdapter;
import umbrella.analyzer.adapter.JavaAnalyzerAdapter;
import umbrella.asm.UmbrellaRemapper;
import umbrella.generator.GenericMapGenerator;
import umbrella.generator.IMapGenerator;
import umbrella.generator.name.INameGenerator;
import umbrella.generator.name.MapNameGenerator;
import umbrella.generator.name.UUIDNameGenerator;
import umbrella.map.GenericMap;
import umbrella.map.IMap;
import umbrella.utility.IOUtility;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class Umbrella {

	/**
	 * Stores the application title.
	 */
	public static final String TITLE;

	/**
	 * Stores the application vendor.
	 */
	public static final String VENDOR;

	/**
	 * Stores the application version.
	 */
	public static final String VERSION;

	/**
	 * Stores the internal logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (Umbrella.class);

	/**
	 * Static Initialization
	 */
	static {
		// define variables
		String title = null;
		String vendor = null;
		String version = null;

		// grab information
		try {
			// locate package
			Package p = Umbrella.class.getPackage ();

			// grab attributes
			title = p.getImplementationTitle ();
			vendor = p.getImplementationVendor ();
			version = p.getImplementationVersion ();
		} catch (Exception ignore) { }

		// store
		TITLE = title;
		VENDOR = vendor;
		VERSION = version;
	}

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
		// handle directories
		if (input.exists () && input.isDirectory ()) {
			// iterate over all files
			for (File current : input.listFiles ()) {
				apply (current, new File (output, current.toURI ().relativize (current.toURI ()).getPath ()), map);
			}

			// skip further execution
			return;
		}

		// declare streams
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
				zipOutputStream.putNextEntry (new ZipEntry (map.mapTypeName (entry.getName ().substring (0, entry.getName ().lastIndexOf ("."))) + ".class"));

				// write patch
				apply (inputStream, zipOutputStream, map);
			}
		} finally {
			IOUtility.closeQuietly (zipOutputStream);
			IOUtility.closeQuietly (outputStream);
		}
	}

	/**
	 * Builds an analyzer instance based on a jar file.
	 * @param file The jar file.
	 * @param relativePath A relative path which is prepended to all elements.
	 * @return The analyzer.
	 * @throws IOException Occurs if reading any class path element is not possible.
	 */
	public static Analyzer getAnalyzer (@NonNull JarFile file, @NonNull String relativePath) throws IOException {
		// fix relative path
		if (relativePath == null) relativePath = "";

		// grab manifest
		Manifest manifest = file.getManifest ();

		// log
		getLogger ().debug ("Searching for class-path information within the jar ...");

		// get class path
		String classPath = manifest.getMainAttributes ().getValue ("Class-Path");

		// search Class-Path attribute
		if (classPath == null) return null;

		// create basic analyzer
		Analyzer analyzer = new Analyzer ();

		// reset analyzer
		analyzer.reset ();

		// append all elements
		for (String element : Splitter.on (' ').omitEmptyStrings ().splitToList (classPath)) {
			// create file
			File elementFile = new File (relativePath + element);

			// log
			getLogger ().debug ("Adding \"" + element + "\" to the analyzer class path.");

			// add adapter
			analyzer.addAdapter ((elementFile.isDirectory () ? new ExplodedAnalyzerAdapter (elementFile) : new JarAnalyzerAdapter (new JarFile (elementFile))));
		}

		// return finished analyzer instance
		return analyzer;
	}

	/**
	 * Builds an analyzer instance based on a classpath string.
	 * @param classpath The classpath.
	 * @return The analyzer.
	 * @throws IOException Occurs if reading any class path element is not possible.
	 */
	public static Analyzer getAnalyzer (@NonNull String classpath) throws IOException {
		// verify empty paths
		if (classpath == null || classpath.isEmpty ()) return null;

		// create basic analyzer
		Analyzer analyzer = new Analyzer ();

		// reset analyzer
		analyzer.reset ();

		// append all elements
		for (String element : Splitter.on (File.separatorChar).omitEmptyStrings ().splitToList (classpath)) {
			// check for java homes
			if (element.startsWith ("java:")) {
				// add adapter
				analyzer.addAdapter (new JavaAnalyzerAdapter (new File (element.substring (5))));

				// skip further execution
				continue;
			}

			// create file
			File elementFile = new File (element);

			// add adapter
			analyzer.addAdapter ((elementFile.isDirectory () ? new ExplodedAnalyzerAdapter (elementFile) : new JarAnalyzerAdapter (new JarFile (elementFile))));
		}

		// return finished analyzer instance
		return analyzer;
	}

	/**
	 * Returns the application options.
	 * @return The options.
	 */
	public static Options getApplicationOptions () {
		// create options object
		Options options = new Options ();

		// information options
		options.addOption (OptionBuilder.withLongOpt ("debug").withDescription ("Enables debug logging.").create ());
		options.addOption (OptionBuilder.withLongOpt ("help").withDescription ("Prints this help message and exits out.").create ('h'));
		options.addOption (OptionBuilder.withLongOpt ("trace").withDescription ("Enables trace logging.").create ());
		options.addOption (OptionBuilder.withLongOpt ("version").withDescription ("Prints the application version and exits out.").create ());

		// input options
		options.addOption (OptionBuilder.withLongOpt ("jar").withDescription ("Loads a jar as input.").hasArg ().withArgName ("jar file").create ('j'));
		options.addOption (OptionBuilder.withLongOpt ("file").withDescription ("Loads a class file or directory as input.").hasArg ().withArgName ("class file").create ('f'));

		// output options
		options.addOption (OptionBuilder.withLongOpt ("out").withDescription ("Specifies an output file or directory.").hasArg ().withArgName ("file or directory").create ('o'));

		// map options
		options.addOption (OptionBuilder.withLongOpt ("map").withDescription ("Specifies an input map.").hasArg ().withArgName ("map file").create ('m'));
		options.addOption (OptionBuilder.withLongOpt ("map-out").withDescription ("Specifies an output map.").hasArg ().withArgName ("map file").create ('w'));

		// map generation options
		options.addOption (OptionBuilder.withLongOpt ("preserve-package-structure").withDescription ("Causes the map generator to preserve package structures.").create ());
		options.addOption (OptionBuilder.withLongOpt ("disable-field-overloading").withDescription ("Disables field overloading within the map generator.").create ());
		options.addOption (OptionBuilder.withLongOpt ("disable-return-type-overloading").withDescription ("Disables method return type overloading within the map generator.").create ());
		// TODO: Add --keep option

		options.addOption (OptionBuilder.withLongOpt ("use-uuid-name-generator").withDescription ("Enables the UUID name generator.").create ());
		options.addOption (OptionBuilder.withLongOpt ("use-unicode-name-generator").withDescription ("Enables the Unicode name generator.").create ());

		options.addOption (OptionBuilder.withLongOpt ("classpath").withDescription ("Specifies a list of Jars or directories representing the Java classpath (a java home has be prefixed with \"java:\").").hasArg ().withArgName ("classpath").create ("cp"));

		// return finished object
		return options;
	}

	/**
	 * Main Entry Point
	 * @param arguments The command line arguments.
	 */
	public static void main (String[] arguments) {
		try {
			// parse command line
			CommandLine commandLine = (new PosixParser ()).parse (getApplicationOptions (), arguments);

			// call main
			main (commandLine);
		} catch (ParseException | IllegalArgumentException ex) {
			// notify user
			System.out.println ("Error: " + ex.getMessage ());

			// print application help
			printHelp ();

			// exit with status -1
			System.exit (-1);
		}
	}

	/**
	 * Main Entry Point
	 * @param commandLine The command line arguments.
	 * @throws IllegalArgumentException Occurs if the supplied parameters are not valid.
	 */
	public static void main (CommandLine commandLine) throws IllegalArgumentException {
		// print application version
		if (commandLine.hasOption ("version")) {
			// print version
			System.out.println ((VERSION != null ? VERSION : "Development Snapshot"));

			// exit with status 0
			System.exit (0);
		}

		// print version and copyright
		System.out.println ("Umbrella Framework " + (VERSION != null ? "v" + VERSION : "(Development Snapshot)"));
		System.out.println ("Copyright (C) 2014 Johannes Donath <http://www.evil-co.org>");
		System.out.println ("Licensed under the terms of the Apache 2.0 License");
		System.out.println ("--------------------------------------------------");

		// set logging
		if (commandLine.hasOption ("debug") || commandLine.hasOption ("trace")) {
			// get context & configuration
			LoggerContext context = ((LoggerContext) LogManager.getContext (false));
			Configuration config = context.getConfiguration ();

			// set new level
			config.getLoggerConfig (LogManager.ROOT_LOGGER_NAME).setLevel ((commandLine.hasOption ("trace") ? Level.ALL : Level.DEBUG));

			// update context
			context.updateLoggers (config);

			// log
			getLogger ().info ("Logging messages up to " + (commandLine.hasOption ("trace") ? "trace" : "debug") + " will be displayed.");
		}

		// log application information
		getLogger ().debug ("Application Brand: " + (TITLE != null ? TITLE : "Unknown"));
		getLogger ().debug ("Application Vendor: " + (VENDOR != null ? VENDOR : "Unknown"));
		getLogger ().debug ("Application Version: " + (VERSION != null ? VERSION : "Unknown"));

		getLogger ().debug ("Java Version: " + System.getProperty ("java.version"));
		getLogger ().debug ("Java Vendor: " + System.getProperty ("java.vendor") + (System.getProperty ("java.vendor.url") != null ? " (" + System.getProperty ("java.vendor.url") + ")" : ""));
		getLogger ().debug ("Java Home: " + System.getProperty ("java.home"));

		getLogger ().debug ("OS: " + System.getProperty ("os.name") + " " + System.getProperty ("os.version") + " (" + System.getProperty ("os.arch") + ")");

		// verify argument combinations
		if (!commandLine.hasOption ("jar") && !commandLine.hasOption ("file")) throw new IllegalArgumentException ("No input input file has been specified");
		if (commandLine.hasOption ("jar") && commandLine.hasOption ("file")) throw new IllegalArgumentException ("More than one input file has been specified");
		if (!commandLine.hasOption ("map-out") && !commandLine.hasOption ("out")) throw new IllegalArgumentException ("No action specified - Aborting");
		if (commandLine.hasOption ("use-unicode-name-generator") && commandLine.hasOption ("use-uuid-name-generator")) throw new IllegalArgumentException ("More than one name generator specified");

		try {
			// get input file
			File input = null;
			JarFile inputJar = null;

			if (commandLine.hasOption ("jar")) {
				input = (new File (commandLine.getOptionValue ("jar"))).getAbsoluteFile ();
				inputJar = new JarFile (input);
			} else if (commandLine.hasOption ("file")) {
				input = (new File (commandLine.getOptionValue ("file"))).getAbsoluteFile ();
			}

			// get map
			IMap map = null;

			// generate map
			if (!commandLine.hasOption ("map")) {
				// log
				getLogger ().info ("Generating a new obfuscation map ...");

				// create a new name generator
				INameGenerator nameGenerator = (commandLine.hasOption ("use-unicode-name-generator") ? MapNameGenerator.UNICODE : (commandLine.hasOption ("use-uuid-name-generator") ? new UUIDNameGenerator () : MapNameGenerator.DEFAULT));

				// create a new generator
				GenericMapGenerator generator = new GenericMapGenerator (nameGenerator, !commandLine.hasOption ("preserve-package-structure"), !commandLine.hasOption ("disable-field-overloading"), !commandLine.hasOption ("disable-return-type-overloading"));

				// build relative path
				String relativePath = null;
				if (inputJar != null) relativePath = new File (".").toURI ().relativize (input.getParentFile ().toURI ()).getPath ();

				// build analyzer
				Analyzer analyzer = (inputJar != null ? getAnalyzer (inputJar, relativePath) : (commandLine.hasOption ("classpath") ? getAnalyzer (commandLine.getOptionValue ("classpath")) : null));

				// generate a new map
				if (inputJar != null)
					map = generator.generate (inputJar, analyzer);
				else
					map = generator.generate (input, analyzer);

				// log
				getLogger ().info ("Map generation finished.");
			} else {
				// get file
				File mapFile = new File (commandLine.getOptionValue ("map"));

				// log
				getLogger ().info ("Loading map \"" + mapFile.getName () + "\" ...");

				// store time
				long startTime = System.currentTimeMillis ();

				// open map
				map = new GenericMap (mapFile);

				// log
				getLogger ().info ("Finished - Map parsing took " + (System.currentTimeMillis () - startTime) + " ms.");
			}

			// write map
			if (commandLine.hasOption ("map-out")) {
				// get file
				File mapFile = new File (commandLine.getOptionValue ("map-out"));

				// log
				getLogger ().info ("Saving map to file \"" + mapFile.getName () + "\" ...");

				// write
				map.save (mapFile);

				// log
				getLogger ().info ("Map was saved successfully.");
			}

			// get output
			File output = (commandLine.hasOption ("out") ? new File (commandLine.getOptionValue ("out")) : null);

			// write
			if (output != null) {
				// log
				getLogger ().info ("Applying map ...");

				// apply map
				if (inputJar != null)
					apply (inputJar, output, map);
				else
					apply (input, output, map);

				// log
				getLogger ().info ("Finished patching.");
			}

			// log
			getLogger ().info ("Processing has finished successfully.");
			System.exit (1);
		} catch (Exception ex) {
			getLogger ().error ("An application error occurred: " + ex.getMessage (), ex);
			getLogger ().error ("One or more actions failed. Exiting.");
			System.exit (-256);
		}
	}

	/**
	 * Prints the command line help.
	 */
	public static void printHelp () {
		(new HelpFormatter ()).printHelp ("umbrella <arguments>", getApplicationOptions ());
	}
}
