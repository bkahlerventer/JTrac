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

import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Space;

/**
 * dashboard page
 */
public class ItemSearchFormPage extends BasePage {        
        
    public ItemSearchFormPage() {
        setVersioned(false);
        Space space = ((JtracSession) getSession()).getCurrentSpace();
        if (space != null) {
            add(new ItemSearchFormPanel(space));
        } else {
            add(new ItemSearchFormPanel(getPrincipal()));
        }
    }       
    
    /**
     * here we are returning to the filter criteria screen from
     * the search results screen
     */
    public ItemSearchFormPage(ItemSearch itemSearch) {
        setVersioned(false);
        itemSearch.setCurrentPage(0);        
        add(new ItemSearchFormPanel(itemSearch));      
    }    

    
}
