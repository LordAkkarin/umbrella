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

import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import umbrella.map.instruction.*;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public enum MapInstructionCategory {
	FIELD_NAME (IFieldNameInstruction.class),
	INVOKE_DYNAMIC_METHOD_NAME (IInvokeDynamicMethodNameMapInstruction.class),
	METHOD_NAME (IMethodNameInstruction.class),
	TYPE_NAME (ITypeNameMapInstruction.class),
	UNKNOWN (null);

	/**
	 * Stores a map of all instruction categories.
	 */
	private static final ImmutableMap<Class<? extends IMapInstruction>, MapInstructionCategory> map;

	/**
	 * Stores the instruction type.
	 */
	private final Class<? extends IMapInstruction> instructionType;

	/**
	 * Static Initializer
	 */
	static {
		// create builder
		ImmutableMap.Builder<Class<? extends IMapInstruction>, MapInstructionCategory> builder = new ImmutableMap.Builder<> ();

		// append all known elements
		for (MapInstructionCategory category : values ())
			if (category.instructionType != null) builder.put (category.instructionType, category);

		// build map
		map = builder.build ();
	}

	/**
	 * Constructs a new MapInstructionCategory instance.
	 * @param instructionType The instruction type.
	 */
	private MapInstructionCategory (Class<? extends IMapInstruction> instructionType) {
		this.instructionType = instructionType;
	}

	/**
	 * Searches for a category based on an instruction type.
	 * @param instructionType The instruction type.
	 * @return The category.
	 */
	public static MapInstructionCategory valueOf (Class<? extends IMapInstruction> instructionType) {
		// check for obvious elements
		if (map.containsKey (instructionType)) return map.get (instructionType);

		// check super interfaces
		if (instructionType.getInterfaces ().length > 0) {
			// iterate over interfaces
			for (Class<?> type : instructionType.getInterfaces ()) {
				// check whether type is assignable
				if (!IMapInstruction.class.isAssignableFrom (type)) continue;

				// search category
				MapInstructionCategory category = valueOf (type.asSubclass (IMapInstruction.class));

				// verify category type (keep trying if unknown)
				if (category != UNKNOWN) return category;
			}
		}

		// unknown category
		return UNKNOWN;
	}

	/**
	 * Searches for a category based on an instruction.
	 * @param instruction The instruction.
	 * @return The category.
	 */
	public static MapInstructionCategory valueOf (@NonNull IMapInstruction instruction) {
		return valueOf (instruction.getClass ());
	}
}
