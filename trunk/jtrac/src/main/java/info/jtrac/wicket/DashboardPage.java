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

import info.jtrac.domain.Counts;
import info.jtrac.domain.CountsHolder;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import java.util.Collections;
import java.util.List;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.PropertyModel;

/**
 * dashboard page
 */
public class DashboardPage extends BasePage {
    
    public DashboardPage() {
        
        setVersioned(false);                       
        
        final User user = getPrincipal();        
        List<UserSpaceRole> spaceRoles = user.getSpaceRoles();        
        
        final CountsHolder countsHolder = getJtrac().loadCountsForUser(user);        
        
        WebMarkupContainer hideLogged = new WebMarkupContainer("hideLogged");
        WebMarkupContainer hideAssigned = new WebMarkupContainer("hideAssigned");
        if(user.getId() == 0) {
            hideLogged.setVisible(false);
            hideAssigned.setVisible(false);
        }
        add(hideLogged);
        add(hideAssigned);
        
        add(new ListView("dashboardRows", spaceRoles) {
            protected void populateItem(final ListItem listItem) {
                UserSpaceRole usr = (UserSpaceRole) listItem.getModelObject();
                Counts counts = countsHolder.getCounts().get(usr.getSpace().getId());
                if (counts == null) {
                    counts = new Counts(false); // this can happen if fresh space
                }
                DashboardRowPanel dashboardRow = new DashboardRowPanel("dashboardRow", usr, counts);
                listItem.add(dashboardRow);
            }
        });
        
        WebMarkupContainer total = new WebMarkupContainer("total");
        
        if(spaceRoles.size() > 1) {
            
            total.add(new Link("search") {
                public void onClick() {
                    setResponsePage(ItemSearchFormPage.class);
                }
            });
            
            if(user.getId() > 0) {            
                total.add(new Link("loggedByMe") {
                    public void onClick() {
                        ItemSearch itemSearch = new ItemSearch(user);
                        itemSearch.setLoggedByList(Collections.singletonList(user));
                        setResponsePage(new ItemListPage(itemSearch));
                    }
                }.add(new Label("loggedByMe", new PropertyModel(countsHolder, "totalLoggedByMe"))));

                total.add(new Link("assignedToMe") {
                    public void onClick() {
                        ItemSearch itemSearch = new ItemSearch(user);
                        itemSearch.setAssignedToList(Collections.singletonList(user));
                        setResponsePage(new ItemListPage(itemSearch));
                    }
                }.add(new Label("assignedToMe", new PropertyModel(countsHolder, "totalAssignedToMe"))));
            } else {
                total.add(new WebMarkupContainer("loggedByMe").setVisible(false));
                total.add(new WebMarkupContainer("assignedToMe").setVisible(false));
            }
            
            total.add(new Link("total") {
                public void onClick() {
                    ItemSearch itemSearch = new ItemSearch(user);                    
                    setResponsePage(new ItemListPage(itemSearch));
                }
            }.add(new Label("total", new PropertyModel(countsHolder, "totalTotal"))));
                       
        } else {             
            total.setVisible(false);
        }   
        
        add(total); 
        
    }
    
}