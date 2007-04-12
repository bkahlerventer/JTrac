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

import info.jtrac.Jtrac;
import info.jtrac.domain.User;
import java.io.Serializable;
import javax.servlet.http.Cookie;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.BoundCompoundPropertyModel;

/**
 * login page
 */
public class LoginPage extends WebPage {              
    
    protected final Log logger = LogFactory.getLog(getClass());    
    
    private Jtrac getJtrac() {
        return ((JtracApplication) getApplication()).getJtrac();
    }        
    
    public LoginPage() {
        setVersioned(false);
        add(new Label("title", getLocalizer().getString("login.title", null)));
        add(new LoginForm("form"));
        add(new Label("version", System.getProperty("jtrac.version")));
    }
    
    private class LoginForm extends Form {                               
        
        private String loginName;
        private String password;
        private boolean rememberMe;

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }        

        public boolean isRememberMe() {
            return rememberMe;
        }

        public void setRememberMe(boolean rememberMe) {
            this.rememberMe = rememberMe;
        }         
        
        public LoginForm(String id) {            
            super(id);
            add(new WebMarkupContainer("hide") {
                @Override
                public boolean isVisible() {
                    return !LoginForm.this.hasError();
                }
            });
            add(new FeedbackPanel("feedback"));            
            setModel(new BoundCompoundPropertyModel(this));
            final TextField loginName = new TextField("loginName");
            loginName.setOutputMarkupId(true);
            add(loginName);
            final PasswordTextField password = new PasswordTextField("password");
            password.setRequired(false);
            password.setOutputMarkupId(true);
            add(password);
            // intelligently set focus on the appropriate textbox
            getBodyContainer().addOnLoadModifier(new AbstractReadOnlyModel() {
                public Object getObject() {
                    String markupId;
                    if(loginName.getConvertedInput() == null) {
                        markupId = loginName.getMarkupId();
                    } else {
                        markupId = password.getMarkupId();
                    }
                    return "document.getElementById('" + markupId + "').focus()";
                }
            }, password);            
            add(new CheckBox("rememberMe"));

        }
                
        @Override
        protected void onSubmit() {                    
            if(loginName == null || password == null) {
                logger.debug("login failed - login name or password is null");
                error(getLocalizer().getString("login.error", null));                
                return;
            }            
            User user = ((JtracApplication) getApplication()).authenticate(loginName, password);         
            if (user == null) { // login failed                
                error(getLocalizer().getString("login.error", null));                   
            } else { // login success
                // remember me cookie
                if(rememberMe) {                    
                    Cookie cookie = new Cookie("jtrac", loginName + ":" + getJtrac().encodeClearText(password));
                    cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days in seconds 
                    getWebRequestCycle().getWebResponse().addCookie(cookie);
                    logger.debug("remember me requested, cookie added: " + cookie.getValue());
                }
                // setup session with principal
                ((JtracSession) getSession()).setUser(user);
                // proceed to bookmarkable page or default dashboard
                if (!continueToOriginalDestination()) {
                    setResponsePage(DashboardPage.class);
                } 
            }                
        }     
                        
    }         
    
}