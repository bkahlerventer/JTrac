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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @goal nbjavase
 * @requiresDependencyResolution runtime 
 */
public class NbJavaSeMojo extends AntPropsMojo {
	
	private StringBuffer getFileReferences() {
		StringBuffer sb = new StringBuffer();
		sb.append("m2.repo=" + buildProperties.get("m2.repo") + "\n\n");
		Set fileReferences = new TreeSet();		
		for (Iterator i = testClassPaths.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry entry = (Map.Entry) i.next();			
			String key = (String) entry.getKey();
			if(!key.equals("m2.repo")) {
				continue;
			}
			Set paths = (Set) entry.getValue();
			for (Iterator j = paths.iterator(); j.hasNext(); ) {
				String path = (String) j.next();				
				String fileReference = path.substring(path.lastIndexOf('/') + 1);
				sb.append("file.reference." + fileReference + "=${" + key + "}/" + path + "\n");
				fileReferences.add(fileReference);
			}
		}
		sb.append("\n");
		//===============================================================
		sb.append("javac.classpath=");
		for (Iterator i = fileReferences.iterator(); i.hasNext(); ) {
			String fileReference = (String) i.next();
			sb.append("\\\n    ${file.reference." + fileReference + "}:");
		}
		sb.append("\n");
		return sb;
	}
	
	protected void generate() throws Exception {
		File nbProjDir = new File("nbproject");
		if(!nbProjDir.exists()) {
			nbProjDir.mkdir();
		}
		File nbProjPropsFile = new File("nbproject/project.properties");
		if(!nbProjPropsFile.exists()) {
			OutputStream os = new FileOutputStream("nbproject/project.properties");
			Writer out = new PrintWriter(os);
			Date date = new Date();
			out.write("# *** generated by the AntProps Maven2 plugin: " + date + " ***\n\n");	
			out.write("javac.source=1.5\n");
			out.write("javac.target=1.5\n");
			out.write("build.dir=target\n");
			out.write("build.classes.dir=${build.dir}/classes\n");
			out.write("build.test.classes.dir=${build.dir}/test-classes\n");
			out.write("javac.test.classpath=${javac.classpath}:${build.classes.dir}\n");
			out.write("source.encoding=UTF-8\n");
			out.write("src.dir=src/main/java\n");
			out.write("test.src.dir=src/test/java\n");
			out.write("run.classpath=${javac.classpath}:${build.classes.dir}\n");
			out.write("run.test.classpath=${javac.test.classpath}:${build.test.classes.dir}\n");
			out.write("\n");	
			out.write(getFileReferences() + "\n");
			out.close();
			os.close();			
			getLog().info("created file 'nbproject/project.properties'");
			String projectSource = FileUtils.readFile(getClass(), "project-javase.xml").toString();
			String projectName = project.getArtifactId();			
			String projectTarget = projectSource.replace("@@project.name@@", projectName);
			FileUtils.writeFile(projectTarget, "nbproject/project.xml", false);
			getLog().info("created file 'nbproject/project.xml'");
		} else {
			getLog().info("'nbproject/project.xml' already exists, modifying contents");
			String contents = FileUtils.readFile("nbproject/project.properties").toString();
			contents = contents.replaceAll("\\njavac\\.classpath.*(\\n\\s+.*)*", "");
			contents = contents.replaceAll("\\nfile\\.reference.*", "");
			contents = contents.replaceAll("\\nm2\\.repo.*", "");
			contents = contents.replaceAll("\\n+", "\n");
			contents = contents + getFileReferences();
			FileUtils.writeFile(contents, "nbproject/project.properties", false);
		}
	}
	
}
