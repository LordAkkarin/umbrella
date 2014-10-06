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
	@NonNull
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
	@NonNull
	private final String description;

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
}
