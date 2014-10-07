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
package umbrella.analyzer.adapter;

import org.objectweb.asm.ClassReader;
import umbrella.analyzer.ClassReport;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface IAnalyzerAdapter {

	/**
	 * Checks whether the specified class exists within this adapter.
	 * @param classPath The class path.
	 * @return True if the class exists.
	 */
	public boolean classExists (String classPath);

	/**
	 * Generates a report for the specified class.
	 * @param classPath The class path.
	 * @return The report.
	 * @throws Exception Occurs if reading the class file is not possible.
	 */
	public ClassReport getReport (String classPath) throws Exception;
}
