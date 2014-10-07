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
package umbrella.asm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.objectweb.asm.commons.Remapper;
import umbrella.map.IMap;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@AllArgsConstructor
public class UmbrellaRemapper extends Remapper {

	/**
	 * Stores the parent map.
	 */
	@Getter
	@Setter
	@NonNull
	private IMap map;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String map (String typeName) {
		return this.map.mapTypeName (super.map (typeName));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String mapFieldName (String owner, String name, String desc) {
		return this.map.mapFieldName (owner, super.mapFieldName (owner, name, desc), desc);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String mapMethodName (String owner, String name, String desc) {
		return this.map.mapMethodName (owner, super.mapMethodName (owner, name, desc), desc);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String mapInvokeDynamicMethodName (String name, String desc) {
		return this.map.mapInvokeDynamicMethodName (super.mapInvokeDynamicMethodName (name, desc), desc);
	}
}
