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

import info.jtrac.Version;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;

/**
 * logout page.  the session invalidation code is in HeaderPanel
 */
public class LogoutPage extends WebPage {              
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    public LogoutPage() {
        add(new Label("title", getLocalizer().getString("logout.title", null)));
        add(new Link("home") {
            public void onClick() {
                setResponsePage(DashboardPage.class);
            }
        });
        add(new Link("login") {
            public void onClick() {
                setResponsePage(LoginPage.class);
            }
        });
        add(new Label("version", Version.VERSION));               
    }    
    
}