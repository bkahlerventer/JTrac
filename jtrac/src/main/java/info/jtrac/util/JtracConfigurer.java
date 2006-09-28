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

package info.jtrac.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

/**
 * Custom extension of the Spring PropertyPlaceholderConfigurer that
 * sets up the jtrac.home System property (creates if required) and also creates
 * a default jtrac.properties file for HSQLDB - useful for those who want
 * to quickly evaluate JTrac.  Just dropping the war into a servlet container
 * would work without the need to even configure a datasource.
 * 
 * This class would effectively do nothing if a "jtrac.properties" file exists in jtrac.home
 *
 * 1) a "jtrac.home" property is looked for in /WEB-INF/classes/jtrac-init.properties
 * 2) if not found, then a "jtrac.home" system property is checked for
 * 3) last resort, a ".jtrac" folder is created in the "user.home" and used as "jtrac.home"
 *
 * Note that later on during startup, the HibernateJtracDao would check if 
 * database tables exist, and if they dont, would proceed to create them
 */
public class JtracConfigurer extends PropertyPlaceholderConfigurer {
    
    private final Log logger = LogFactory.getLog(getClass());
    
    public JtracConfigurer() throws Exception {
        String jtracHome = null;
        ClassPathResource cpr = new ClassPathResource("jtrac-init.properties");
        File initPropsFile = cpr.getFile();
        if (initPropsFile.exists()) {
            logger.info("found 'jtrac-init.properties' on classpath, processing...");
            InputStream is = null;
            Properties props = new Properties();
            try {
                is = new FileInputStream(initPropsFile);                
                props.load(is);
            } finally {
                is.close();
            }
            jtracHome = props.getProperty("jtrac.home");
        }
        if (jtracHome != null) {
            logger.info("'jtrac.home' property initialized from 'jtrac-init.properties' as '" + jtracHome + "'");
        } else {
            logger.info("valid 'jtrac.home' property not available in 'jtrac-init.properties', trying system properties...");                
            jtracHome = System.getProperty("jtrac.home");
            if (jtracHome == null) {                        
                jtracHome = System.getProperty("user.home") + "/.jtrac";
                logger.warn("System property 'jtrac.home' does not exist.  Will use 'user.home' directory '" + jtracHome + "'");                
                System.setProperty("jtrac.home", jtracHome);
            } else {
                logger.info("System property 'jtrac.home' exists: '" + jtracHome + "'");
            }
        }
        File homeFile = new File(jtracHome);
        if (!homeFile.exists()) {
            homeFile.mkdir();
            logger.info("directory does not exist, created '" + homeFile.getPath() + "'");
            if (!homeFile.exists()) {
                String message = "invalid path '" + homeFile.getAbsolutePath() + "', try creating this directory first.  Aborting.";
                logger.fatal(message);
                throw new RuntimeException(message);
            }
        } else {
            logger.info("directory already exists: '" + homeFile.getPath() + "'");
        }
        System.setProperty("jtrac.home", homeFile.getAbsolutePath());
        File attachmentsFile = new File(jtracHome + "/attachments");
        if (!attachmentsFile.exists()) {
            attachmentsFile.mkdir();
            logger.info("directory does not exist, created '" + attachmentsFile.getPath() + "'");
        } else {
            logger.info("directory already exists: '" + attachmentsFile.getPath() + "'");
        }
        File indexesFile = new File(jtracHome + "/indexes");
        if (!indexesFile.exists()) {
            indexesFile.mkdir();
            logger.info("directory does not exist, created '" + indexesFile.getPath() + "'");
        } else {
            logger.info("directory already exists: '" + indexesFile.getPath() + "'");
        }
        File propFile = new File(homeFile.getPath() + "/jtrac.properties");
        if (!propFile.exists()) {                
            propFile.createNewFile();
            logger.info("properties file does not exist, created '" + propFile.getPath() + "'");
            OutputStream os = new FileOutputStream(propFile);
            Writer out = new PrintWriter(os);
            try {
                out.write("database.driver=org.hsqldb.jdbcDriver\n");
                out.write("database.url=jdbc:hsqldb:file:${jtrac.home}/db/jtrac\n");
                out.write("database.username=sa\n");
                out.write("database.password=\n");
                out.write("hibernate.dialect=org.hibernate.dialect.HSQLDialect\n");
                out.write("hibernate.show_sql=false\n");
            } finally {
                out.close();
                os.close();
            }
            logger.info("HSQLDB will be used.  Finished creating '" + propFile.getPath() + "'");
        } else {
            logger.info("'jtrac.properties' file exists: '" + propFile.getPath() + "'");
        }            
        setLocation(new FileSystemResource(propFile));
    }
    
}