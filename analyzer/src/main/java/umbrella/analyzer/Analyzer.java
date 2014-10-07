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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import umbrella.analyzer.adapter.AnalyzerComparator;
import umbrella.analyzer.adapter.IAnalyzerAdapter;
import umbrella.analyzer.adapter.JavaAnalyzerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides a simple system for analyzing classpath dependencies.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class Analyzer {

	/**
	 * Stores the internal logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (Analyzer.class);

	/**
	 * Stores a list of classpath elements.
	 */
	private final List<IAnalyzerAdapter> adapterList = new ArrayList<> ();

	/**
	 * Constructs a new Analyzer instance.
	 */
	public Analyzer () {
		super ();

		try {
			this.addAdapter (new JavaAnalyzerAdapter (new File (System.getProperty ("java.home"))));
		} catch (IOException ex) {
			getLogger ().error ("Could not load Java standard library", ex);
		}
	}

	/**
	 * Adds a new analyzer adapter.
	 * @param adapter The adapter.
	 */
	public void addAdapter (@NonNull IAnalyzerAdapter adapter) {
		// add element
		this.adapterList.add (adapter);

		// sort list
		Collections.sort (this.adapterList, new AnalyzerComparator ());
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
