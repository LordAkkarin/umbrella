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
import umbrella.analyzer.adapter.IAnalyzerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a simple system for analyzing classpath dependencies.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class Analyzer {

	/**
	 * Stores a list of classpath elements.
	 */
	private final List<IAnalyzerAdapter> adapterList = new ArrayList<> ();

	/**
	 * Adds a new analyzer adapter.
	 * @param adapter The adapter.
	 */
	public void addAdapter (@NonNull IAnalyzerAdapter adapter) {
		this.adapterList.add (adapter);
	}

	/**
	 * Checks whether a class exists within the class path.
	 * @param classPath The class path.
	 * @return True if the class exists.
	 */
	public boolean classExists (@NonNull String classPath) {
		for (IAnalyzerAdapter adapter : this.adapterList) {
			if (adapter.classExists (classPath)) return true;
		}

		return false;
	}

	/**
	 * Generates a report.
	 * @param classPath The class path.
	 * @return The report.
	 * @throws Exception Occurs if generating a report is not possible.
	 */
	public ClassReport getReport (@NonNull String classPath) throws Exception {
		for (IAnalyzerAdapter adapter : this.adapterList) {
			if (adapter.classExists (classPath)) return adapter.getReport (classPath);
		}

		return null;
	}

	/**
	 * Resets the analyzer.
	 */
	public void reset () {
		this.adapterList.clear ();
	}
}
