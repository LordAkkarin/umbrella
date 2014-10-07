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
package umbrella.generator.name;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@AllArgsConstructor
public class MapNameGenerator implements INameGenerator {

	/**
	 * The default map generator.
	 */
	public static final MapNameGenerator DEFAULT = new MapNameGenerator ("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz01234567890");

	/**
	 * The unicode map generator.
	 */
	public static final MapNameGenerator UNICODE;

	/**
	 * Defines the character offset.
	 */
	public static final int UNICODE_OFFSET = 32;

	/**
	 * Static Initializer
	 */
	static {
		// create builder
		StringBuilder builder = new StringBuilder ();

		// append elements
		for (int i = UNICODE_OFFSET; i < (UNICODE_OFFSET + 4096); i++) builder.append (((char) i));

		// create map
		UNICODE = new MapNameGenerator (DEFAULT.getTypeMap (), builder.toString ());
	}

	/**
	 * Stores the type map.
	 */
	@Getter
	@Setter
	@NonNull
	public String typeMap;

	/**
	 * Stores the member map.
	 */
	@Getter
	@Setter
	@NonNull
	public String memberMap;

	/**
	 * Generates a new name based on a character map.
	 * @param map The map.
	 * @param previousName The previous name (or null).
	 * @param position The current position.
	 * @return The name.
	 */
	protected String generateName (String map, String previousName, int position) {
		// initialize if needed
		if (previousName == null) return Character.toString (this.typeMap.charAt (0));

		// create builder
		StringBuilder builder = new StringBuilder ();
		builder.append (previousName);

		// increase
		if (builder.charAt (position) != map.charAt ((map.length () - 1))) {
			// update character
			builder.setCharAt (position, (map.charAt ((map.indexOf (builder.charAt (position)) + 1))));

			// skip further execution
			return builder.toString ();
		}

		// wrap around completely
		if (position == 0) {
			// reset characters
			for (int i = 0; i < builder.length (); i++) builder.setCharAt (i, map.charAt (0));

			// append a new character
			builder.append (map.charAt (0));

			// skip further execution
			return builder.toString ();
		}

		// wrap around current position
		builder.setCharAt (position, map.charAt (0));

		// update previous character
		return this.generateName (map, builder.toString (), (position - 1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateFieldName (String previousName) {
		return this.generateName (this.memberMap, previousName, (previousName != null ? (previousName.length () - 1) : -1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateInvokeDynamicMethodName (String previousName) {
		return this.generateName (this.memberMap, previousName, (previousName != null ? (previousName.length () - 1) : -1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateMethodName (String previousName) {
		return this.generateName (this.memberMap, previousName, (previousName != null ? (previousName.length () - 1) : -1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateTypeName (String previousName) {
		return this.generateName (this.typeMap, previousName, (previousName != null ? (previousName.length () - 1) : -1));
	}
}
