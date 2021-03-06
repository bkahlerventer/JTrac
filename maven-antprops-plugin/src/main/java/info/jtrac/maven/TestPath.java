/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.jtrac.maven;

import java.util.List;

/**
 * Represents an additional test classpath section in the properties file
 * this is just a data holder object that is initialized by Maven2
 * and used by the AntPropsMojo
 */
public class TestPath {
	
	String baseDirProperty;
	private List paths;
	
	public String getBaseDirProperty() {
		return baseDirProperty;
	}
	
	public List getPaths() {
		return paths;
	}

}
