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
package umbrella.map.instruction;

import umbrella.map.IMap;

/**
 * Represents a single map instruction.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface IMapInstruction {

	/**
	 * Returns an inversed version of the instruction.
	 * @param map The map.
	 * @return The instruction.
	 * @since 1.0.0
	 */
	public IMapInstruction getInverse (IMap map);

	/**
	 * Returns a serialized version of the instruction.
	 * @return The serialized instruction.
	 * @since 1.0.0
	 */
	public String serialize ();
}
