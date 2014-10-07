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
package umbrella.map.instruction.utility;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.NonNull;
import umbrella.map.instruction.IMapInstruction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Provides a generic instruction registry implementation.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class GenericMapInstructionRegistry implements IMapInstructionRegistry {

	/**
	 * Stores the registry.
	 */
	private final BiMap<String, Class<? extends IMapInstruction>> registry = HashBiMap.create ();

	/**
	 * Constructs a new GenericMapInstructionRegistry instance.
	 */
	public GenericMapInstructionRegistry () {
		super ();
		this.registerDefaultInstructions ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMapInstruction getElement (@NonNull String name, @NonNull String instruction) {
		// get class
		Class<? extends IMapInstruction> instructionType = this.getElementType (name);

		// verify
		Preconditions.checkState (instructionType != null, "Instruction \"" + name + "\" could not be found");

		// initialize new instance
		try {
			// find constructor
			Constructor<? extends IMapInstruction> constructor = instructionType.getConstructor (String.class);

			// create instance
			return constructor.newInstance (instruction);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
			throw new IllegalStateException ("Could not initialize instruction with name \"" + name + "\": " + ex.getMessage (), ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends IMapInstruction> getElementType (String name) {
		return this.registry.get (name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName (Class<? extends IMapInstruction> type) {
		return this.registry.inverse ().get (type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName (IMapInstruction instruction) {
		return this.getName (instruction.getClass ());
	}

	/**
	 * Registers a new instruction.
	 * @param name The instruction name.
	 * @param instructionType The instruction type.
	 */
	public void registerInstruction (@NonNull String name, @NonNull Class<? extends IMapInstruction> instructionType) {
		this.registry.put (name, instructionType);
	}

	/**
	 * Registers all default instruction types.
	 */
	protected void registerDefaultInstructions () { }
}
