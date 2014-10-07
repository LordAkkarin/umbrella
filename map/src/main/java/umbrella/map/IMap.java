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

import umbrella.map.instruction.*;
import umbrella.map.instruction.utility.IMapInstructionRegistry;
import umbrella.map.instruction.utility.MapInstructionCategory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Represents a map.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface IMap {

	/**
	 * Returns a field instruction (or null).
	 * @param owner The owner type.
	 * @param name The original name.
	 * @param description The description.
	 * @return The instruction.
	 * @since 1.0.0
	 */
	public IFieldNameInstruction getFieldNameInstruction (String owner, String name, String description);

	/**
	 * Returns an invoke dynamic method name instruction (or null).
	 * @param name The original name.
	 * @param description The description.
	 * @return The instruction.
	 * @since 1.0.0
	 */
	public IInvokeDynamicMethodNameMapInstruction getInvokeDynamicMethodNameInstruction (String name, String description);

	/**
	 * Returns a method name instruction (or null).
	 * @param owner The owner type.
	 * @param name The original name.
	 * @param description The description.
	 * @return The instruction.
	 * @since 1.0.0
	 */
	public IMethodNameInstruction getMethodNameInstruction (String owner, String name, String description);

	/**
	 * Returns a type name instruction (or null).
	 * @param name The original name.
	 * @return The instruction.
	 * @since 1.0.0
	 */
	public ITypeNameMapInstruction getTypeNameInstruction (String name);

	/**
	 * Returns a map of all instructions within the map.
	 * @return The instruction map.
	 * @since 1.0.0
	 */
	public Map<IMapInstruction, IMapInstruction> getInstructionMap ();

	/**
	 * Returns a map of all instructions within a specific category.
	 * @param category The category.
	 * @return The instruction map.
	 * @since 1.0.0
	 */
	public Map<IMapInstruction, IMapInstruction> getInstructionMap (MapInstructionCategory category);

	/**
	 * Returns a map of instructions of a specific type.
	 * @param instructionType The instruction type.
	 * @param deep True if a deep search (checking all categories) shall be performed.
	 * @param <T> The instruction type.
	 * @return The instruction map.
	 * @since 1.0.0
	 */
	public <T extends IMapInstruction> Map<T, IMapInstruction> getInstructionMap (Class<T> instructionType, boolean deep);

	/**
	 * Returns a map of instructions of a specific type.
	 * @param instructionType The instruction type.
	 * @param <T> The instruction type.
	 * @return The instruction map.
	 * @since 1.0.0
	 */
	public <T extends IMapInstruction> Map<T, IMapInstruction> getInstructionMap (Class<T> instructionType);

	/**
	 * Returns the active instruction registry.
	 * @return The instruction registry.
	 * @since 1.0.0
	 */
	public IMapInstructionRegistry getInstructionRegistry ();

	/**
	 * Maps a description.
	 * @param description The description.
	 * @return The mapped description.
	 * @since 1.0.0
	 */
	public String mapDescription (String description);

	/**
	 * Checks whether a specific mapping result exists.
	 * @param instruction The instruction.
	 * @return True if the mapping exists.
	 */
	public boolean mappingExists (IMapInstruction instruction);

	/**
	 * Maps a field name.
	 * @param owner The owner type.
	 * @param name The original name.
	 * @param description The description.
	 * @return The new name.
	 * @since 1.0.0
	 */
	public String mapFieldName (String owner, String name, String description);

	/**
	 * Maps an invoke dynamic method name.
	 * @param name The original name.
	 * @param description The description.
	 * @return The new name.
	 */
	public String mapInvokeDynamicMethodName (String name, String description);

	/**
	 * Maps a method name.
	 * @param owner The owner type.
	 * @param name The original name.
	 * @param description The description.
	 * @return The new name.
	 * @since 1.0.0
	 */
	public String mapMethodName (String owner, String name, String description);

	/**
	 * Maps a type.
	 * @param name The original name.
	 * @return The new name.
	 * @since 1.0.0
	 */
	public String mapTypeName (String name);

	/**
	 * Merges all instruction of another map into the map.
	 * @param map The map.
	 * @since 1.0.0
	 */
	public void merge (IMap map);

	/**
	 * Merges all instructions within a specific category into the map.
	 * @param map The map.
	 * @param category The category.
	 * @since 1.0.0
	 */
	public void merge (IMap map, MapInstructionCategory category);

	/**
	 * Deletes all instructions.
	 * @since 1.0.0
	 */
	public void reset ();

	/**
	 * Removes all instructions of a specific category.
	 * @param category The category.
	 * @since 1.0.0
	 */
	public void reset (MapInstructionCategory category);

	/**
	 * Saves a map.
	 * @param outputStream The output stream.
	 * @throws IOException Occurs if writing the map is not possible.
	 * @since 1.0.0
	 */
	public void save (OutputStream outputStream) throws IOException;

	/**
	 * Saves a map to a file.
	 * @param file The output file.
	 * @throws IOException Occurs if writing to the file is not possible.
	 * @since 1.0.0
	 */
	public void save (File file) throws IOException;
}
