package net.frontlinesms.plugins.patientview.data.domain.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;

import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name="medic_forms")
public class MedicForm {

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long fid;
	
	@IndexColumn(name="form_name_index")
	private String name;
	
	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL, mappedBy="parentForm")
	private List<MedicFormField> fields;
	
	@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="form")
	private Set<MedicFormResponse> responses;
	
	@OneToOne(fetch=FetchType.LAZY, cascade={})
	@JoinColumn(name="vanilla_form_id", nullable=true)
	private Form form;
	
	public MedicForm(){}
	
	public MedicForm(String name){
		this.name = name;
		fields = new ArrayList<MedicFormField>();
	}
	
	/**
	 * Gets the vanilla frontline form that this medic form is linked to
	 * @return the form
	 */
	public Form getForm() {
		return form;
	}

	/**
	 * Sets the vanilla frontline form that this medic form is linked to
	 * @param form
	 */
	public void setForm(Form form) {
		this.form = form;
	}

	public MedicForm(String name, List<MedicFormField> fields){
		this.name = name;
		setFormFields(fields);
	}
	
	public void setFormFields(List<MedicFormField> fields){
		this.fields = fields;
		int i = 0;
		for(MedicFormField mff: this.fields){
			mff.setForm(this);
			mff.setPosition(i);
			i++;
		}
	}
	
	/**
	 * creates a MedicForm from a vanilla frontline form
	 * @param f
	 */
	public MedicForm(Form f){
		this.form = f;
		this.name = f.getName();
		fields = new ArrayList<MedicFormField>();
		for(FormField field : f.getFields()){
			fields.add(new MedicFormField(this,
					DataType.getDataTypeForString(field.getType().name()),
					field.getLabel()));
		}
	}

	/**
	 * @return the name of the form
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name of the form
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ID of the form
	 */
	public long getFid() {
		return fid;
	}

	/**
	 * @return a list of the fields on the form
	 */
	public List<MedicFormField> getFields() {
		return fields;
	}
	
	/**
	 * Adds a field to the form at the end
	 * @param field
	 */
	public void addField(MedicFormField field){
		field.setForm(this);
		field.setPosition(fields.size());
		fields.add(field);
	}
	
	/**
	 * removes a field from the form
	 * @param field
	 */
	public void removedField(MedicFormField field){
		fields.remove(field);
	}
	
	/**
	 * @return All the responses to this form
	 */
	public Set<MedicFormResponse> getResponses(){
		return responses;
	}
	
}
