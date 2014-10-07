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
package umbrella.analyzer;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import umbrella.map.instruction.GenericMethodNameInstruction;
import umbrella.map.instruction.IMethodNameInstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a class report.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class ClassReport {

	/**
	 * Stores the internal logger instance.
	 */
	private static final Logger logger = LogManager.getLogger (ReportVisitor.class);

	/**
	 * Stores a list of known methods.
	 */
	private List<IMethodNameInstruction> knownMethods = new ArrayList<> ();

	/**
	 * Constructs a new ClassReport instance.
	 * @param reader The class reader.
	 */
	public ClassReport (@NonNull ClassReader reader) {
		// construct report visitor
		ReportVisitor reportVisitor = new ReportVisitor ();

		// start mapping
		reader.accept (reportVisitor, ClassReader.SKIP_DEBUG | ClassReader.EXPAND_FRAMES);
	}

	/**
	 * Analyzes class files.
	 */
	private class ReportVisitor extends ClassVisitor {

		/**
		 * Constructs a new ReportVisitor instance.
		 */
		public ReportVisitor () {
			super (Opcodes.ASM5);
		}

		/**
		 * Constructs a new ReportVisitor instance.
		 * @param cv A child class visitor.
		 */
		public ReportVisitor (ClassVisitor cv) {
			super (Opcodes.ASM5, cv);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MethodVisitor visitMethod (int access, String name, String desc, String signature, String[] exceptions) {
			// skip final methods
			if ((access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL) return super.visitMethod (access, name, desc, signature, exceptions);

			// append to list of known methods
			knownMethods.add (new GenericMethodNameInstruction (null, name, desc));

			// call parent
			return super.visitMethod (access, name, desc, signature, exceptions);
		}
	}
}
