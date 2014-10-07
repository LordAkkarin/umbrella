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

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class Test implements Comparable<Test> {
	public static final int variable1 = 42;
	public static final String variable2 = "Test";
	public static int variable3 = 42;
	public static String variable4 = "Test";
	public int variable5 = 42;
	public String variable6 = "Test";
	public InnerInterface1 variable7 = null;

	public static void method1 () { }
	public static int method2 () { return 42; }
	public static String method3 () { return "Test"; }
	public void method4 () { }
	public int method5 () { return 42; }
	public String method6 () { return "Test"; }

	@Override
	public int compareTo (Test o) { return 0; }
	@Override
	public boolean equals (Object obj) { return super.equals (obj); }

	public static interface InnerInterface1 { }
	public interface InnerInterface2 extends InnerInterface1 { }

	public static class InnerClass1 implements InnerInterface1 { }
	public class InnerClass2 implements InnerInterface2 { }
}
