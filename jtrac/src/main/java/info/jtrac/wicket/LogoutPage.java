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

package info.jtrac.wicket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

/**
 * logout page.  the session invalidation code is in HeaderPanel
 */
public class LogoutPage extends WebPage {              
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    public LogoutPage() {
        setVersioned(false);
        add(new Label("title", getLocalizer().getString("logout.title", null)));
        add(new Label("version", System.getProperty("jtrac.version")));               
    }    
    
}