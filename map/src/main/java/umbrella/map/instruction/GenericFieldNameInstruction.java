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
public class GenericFieldNameInstruction implements IFieldNameInstruction {

	/**
	 * Stores the field owner.
	 */
	@Getter
	private final String owner;

	/**
	 * Stores the field name.
	 */
	@Getter
	@NonNull
	private final String name;

	/**
	 * Stores the field description.
	 */
	@Getter
	private final String description;

	/**
	 * Constructs a new GenericFieldNameInstruction instance.
	 * @param serialized The serialized instruction.
	 */
	public GenericFieldNameInstruction (String serialized) {
		int ownerEnd = serialized.indexOf ('#');
		int nameEnd = serialized.indexOf (':');

		// split
		this.owner = serialized.substring (0, ownerEnd);
		this.name = serialized.substring ((ownerEnd + 1), nameEnd);
		this.description = serialized.substring ((nameEnd + 1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMapInstruction getInverse (IMap map) {
		return (new GenericFieldNameInstruction (map.mapTypeName (owner), map.mapFieldName (this.owner, this.name, this.description), map.mapDescription (this.description)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String serialize () {
		return this.owner + "#" + this.name + ":" + this.description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals (Object obj) {
		// check basics
		if (obj == null) return false;
		if (!(obj instanceof IFieldNameInstruction)) return false;

		// cast
		IFieldNameInstruction instruction = ((IFieldNameInstruction) obj);

		// check values
		return (
			(this.owner == null || instruction.getOwner () == null || this.owner.equals (instruction.getOwner ())) &&
			((this.name == null && instruction.getName () == null) || this.name.equals (instruction.getName ())) &&
			(this.description == null || instruction.getDescription () == null || this.description.equals (instruction.getDescription ()))
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode () {
		return ((this.owner != null ? this.owner.hashCode () : 0) + (this.name != null ? this.name.hashCode () : 0) + (this.description != null ? this.description.hashCode () : 0) + 1000);
	}
}
