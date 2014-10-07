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
package umbrella.generator;

import com.google.common.base.Joiner;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.*;
import umbrella.analyzer.Analyzer;
import umbrella.analyzer.ClassReport;
import umbrella.generator.name.INameGenerator;
import umbrella.map.IMap;
import umbrella.map.instruction.GenericFieldNameInstruction;
import umbrella.map.instruction.GenericInvokeDynamicMethodNameInstruction;
import umbrella.map.instruction.GenericMethodNameInstruction;
import umbrella.map.instruction.GenericTypeNameInstruction;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@AllArgsConstructor
public class GenericMapGenerator extends AbstractMapGenerator {

	/**
	 * Stores the internal logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (GenericMapGenerator.class);

	/**
	 * Stores the parent name generator.
	 */
	@Getter
	@Setter
	@NonNull
	private INameGenerator nameGenerator;

	/**
	 * Defines whether package name removal is enabled.
	 */
	@Getter
	@Setter
	private boolean packageNameRemovalEnabled;

	/**
	 * Defines whether field type overloading is enabled.
	 */
	@Getter
	@Setter
	private boolean fieldTypeOverloadingEnabled;

	/**
	 * Defines whether method return type overloading is enabled.
	 */
	@Getter
	@Setter
	private boolean methodReturnTypeOverloadingEnabled;

	/**
	 * Constructs a new GenericMapGenerator instance.
	 * @param generator The generator.
	 */
	public GenericMapGenerator (@NonNull INameGenerator generator) {
		this (generator, true);
	}

	/**
	 * Constructs a new GenericMapGenerator instance.
	 * @param generator The generator.
	 * @param packageNameRemovalEnabled True if packages shall be removed.
	 */
	public GenericMapGenerator (@NonNull INameGenerator generator, boolean packageNameRemovalEnabled) {
		this (generator, packageNameRemovalEnabled, true, true);
	}

