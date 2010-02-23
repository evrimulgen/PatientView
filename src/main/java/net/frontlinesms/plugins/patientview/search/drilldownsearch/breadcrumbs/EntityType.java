package net.frontlinesms.plugins.patientview.search.drilldownsearch.breadcrumbs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.frontlinesms.data.domain.Message;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * 
 * @author Dieterich
 * This class defines the types of entities that can be searched. 
 * Each entity can have a list of entities attached to it. These entities, if they are already
 * in the drill-down hierarchy, will prevent that entity from being drilled down into.
 */

public enum EntityType {
	CHW("medic.common.chw",CommunityHealthWorker.class),
	PATIENT("medic.common.patient", Patient.class),
	MESSAGE("medic.common.text.message", Message.class),
	FORM("medic.common.form",Form.class),
	FIELD("medic.common.field", FormField.class);
	
	/**
	 * the name of the entity
	 */
	private String name;
	
	private Class entityClass;
	
	private EntityType(String name, Class entityClass){
		this.name = name;
		this.entityClass = entityClass;
	}
	
	public String getName(){
		return InternationalisationUtils.getI18NString(name);
	}
	public Class getEntityClass(){
		return entityClass;
	}
	
	public boolean equals(EntityType o){
		return (o.getName().equals(getName()) && o.getClass().equals(getClass()));
	}
}