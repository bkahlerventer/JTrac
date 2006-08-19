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

package info.jtrac.domain;

import org.apache.lucene.document.Document;

/**
 * Any updates to an Item (even a new insert) causes a snapshot of
 * the item to be stored in the History table.
 * In this way for each Item, a History view is available which
 * shows the diffs, who made changes and when, etc.
 */
public class History extends AbstractItem {
    
    private String comment;
    private Double actualEffort;
    private Attachment attachment;

    public History() {
        // zero arg constructor
    }
    
    public History(Item item) {
        setSummary(item.getSummary());
        setDetail(item.getDetail());
        setLoggedBy(item.getLoggedBy());
        setAssignedTo(item.getAssignedTo());
        // setTimeStamp(item.getTimeStamp());
        setPlannedEffort(item.getPlannedEffort());
        //==========================
        for(Field.Name fieldName : Field.Name.values()) {
            setValue(fieldName, item.getValue(fieldName));
        }
    }
    
    /**
     * Lucene DocumentCreator implementation
     */
    public Document createDocument() {
        Document d = new Document();
        d.add(org.apache.lucene.document.Field.UnIndexed("id", getId() + ""));
        d.add(org.apache.lucene.document.Field.UnIndexed("itemId", getParent().getId() + ""));
        d.add(org.apache.lucene.document.Field.UnIndexed("type", "history"));
        d.add(org.apache.lucene.document.Field.UnStored("summary", getSummary()));
        d.add(org.apache.lucene.document.Field.UnStored("detail", getDetail()));
        d.add(org.apache.lucene.document.Field.UnStored("comment", comment));
        return d;
    }
    
    @Override
    public String getRefId() {
        return getParent().getRefId();
    }      
    
    @Override
    public Space getSpace() {
        return getParent().getSpace();
    }            
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public Double getActualEffort() {
        return actualEffort;
    }

    public void setActualEffort(Double actualEffort) {
        this.actualEffort = actualEffort;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append("; comment [").append(comment);
        sb.append("]; actualEffort [").append(actualEffort);
        sb.append("]; attachment [").append(attachment);
        sb.append("]");
        return sb.toString();
    }
    
}
