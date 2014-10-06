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

import umbrella.map.instruction.*;

/**
 * Ensures that maps are built upon well known names.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface IMapInstructionRegistry {

	/**
	 * Defines the default instruction registry.
	 */
	public static final IMapInstructionRegistry DEFAULT = new GenericMapInstructionRegistry () {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void registerDefaultInstructions () {
			super.registerDefaultInstructions ();

			this.registerInstruction ("FLD", GenericFieldNameInstruction.class);
			this.registerInstruction ("IDM", GenericInvokeDynamicMethodNameInstruction.class);
			this.registerInstruction ("MTD", GenericMethodNameInstruction.class);
			this.registerInstruction ("TYP", GenericTypeNameInstruction.class);
		}
	};

	/**
	 * Returns an instruction.
	 * @param name The type name.
	 * @param instruction The instruction.
	 * @return The instruction.
	 * @since 1.0.0
	 */
	public IMapInstruction getElement (String name, String instruction);

	/**
	 * Returns an instruction type.
	 * @param name The type name.
	 * @return The instruction type.
	 * @since 1.0.0
	 */
	public Class<? extends IMapInstruction> getElementType (String name);

	/**
	 * Returns an instruction name.
	 * @param type The instruction type.
	 * @return The instruction name.
	 * @since 1.0.0
	 */
	public String getName (Class<? extends IMapInstruction> type);

	/**
	 * Returns an instruction name.
	 * @param instruction The instruction.
	 * @return The instruction name.
	 * @since 1.0.0
	 */
	public String getName (IMapInstruction instruction);
}