	/**
	 * Constructs a new GenericMapGenerator instance.
	 * @param generator The generator.
	 * @param packageNameRemovalEnabled True if packages shall be removed.
	 * @param fieldTypeOverloadingEnabled True if field type overloading shall be used.
	 */
	public GenericMapGenerator (@NonNull INameGenerator generator, boolean packageNameRemovalEnabled, boolean fieldTypeOverloadingEnabled) {
		this (generator, packageNameRemovalEnabled, fieldTypeOverloadingEnabled, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate (@NonNull ClassReader classReader, @NonNull IMap map, Analyzer analyzer) throws Exception {
		// create a default analyzer if needed
		if (analyzer == null) analyzer = new Analyzer ();

		// initialize the class visitor
		GeneratorClassVisitor classVisitor = new GeneratorClassVisitor (map, analyzer);

		// start generation
		classReader.accept (classVisitor, ClassReader.SKIP_DEBUG | ClassReader.EXPAND_FRAMES);
	}

	/**
	 * A visitor.
	 */
	private class GeneratorClassVisitor extends ClassVisitor {

		/**
		 * Stores the parent analyzer instance.
		 */
		private final Analyzer analyzer;

		/**
		 * Stores the parent map instance.
		 */
		private final IMap map;

		/**
		 * Defines the current class name.
		 */
		private String currentClass = null;

		/**
		 * Stores the super name.
		 */
		private String superName = null;

		/**
		 * Stores the interfaces.
		 */
		private String[] interfaces;

		/**
		 * Constructs a new GeneratorClassVisitor instance.
		 * @param map The map.
		 */
		public GeneratorClassVisitor (IMap map, Analyzer analyzer) {
			super (Opcodes.ASM5);

			this.analyzer = analyzer;
			this.map = map;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit (int version, int access, String name, String signature, String superName, String[] interfaces) {
			super.visit (version, access, name, signature, superName, interfaces);

			// trace log
			getLogger ().trace ("Generating mapping for type \"" + name + "\" " + (superName != null ? "(super: " + superName + ") " : "") + (interfaces.length > 0 ? "(interfaces: " + Joiner.on (", ").join (interfaces) + ") " : "") + "...");

			// store current class
			this.currentClass = name;
			this.superName = superName;
			this.interfaces = interfaces;

			// search for existing mappings
			if (this.map.getTypeNameInstruction (name) != null) {
				// log
				getLogger ().trace ("Type \"" + name + "\" has already been mapped to \"" + this.map.mapTypeName (name) + "\". Skipping.");

				// skip further execution
				return;
			}

			// extract prefix
			String prefix = "";
			if (!isPackageNameRemovalEnabled ()) prefix = name.substring ((name.lastIndexOf ('/') + 1));

			// generate a new name
			String replacementName = null;
			GenericTypeNameInstruction instruction;

			do {
				// generate a name
				replacementName = getNameGenerator ().generateTypeName (replacementName);

				// create a mapping
				instruction = new GenericTypeNameInstruction (prefix + replacementName);
			} while (this.map.mappingExists (instruction));

			// add instruction
			getLogger ().trace ("Mapped \"" + name + "\" to \"" + instruction.getName () + "\".");
			this.map.addInstruction (new GenericTypeNameInstruction (name), instruction);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visitInnerClass (String name, String outerName, String innerName, int access) {
			super.visitInnerClass (name, outerName, innerName, access);

			// trace log
			getLogger ().trace ("Generating mapping for type \"" + name + "\" ...");

			// search for existing mappings
			if (this.map.getTypeNameInstruction (name) != null) {
				// log
				getLogger ().trace ("Type \"" + name + "\" has already been mapped to \"" + this.map.mapTypeName (name) + "\". Skipping.");

				// skip further execution
				return;
			}

			// extract prefix
			String prefix = "";
			if (!isPackageNameRemovalEnabled ()) prefix = name.substring ((name.lastIndexOf ('/') + 1));

			// generate a new name
			String replacementName = null;
			GenericTypeNameInstruction instruction;

			do {
				// generate a name
				replacementName = getNameGenerator ().generateTypeName (replacementName);

				// create a mapping
				instruction = new GenericTypeNameInstruction (prefix + replacementName);
			} while (this.map.mappingExists (instruction));

			// add instruction
			getLogger ().trace ("Mapped \"" + name + "\" to \"" + instruction.getName () + "\".");
			this.map.addInstruction (new GenericTypeNameInstruction (name), instruction);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MethodVisitor visitMethod (int access, String name, String desc, String signature, String[] exceptions) {
			// trace log
			getLogger ().trace ("Generating mapping for method \"" + this.currentClass + "#" + name + ":" + desc + "\" ...");

			// skip init methods
			if (name.equals ("<clinit>") || name.equals ("<init>")) {
				// trace log
				getLogger ().trace ("Encountered init method \"" + name + "\". Skipping.");

				// skip further execution
				return super.visitMethod (access, name, desc, signature, exceptions);
			}

			// skip overridden methods
			try {
				if (this.superName != null && this.analyzer.classExists (this.superName)) {
					// get report
					ClassReport report = this.analyzer.getReport (this.superName);

					// check for method signature
					if (report.isKnownMethod (new GenericMethodNameInstruction (null, name, desc))) {
						// trace log
						getLogger ().trace ("Method \"" + this.currentClass + "#" + name + ":" + desc + "\" seems to be overriding a method from \"" + this.superName + "\". Skipping.");

						// skip further execution
						return new GeneratorMethodVisitor (this.map, super.visitMethod (access, name, desc, signature, exceptions));
					}
				}

				for (String parent : this.interfaces) {
					// skip unknown classes
					if (!this.analyzer.classExists (parent)) continue;

					// generate a report
					ClassReport report = this.analyzer.getReport (parent);

					// check for method signature
					if (report.isKnownMethod (new GenericMethodNameInstruction (null, name, desc))) {
						// trace log
						getLogger ().trace ("Method \"" + this.currentClass + "#" + name + ":" + desc + "\" seems to be overriding a method from \"" + parent + "\". Skipping.");

						// skip further execution
						return new GeneratorMethodVisitor (this.map, super.visitMethod (access, name, desc, signature, exceptions));
					}
				}
			} catch (Exception ex) {
				// log
				getLogger ().error ("Could not generate one or more class reports: " + ex.getMessage (), ex);
				getLogger ().error ("The method has been skipped and will not be renamed.");

				// skip further execution
				return new GeneratorMethodVisitor (this.map, super.visitMethod (access, name, desc, signature, exceptions));
			}

			// search for existing mappings
			if (this.map.getMethodNameInstruction (this.currentClass, name, desc) != null) {
				// log
				getLogger ().trace ("Method \"" + this.currentClass + "#" + name + ":" + desc + "\" has already been mapped to \"" + this.map.mapMethodName (this.currentClass, name, desc) + "\". Skipping.");

				// skip further execution
				return new GeneratorMethodVisitor (this.map, super.visitMethod (access, name, desc, signature, exceptions));
			}

			// generate a new name
			String replacementName = null;
			GenericMethodNameInstruction instruction;

			do {
				// generate a name
				replacementName = getNameGenerator ().generateFieldName (replacementName);

				// create a mapping
				instruction = new GenericMethodNameInstruction (this.currentClass, replacementName, (isMethodReturnTypeOverloadingEnabled () ? desc : desc.substring (0, (desc.lastIndexOf (')') + 1))));
			} while (this.map.mappingExists (instruction));

			// add instruction
			getLogger ().trace ("Mapped method \"" + this.currentClass + "#" + name + ":" + desc + "\" to \"" + instruction.getName () + "\".");
			this.map.addInstruction (new GenericMethodNameInstruction (this.currentClass, name, desc), instruction);

			// call parent
			return new GeneratorMethodVisitor (this.map, super.visitMethod (access, name, desc, signature, exceptions));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FieldVisitor visitField (int access, String name, String desc, String signature, Object value) {
			// trace log
			getLogger ().trace ("Generating mapping for field \"" + this.currentClass + "#" + name + ":" + desc + "\" ...");

			// search for existing mappings
			if (this.map.getFieldNameInstruction (this.currentClass, name, desc) != null) {
				// log
				getLogger ().trace ("Field \"" + this.currentClass + "#" + name + ":" + desc + "\" has already been mapped to \"" + this.map.mapMethodName (this.currentClass, name, desc) + "\". Skipping.");

				// skip further execution
				return super.visitField (access, name, desc, signature, value);
			}

			// generate a new name
			String replacementName = null;
			GenericFieldNameInstruction instruction;

			do {
				// generate a name
				replacementName = getNameGenerator ().generateMethodName (replacementName);

				// create a mapping
				instruction = new GenericFieldNameInstruction (this.currentClass, replacementName, (isFieldTypeOverloadingEnabled () ? desc : null));
			} while (this.map.mappingExists (instruction));

			// add instruction
			getLogger ().trace ("Mapped field \"" + this.currentClass + "#" + name + ":" + desc + "\" to \"" + instruction.getName () + "\".");
			this.map.addInstruction (new GenericFieldNameInstruction (this.currentClass, name, desc), instruction);

			// call parent
			return super.visitField (access, name, desc, signature, value);
		}
	}

	/**
	 * Provides a method visitor.
	 */
	private class GeneratorMethodVisitor extends MethodVisitor {

		/**
		 * Stores the parent map instance.
		 */
		private final IMap map;

		/**
		 * Constructs a new GeneratorMethodVisitor instance.
		 * @param mv The method visitor.
		 */
		public GeneratorMethodVisitor (IMap map, MethodVisitor mv) {
			super (Opcodes.ASM5, mv);
			this.map = map;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visitInvokeDynamicInsn (String name, String desc, Handle bsm, Object... bsmArgs) {
			super.visitInvokeDynamicInsn (name, desc, bsm, bsmArgs);

			// trace log
			getLogger ().trace ("Generating mapping for invoke dynamic \"" + name + ":" + desc + "\" ...");

			// search for existing mapping
			if (this.map.getInvokeDynamicMethodNameInstruction (name, desc) != null) {
				// trace log
				getLogger ().trace ("Invoke dynamic \"" + name + ":" + desc + "\" has already been mapped to \"" + this.map.mapInvokeDynamicMethodName (name, desc) + "\". Skipping.");

				// skip further execution
				return;
			}

			// generate a new name
			String replacementName = null;
			GenericInvokeDynamicMethodNameInstruction instruction;

			do {
				// generate a name
				replacementName = getNameGenerator ().generateMethodName (replacementName);

				// create a mapping
				instruction = new GenericInvokeDynamicMethodNameInstruction (replacementName, desc);
			} while (this.map.mappingExists (instruction));

			// add instruction
			getLogger ().trace ("Mapped invoke dynamic \"" + name + ":" + desc + "\" to \"" + instruction.getName () + "\".");
			this.map.addInstruction (new GenericInvokeDynamicMethodNameInstruction (name, desc), instruction);
		}
	}
}
