package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.TextBox;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class PasswordTextField extends TextBox implements PersonalFormField{

	public static final String NAME = "passwordField";
	private String response;
	
	public PasswordTextField(ExtendedThinlet thinlet, String password){
		super(thinlet, InternationalisationUtils.getI18NString("login.password")+":", NAME);
		response = "";
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setAttachedObject(mainPanel, this);
	}
	
	public void textBoxKeyPressed(String typed){
		super.textBoxKeyPressed(typed);
		String newText = typed.substring(typed.lastIndexOf("*") + 1);
		if(typed.lastIndexOf("*") <  response.length() -1){
			response = response.substring(0, typed.lastIndexOf("*") + 1) + newText;
		}else{
			response = response + newText;
		}
		String mask = "";
		for(int i = 0; i < response.length();i++){
			mask = mask + "*";
		}
		thinlet.setText(textBox, mask);	
	}

	public boolean isValid() {
		return true;
	}
	
	@Override
	public String getResponse() {
		return response;
	}

	@Override
	public void setResponse(String s) {
		textBoxKeyPressed(s);
	}

	public void setFieldForPerson(Person p) {
		if(p instanceof User){
			((User) p).setPassword(getResponse());
		}
	}

}
