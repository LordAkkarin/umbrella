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
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.*;
import umbrella.map.instruction.*;
import umbrella.map.instruction.utility.IMapInstructionRegistry;
import umbrella.map.instruction.utility.MapInstructionCategory;
import umbrella.utility.IOUtility;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class GenericMap implements IMap {

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
	 * Adds a new instruction.
	 * @param original The original.
	 * @param replacement The replacement.
	 * @since 1.0.0
	 */
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
