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
public class GenericMethodNameInstruction implements IMethodNameInstruction {

	/**
	 * Stores the owner type.
	 */
	@Getter
	@NonNull
	public final String owner;

	/**
	 * Stores the method name.
	 */
	@Getter
	@NonNull
	public final String name;

	/**
	 * Stores the method description.
	 */
	@Getter
	@NonNull
	public final String description;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMapInstruction getInverse (IMap map) {
		return (new GenericMethodNameInstruction (map.mapTypeName (this.owner), map.mapMethodName (this.owner, this.name, this.description), map.mapDescription (this.description)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String serialize () {
		return this.owner + "#" + this.name + ":" + this.description;
	}
}
