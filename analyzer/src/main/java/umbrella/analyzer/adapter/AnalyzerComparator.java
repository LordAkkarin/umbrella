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

import java.util.Comparator;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class AnalyzerComparator implements Comparator<IAnalyzerAdapter> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare (IAnalyzerAdapter o1, IAnalyzerAdapter o2) {
		return Math.max (-1, Math.min (1, (o1.getPriority ().numeric - o2.getPriority ().numeric)));
	}
}
