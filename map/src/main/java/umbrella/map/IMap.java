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

import umbrella.map.instruction.IMapInstruction;
import umbrella.map.instruction.utility.IMapInstructionRegistry;
import umbrella.map.instruction.utility.MapInstructionCategory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Represents a map.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface IMap {

	/**
	 * Returns a list of all instructions within the map.
	 * @return The instruction list.
	 */
	public List<IMapInstruction> getInstructionList ();

	/**
	 * Returns a list of instructions within a specific category.
	 * @param category The category.
	 * @return The instruction list.
	 */
	public List<IMapInstruction> getInstructionList (MapInstructionCategory category);

	/**
	 * Returns a list of instructions of a specific type.
	 * @param instructionType The instruction type.
	 * @param deep True if a deep search (checking all categories) shall be performed.
	 * @param <T> The type.
	 * @return The instruction list.
	 */
	public <T extends IMapInstruction> List<T> getInstructionList (Class<T> instructionType, boolean deep);

	/**
	 * Returns a list of instructions of a specific type (non-deep search).
	 * @param instructionType The instruction type.
	 * @param <T> The type.
	 * @return The instruction list.
	 */
	public <T extends IMapInstruction> List<T> getInstructionList (Class<T> instructionType);

	/**
	 * Returns the active instruction registry.
	 * @return The instruction registry.
	 */
	public IMapInstructionRegistry getInstructionRegistry ();

	/**
	 * Maps a field name.
	 * @param owner The owner type.
	 * @param name The original name.
	 * @param description The description.
	 * @return The new name.
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
	 */
	public String mapMethodName (String owner, String name, String description);

	/**
	 * Maps a type.
	 * @param name The original name.
	 * @return The new name.
	 */
	public String mapTypeName (String name);

	/**
	 * Merges all instruction of another map into the map.
	 * @param map The map.
	 */
	public void merge (IMap map);

	/**
	 * Merges all instructions within a specific category into the map.
	 * @param map The map.
	 * @param category The category.
	 */
	public void merge (IMap map, MapInstructionCategory category);

	/**
	 * Saves a map.
	 * @param outputStream The output stream.
	 * @throws IOException Occurs if writing the map is not possible.
	 */
	public void save (OutputStream outputStream) throws IOException;

	/**
	 * Saves a map to a file.
	 * @param file The output file.
	 * @throws IOException Occurs if writing to the file is not possible.
	 */
	public void save (File file) throws IOException;
}
