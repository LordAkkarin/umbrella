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

import lombok.NonNull;
import umbrella.analyzer.ClassReport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class DelegatedAnalyzerAdapter extends AbstractAnalyzerAdapter {

	/**
	 * Stores a list of child adapters.
	 */
	private List<IAnalyzerAdapter> adapterList = new ArrayList<> ();

	/**
	 * Constructs a new DelegatedAnalyzerAdapter instance.
	 * @param analyzerAdapters A list of adapters.
	 * @param priority The priority.
	 */
	public DelegatedAnalyzerAdapter (@NonNull Collection<IAnalyzerAdapter> analyzerAdapters, @NonNull Priority priority) {
		super (priority);
		this.adapterList.addAll (analyzerAdapters);
	}

	/**
	 * Constructs a new DelegatedAnalyzerAdapter instance.
	 * @param analyzerAdapters The list of adapters.
	 */
	public DelegatedAnalyzerAdapter (@NonNull Collection<IAnalyzerAdapter> analyzerAdapters) {
		this (analyzerAdapters, Priority.NORMAL);
	}

	/**
	 * Constructs a new DelegatedAnalyzerAdapter instance.
	 * @param priority The priority.
	 */
	public DelegatedAnalyzerAdapter (@NonNull Priority priority) {
		super (priority);
	}

	/**
	 * Constructs a new DelegatedAnalyzerAdapter instance.
	 */
	public DelegatedAnalyzerAdapter () {
		this (Priority.NORMAL);
	}

	/**
	 * Adds a new analyzer to the list.
	 * @param adapter The adapter.
	 */
	public void addAnalyzer (@NonNull IAnalyzerAdapter adapter) {
		this.adapterList.add (adapter);

		// sort list
		Collections.sort (this.adapterList, new AnalyzerComparator ());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean classExists (String classPath) {
		for (IAnalyzerAdapter adapter : this.adapterList) if (adapter.classExists (classPath)) return true;
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClassReport getReport (String classPath) throws Exception {
		// log
		getLogger ().trace ("Generating report for class \"" + classPath + "\" within adapter " + this.getClass ().getName () + ".");

		// generate report
		for (IAnalyzerAdapter adapter : this.adapterList) if (adapter.classExists (classPath)) return adapter.getReport (classPath);
		return null;
	}
}
