package info.jtrac.wicket;

import info.jtrac.domain.Counts;
import info.jtrac.domain.CountsHolder;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.util.SecurityUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;

public class DashboardPage extends BasePage {
      
    public DashboardPage() {
        
        super("Dashboard");
        
        final User user = SecurityUtils.getPrincipal();
        CountsHolder countsHolder = getJtrac().loadCountsForUser(user);        
        List<Counts> countsList = new ArrayList<Counts>(countsHolder.getCounts().size());        
        for(Map.Entry<Long, Counts> entry : countsHolder.getCounts().entrySet()) {
            countsList.add(entry.getValue());
        }               
        
        border.add(new ListView("dashboard", countsList) {
            protected void populateItem(final ListItem listItem) {
                Counts counts = (Counts) listItem.getModelObject();
                DashboardRowPanel a = new DashboardRowPanel("dashboardRow", counts);
                Space space = getJtrac().loadSpace(1);
                Counts detailed = getJtrac().loadCountsForUserSpace(user, space);
                DashboardRowExpandedPanel b = new DashboardRowExpandedPanel("dashboardRowExpanded", detailed, space);
                listItem.add(a);
                listItem.add(b);
            }
        });        
    }
    
}
