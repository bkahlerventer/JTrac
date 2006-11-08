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

import info.jtrac.domain.Attachment;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemItem;
import info.jtrac.webflow.ItemViewFormAction.ItemViewForm;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.util.HtmlUtils;

/**
 * Utilities to convert an Item into HTML etc.
 * The getAsHtml() routine is used to diplay an item - within a tag lib for JSP
 * And we are able to re-use this to send HTML e-mail etc.
 */
public final class ItemUtils {    
    
    private static String fixWhiteSpace(String text) {
        if (text == null) {
            return "";
        }
        String temp = HtmlUtils.htmlEscape(text);  
        return temp.replaceAll("\n", "<br/>").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }
    
    public static String getAsHtml(Item item, HttpServletRequest request, HttpServletResponse response) {
        boolean isWeb = request != null && response != null;
        
        String tableStyle = " class='jtrac'";
        String tdStyle = "";
        String thStyle = "";
        String altStyle = " class='alt'";
        String labelStyle = " class='label'";
        
        if (!isWeb) {
            // inline CSS so that HTML mail works across most mail-reader clients
            String tdCommonStyle = "border: 1px solid black";
            tableStyle = " class='jtrac' style='border-collapse: collapse; font-family: Arial; font-size: 80%'";
            tdStyle = " style='" + tdCommonStyle + "'";
            thStyle = " style='" + tdCommonStyle + "; background: #CCCCCC'";
            altStyle = " style='background: #DEDEFF'";
            labelStyle = " style='" + tdCommonStyle + "; background: #CCCCCC; font-weight: bold; text-align: right'";
        }

        StringBuffer sb = new StringBuffer();
        sb.append("<table width='100%'" + tableStyle + ">");
        sb.append("<tr" + altStyle + ">");
        sb.append("  <td" + labelStyle + ">ID</td>");
        sb.append("  <td" + tdStyle + ">" + item.getRefId() + "</td>");
        sb.append("  <td" + labelStyle + ">Related Items</td>");
        sb.append("  <td colspan='3'" + tdStyle + ">");
        if (item.getRelatedItems() != null || item.getRelatingItems() != null) {
            String flowUrlParam = null;
            String flowUrl = null;
            if (isWeb) {
                flowUrlParam = "_flowExecutionKey=" + request.getAttribute("flowExecutionKey");
                flowUrl = "/flow?" + flowUrlParam;
            }
            if (item.getRelatedItems() != null) {
                ItemViewForm itemViewForm = null;
                if (isWeb) {
                    itemViewForm = (ItemViewForm) request.getAttribute("itemViewForm");
                    sb.append("<input type='hidden' name='_removeRelated'/>");
                }
                for(ItemItem itemItem : item.getRelatedItems()) {                    
                    String refId = itemItem.getRelatedItem().getRefId();
                    if (isWeb) {
                        String checked = "";
                        Set<Long> set = itemViewForm.getRemoveRelated();
                        if (set != null && set.contains(itemItem.getId())) {
                            checked = " checked='true'";
                        }
                        String url = flowUrl + "&_eventId=viewRelated&itemId=" + itemItem.getRelatedItem().getId();
                        refId = "<a href='" + response.encodeURL(request.getContextPath() + url) + "'>" + refId + "</a>"
                                + "<input type='checkbox' name='removeRelated' value='" 
                                + itemItem.getId() + "' title='remove'" + checked + "/>";
                    }
                    sb.append(itemItem.getRelationText() + " " + refId + " ");
                }
            }
            if (item.getRelatingItems() != null) {
                for(ItemItem itemItem : item.getRelatingItems()) {
                    String refId = itemItem.getItem().getRefId();
                    if (isWeb) {
                        String url = flowUrl + "&_eventId=viewRelated&itemId=" + itemItem.getItem().getId();
                        refId = "<a href='" + response.encodeURL(request.getContextPath() + url) + "'>" + refId + "</a>";
                    }
                    sb.append(refId + " " + itemItem.getRelationText() + " this. ");
                }
            }
        }
        sb.append("  </td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("  <td width='20%'" + labelStyle + ">Status</td>");
        sb.append("  <td" + tdStyle + ">" + item.getStatusValue() + "</td>");
        sb.append("  <td" + labelStyle + ">Logged By</td>");
        sb.append("  <td" + tdStyle + ">" + item.getLoggedBy().getName() + "</td>");
        sb.append("  <td" + labelStyle + ">Assigned To</td>");
        sb.append("  <td width='15%'" + tdStyle + ">" + ( item.getAssignedTo() == null ? "" : item.getAssignedTo().getName() ) + "</td>");
        sb.append("</tr>");
        sb.append("<tr" + altStyle + ">");
        sb.append("  <td" + labelStyle + ">Summary</td>");
        sb.append("  <td colspan='5'" + tdStyle + ">" + HtmlUtils.htmlEscape(item.getSummary()) + "</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("  <td valign='top'" + labelStyle + ">Detail</td>");
        sb.append("  <td colspan='5'" + tdStyle + ">" + fixWhiteSpace(item.getDetail()) + "</td>");
        sb.append("</tr>");
        
        int row = 0;
        Map<Field.Name, Field> fields = item.getSpace().getMetadata().getFields();
        for(Field.Name fieldName : item.getSpace().getMetadata().getFieldOrder()) {
            Field field = fields.get(fieldName);
            sb.append("<tr" + ( row % 2 == 0 ? altStyle : "" ) + ">");
            sb.append("  <td" + labelStyle + ">" + field.getLabel() + "</td>");
            sb.append("  <td colspan='5'" + tdStyle + ">" + item.getCustomValue(fieldName) + "</td>");
            sb.append("</tr>");
            row ++;
        }
        sb.append("</table>");
        
        //=========================== HISTORY ==================================
        sb.append("<br/>&nbsp;<b" + tableStyle + ">History</b>");
        sb.append("<table width='100%'" + tableStyle + ">");
        sb.append("<tr>");
        sb.append("  <th" + thStyle + ">Logged By</th><th" + thStyle + ">Status</th>"
                + "<th" + thStyle + ">Assigned To</th><th" + thStyle + ">Comment</th><th" + thStyle + ">Time Stamp</th>");
        List<Field> editable = item.getSpace().getMetadata().getEditableFields();
        for(Field field : editable) {
            sb.append("<th" + thStyle + ">" + field.getLabel() + "</th>");
        }
        sb.append("</tr>");
        
        if (item.getHistory() != null) {
            row = 1;
            for(History history : item.getHistory()) {
                sb.append("<tr valign='top'" + ( row % 2 == 0 ? altStyle : "" ) + ">");
                sb.append("  <td" + tdStyle + ">" + history.getLoggedBy().getName() + "</td>");
                sb.append("  <td" + tdStyle + ">" + history.getStatusValue() +"</td>");
                sb.append("  <td" + tdStyle + ">" + ( history.getAssignedTo() == null ? "" : history.getAssignedTo().getName() ) + "</td>");
                sb.append("  <td" + tdStyle + ">");
                Attachment attachment = history.getAttachment();
                if (attachment != null) {
                    if (request != null && response != null) {
                        String href = response.encodeURL(request.getContextPath() + "/app/attachments/" + attachment.getFileName() +"?filePrefix=" + attachment.getFilePrefix());
                        sb.append("<a target='_blank' href='" + href + "'>" + attachment.getFileName() + "</a>&nbsp;");
                    } else {
                        sb.append("(attachment:&nbsp;" + attachment.getFileName() + ")&nbsp;");
                    }
                }
                sb.append(fixWhiteSpace(history.getComment()));
                sb.append("  </td>");
                sb.append("  <td" + tdStyle + ">" + history.getTimeStamp() + "</td>");
                for(Field field : editable) {
                    sb.append("<td" + tdStyle + ">" + history.getCustomValue(field.getName()) + "</td>");
                }
                sb.append("</tr>");
                row++;
            }
        }
        sb.append("</table>");
        return sb.toString();
    }
    
}