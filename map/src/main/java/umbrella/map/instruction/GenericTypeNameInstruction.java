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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import umbrella.map.IMap;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@RequiredArgsConstructor
public class GenericTypeNameInstruction implements ITypeNameMapInstruction {

	/**
	 * Stores the type name.
	 */
	@Getter
	@NonNull
	private final String name;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMapInstruction getInverse (IMap map) {
		return (new GenericTypeNameInstruction (map.mapTypeName (this.name)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String serialize () {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals (Object obj) {
		// check basics
		if (obj == null) return false;
		if (!(obj instanceof ITypeNameMapInstruction)) return false;

		// cast
		ITypeNameMapInstruction instruction = ((ITypeNameMapInstruction) obj);

		// check values
		return (
			((this.name == null && instruction.getName () == null) || this.name.equals (instruction.getName ()))
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode () {
		return (this.name.hashCode ()) + 1000;
	}
}
