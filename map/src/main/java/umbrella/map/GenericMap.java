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
package umbrella.map;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.*;
import umbrella.map.instruction.*;
import umbrella.map.instruction.utility.IMapInstructionRegistry;
import umbrella.map.instruction.utility.MapInstructionCategory;
import umbrella.utility.IOUtility;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class GenericMap implements IMap {

	/**
	 * Stores the library version.
	 */
	public static final String VERSION;

	/**
	 * Stores all instructions.
	 */
	private final Table<MapInstructionCategory, IMapInstruction, IMapInstruction> instructionMap = HashBasedTable.create ();

	/**
	 * Stores the instruction registry.
	 */
	@Getter
	@Setter (AccessLevel.PROTECTED)
	@NonNull
	private IMapInstructionRegistry instructionRegistry;

	/**
	 * Static Initializer
	 */
	static {
		String version = null;

		// get package version
		try { version = GenericMap.class.getPackage ().getImplementationVersion (); } catch (Exception ex) { }

		// store
		VERSION = version;
	}

	/**
	 * Constructs a new AbstractMap instance.
	 * @since 1.0.0
	 */
	public GenericMap () {
		this (IMapInstructionRegistry.DEFAULT);
	}

	/**
	 * Constructs a new AbstractMap instance.
	 * @param map The map to copy.
	 * @since 1.0.0
	 */
	public GenericMap (IMap map) {
		this (IMapInstructionRegistry.DEFAULT, map);
	}

	/**
	 * Constructs a new AbstractMap instance.
	 * @param registry The instruction registry.
	 * @since 1.0.0
	 */
	public GenericMap (@NonNull IMapInstructionRegistry registry) {
		this.instructionRegistry = registry;

		// reset
		this.reset ();
	}

	/**
	 * Constructs a new AbstractMap instance.
	 * @param registry The instruction registry.
	 * @param map The map to copy.
	 * @since 1.0.0
	 */
	public GenericMap (@NonNull IMapInstructionRegistry registry, @NonNull IMap map) {
		this (registry);

		// merge map
		this.merge (map);
	}

	/**
	 * Constructs a new GenericMap instance.
	 * @param registry The instruction registry.
	 * @param inputStream The input stream.
	 * @param close Indicates whether the stream shall be closed.
	 */
	protected GenericMap (@NonNull IMapInstructionRegistry registry, @NonNull InputStream inputStream, boolean close) {
		this (registry);

		// define variables
		Scanner scanner = null;

		// load data
		try {
			// open scanner
			scanner = new Scanner (inputStream);

			// initialize line number
			int lineNumber = 0;

			// load all lines
			while (scanner.hasNextLine ()) {
				// update line number
				lineNumber++;

				// extract line
				String line = scanner.nextLine ();

				// check line
				if (line.isEmpty () || line.startsWith ("//")) continue;

				// split mapping
				List<String> elements = Splitter.on (' ').limit (4).splitToList (line);

				// verify count
				Preconditions.checkArgument (elements.size () == 4, "Mapping on line " + lineNumber + " does not contain exactly 4 elements");

				// parse types
				IMapInstruction original = this.getInstructionRegistry ().getElement (elements.get (0), elements.get (1));
				IMapInstruction replacement = this.getInstructionRegistry ().getElement (elements.get (2), elements.get (3));

				// add instruction
				this.addInstruction (original, replacement);
			}
		} finally {
			IOUtility.closeQuietly (scanner);
			if (close) IOUtility.closeQuietly (inputStream);
		}
	}

	/**
	 * Constructs a new GenericMap instance.
	 * @param registry The instruction registry.
	 * @param inputStream The input stream.
	 */
	public GenericMap (@NonNull IMapInstructionRegistry registry, @NonNull InputStream inputStream) {
		this (registry, inputStream, false);
	}

	/**
	 * Constructs a new GenericMap instance.
	 * @param inputStream The input stream.
	 */
	public GenericMap (@NonNull InputStream inputStream) {
		this (IMapInstructionRegistry.DEFAULT, inputStream);
	}

	/**
	 * Constructs a new GenericMap instance.
	 * @param registry The instruction registry.
	 * @param file The file.
	 * @throws IOException Occurs if reading from the file is not possible.
	 */
	public GenericMap (@NonNull IMapInstructionRegistry registry, @NonNull File file) throws IOException {
		this (registry, (new FileInputStream (file)), true);
	}

	/**
	 * Constructs a new GenericMap instance.
	 * @param file The file.
	 * @throws IOException Occurs if reading from the file is not possible.
	 */
	public GenericMap (@NonNull File file) throws IOException {
		this (IMapInstructionRegistry.DEFAULT, file);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addInstruction (@NonNull IMapInstruction original, @NonNull IMapInstruction replacement) {
		// get categories
		MapInstructionCategory originalCategory = MapInstructionCategory.valueOf (original);

		// verify category
		Preconditions.checkArgument (originalCategory == MapInstructionCategory.valueOf (replacement), "Instruction categories do not match");

		// store
		this.instructionMap.put (MapInstructionCategory.valueOf (original), original, replacement);
	}

	/**
	 * Adds a set of instructions.
	 * @param instructionMap The instruction map.
	 * @since 1.0.0
	 */
	public void addInstructions (@NonNull Map<IMapInstruction, IMapInstruction> instructionMap) {
		for (Map.Entry<IMapInstruction, IMapInstruction> entry : instructionMap.entrySet ()) this.addInstruction (entry.getKey (), entry.getValue ());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFieldNameInstruction getFieldNameInstruction (String owner, String name, String description) {
		return ((IFieldNameInstruction) this.instructionMap.get (MapInstructionCategory.FIELD_NAME, new GenericFieldNameInstruction (owner, name, description)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IInvokeDynamicMethodNameMapInstruction getInvokeDynamicMethodNameInstruction (String name, String description) {
		return ((IInvokeDynamicMethodNameMapInstruction) this.instructionMap.get (MapInstructionCategory.INVOKE_DYNAMIC_METHOD_NAME, new GenericInvokeDynamicMethodNameInstruction (name, description)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMethodNameInstruction getMethodNameInstruction (String owner, String name, String description) {
		return ((IMethodNameInstruction) this.instructionMap.get (MapInstructionCategory.METHOD_NAME, new GenericMethodNameInstruction (owner, name, description)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeNameMapInstruction getTypeNameInstruction (String name) {
		return ((ITypeNameMapInstruction) this.instructionMap.get (MapInstructionCategory.TYPE_NAME, new GenericTypeNameInstruction (name)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<IMapInstruction, IMapInstruction> getInstructionMap () {
		return this.getInstructionMap (((MapInstructionCategory) null));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<IMapInstruction, IMapInstruction> getInstructionMap (MapInstructionCategory category) {
		// generate map
		if (category == null) {
			// create empty map
			Map<IMapInstruction, IMapInstruction> map = new HashMap<> ();

			// append all elements
			for (Map.Entry<MapInstructionCategory, Map<IMapInstruction, IMapInstruction>> entry : this.instructionMap.rowMap ().entrySet ()) {
				map.putAll (entry.getValue ());
			}

			// return finished map
			return map;
		}

		return this.instructionMap.row (category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends IMapInstruction> Map<T, IMapInstruction> getInstructionMap (Class<T> instructionType, boolean deep) {
		// construct an empty map
		Map<IMapInstruction, IMapInstruction> map = new HashMap<> ();

		// copy
		if (deep) {
			for (Map.Entry<MapInstructionCategory, Map<IMapInstruction, IMapInstruction>> entry : this.instructionMap.rowMap ().entrySet ()) {
				map.putAll (entry.getValue ());
			}
		} else
			map.putAll (this.instructionMap.row (MapInstructionCategory.valueOf (instructionType)));

		// remove elements which do not match
		for (Map.Entry<IMapInstruction, IMapInstruction> entry : map.entrySet ()) {
			if (!entry.getKey ().getClass ().isAssignableFrom (instructionType)) map.remove (entry.getKey ());
		}

		// return finished map
		return ((Map<T, IMapInstruction>) map);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends IMapInstruction> Map<T, IMapInstruction> getInstructionMap (Class<T> instructionType) {
		return this.getInstructionMap (instructionType, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String mapDescription (String description) {
		System.out.println (description);
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean mappingExists (@NonNull IMapInstruction instruction) {
		for (Table.Cell<MapInstructionCategory, IMapInstruction, IMapInstruction> entry : this.instructionMap.cellSet ()) {
			if (entry.getValue ().equals (instruction)) return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String mapFieldName (String owner, String name, String description) {
		IFieldNameInstruction instruction = this.getFieldNameInstruction (owner, name, description);
		return (instruction != null ? instruction.getName () : name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String mapInvokeDynamicMethodName (String name, String description) {
		IInvokeDynamicMethodNameMapInstruction instruction = this.getInvokeDynamicMethodNameInstruction (name, description);
		return (instruction != null ? instruction.getName () : name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String mapMethodName (String owner, String name, String description) {
		IMethodNameInstruction instruction = this.getMethodNameInstruction (owner, name, description);
		return (instruction != null ? instruction.getName () : name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String mapTypeName (String name) {
		ITypeNameMapInstruction instruction = this.getTypeNameInstruction (name);
		return (instruction != null ? instruction.getName () : name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void merge (IMap map) {
		this.merge (map, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void merge (IMap map, MapInstructionCategory category) {
		this.addInstructions (this.getInstructionMap (category));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset () {
		this.reset (null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset (MapInstructionCategory category) {
		// reset all categories at once
		if (category == null) {
			// reset map completely
			this.instructionMap.clear ();

			// stop further execution
			return;
		}

		// reset a single category
		this.instructionMap.row (category).clear ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save (OutputStream outputStream) throws IOException {
		// create buffers
		OutputStreamWriter streamWriter = null;
		BufferedWriter writer = null;

		// write data
		try {
			// open writers
			streamWriter = new OutputStreamWriter (outputStream);
			writer = new BufferedWriter (streamWriter);

			// get date format
			SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm'Z'");
			format.setTimeZone (TimeZone.getTimeZone ("UTC"));

			// create list
			List<String> elementList = new ArrayList<> ();

			// append elements
			elementList.add ("Umbrella Map");
			elementList.add (null);
			elementList.add ("Generated on:  " + format.format ((new Date ())));
			elementList.add ("Generator: " + (this.getClass ().getCanonicalName ().length () % 2 > 1 ? " " : "") + this.getClass ().getCanonicalName ());
			elementList.add ("Map Version: " + (VERSION != null && VERSION.length () % 2 > 1 ? " " : "") + (VERSION != null ? VERSION : " Development Snapshot"));
			elementList.add (null);

			// get maximum length
			int length = 0;
			for (String element : elementList) length = Math.max (length, (element != null ? element.length () : 0));

			// write header
			for (String element : elementList) {
				writer.write ("// ");

				if (element != null) {
					// calculate spacer
					int spacerWidth = ((length / 2) - (element.length () / 2));

					// write spacer
					for (int i = 0; i < spacerWidth; i++) writer.write (" ");

					// write element
					writer.write (element);

					// write spacer
					for (int i = 0; i < spacerWidth; i++) writer.write (" ");
				} else
					for (int i = 0; i < length; i++) writer.write ("-");

				// write end
				writer.write (" \\\\\n");
			}

			// write elements
			for (Map.Entry<IMapInstruction, IMapInstruction> entry : this.getInstructionMap ().entrySet ()) {
				// write key identifier
				writer.write (this.getInstructionRegistry ().getName (entry.getKey ()));
				writer.write (" ");

				// write key
				writer.write (entry.getKey ().serialize ());
				writer.write (" ");

				// write value identifier
				writer.write (this.getInstructionRegistry ().getName (entry.getValue ()));
				writer.write (" ");

				// write value
				writer.write (entry.getValue ().serialize ());

				// write new line
				writer.write ("\n");
			}

			// write eof comment
			writer.write ("// EOF");
		} finally {
			IOUtility.closeQuietly (writer);
			IOUtility.closeQuietly (streamWriter);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save (File file) throws IOException {
		// define streams
		FileOutputStream outputStream = null;

		// save
		try {
			// open stream
			outputStream = new FileOutputStream (file);

			// write file
			this.save (outputStream);
		} finally {
			IOUtility.closeQuietly (outputStream);
		}
	}
}
