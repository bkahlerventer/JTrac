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

import info.jtrac.domain.Item;
import wicket.markup.html.basic.Label;
import wicket.model.PropertyModel;

/**
 * panel for showing the item read-only view
 */
public class ItemViewPanel extends BasePanel {    
    
    public ItemViewPanel(String id, Item item) {
        
        super(id);    
        
        add(new Label("refId", new PropertyModel(item, "refId")));
        add(new Label("status", new PropertyModel(item, "statusValue")));
        add(new Label("loggedBy", new PropertyModel(item, "loggedBy.name")));
        add(new Label("assignedTo", new PropertyModel(item, "assignedTo.name")));
        add(new Label("summary", new PropertyModel(item, "summary")));
        add(new Label("detail", new PropertyModel(item, "detail")));
      
    }
    
}
