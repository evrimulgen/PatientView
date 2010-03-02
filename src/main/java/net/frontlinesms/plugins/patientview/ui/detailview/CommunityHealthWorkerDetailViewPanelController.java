package net.frontlinesms.plugins.patientview.ui.detailview;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.awt.Component;
import java.util.HashMap;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.ui.personpanel.CommunityHealthWorkerPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonAttributePanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class CommunityHealthWorkerDetailViewPanelController implements
		DetailViewPanelController<CommunityHealthWorker> {

	private static final String EDIT_CHW_ATTRIBUTES = "detailview.buttons.edit.chw.attributes";
	private static final String SAVE_CHW_ATTRIBUTES = "detailview.buttons.save";
	private static final String CANCEL = "detailview.buttons.cancel";
	private Object mainPanel;
	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	private boolean inEditingMode;
	
	private CommunityHealthWorkerPanel currentCHWPanel;
	private PersonAttributePanel currentAttributePanel;
	
	public CommunityHealthWorkerDetailViewPanelController(UiGeneratorController uiController,ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
	}
	public Class getEntityClass() {
		return CommunityHealthWorker.class;
	}

	public HashMap<String, String> getFurtherOptions() {
		return null;
	}

	public Object getPanel() {
		return mainPanel;
	}

	public void viewWillAppear(CommunityHealthWorker p) {
		mainPanel = uiController.create("panel");
		uiController.setWeight(mainPanel, 1, 1);
		uiController.setColumns(mainPanel, 1);
		currentCHWPanel = new CommunityHealthWorkerPanel(uiController,appCon,p);
		currentAttributePanel =new PersonAttributePanel(uiController,appCon,p);
		uiController.add(mainPanel, currentCHWPanel.getMainPanel());
		uiController.add(mainPanel, currentAttributePanel.getMainPanel());
		uiController.add(mainPanel, getBottomButtons());
	}
	
	private Object getBottomButtons(){
		Object buttonPanel = uiController.create("panel");
		uiController.setName(buttonPanel, "buttonPanel");
		uiController.setColumns(buttonPanel, 3);
		Object leftButton = uiController.createButton(!inEditingMode?getI18NString(EDIT_CHW_ATTRIBUTES):getI18NString(SAVE_CHW_ATTRIBUTES));
		if(inEditingMode){
			uiController.setAction(leftButton, "saveButtonClicked", null, this);
		}else{
			uiController.setAction(leftButton, "editButtonClicked", null, this);
		}
		uiController.setHAlign(leftButton, Thinlet.LEFT);
		uiController.setVAlign(leftButton, Thinlet.BOTTOM);
		uiController.add(buttonPanel,leftButton);
		if(inEditingMode){
			Object spacerLabel = uiController.createLabel("");
			uiController.setWeight(spacerLabel, 1, 0);
			uiController.add(buttonPanel,spacerLabel);
			Object rightButton = uiController.createButton(getI18NString(CANCEL));
			uiController.setHAlign(rightButton, Thinlet.RIGHT);
			uiController.setVAlign(rightButton, Thinlet.BOTTOM);
			uiController.setAction(rightButton, "cancelButtonClicked", null, this);
			uiController.add(buttonPanel, rightButton);
		}
		uiController.setWeight(buttonPanel, 1, 1);
		uiController.setVAlign(buttonPanel, Thinlet.BOTTOM);
		return buttonPanel;
	}

	public void editButtonClicked(){
		inEditingMode=true;
		currentAttributePanel.switchToEditingPanel();
		uiController.remove(uiController.find(mainPanel,"buttonPanel"));
		uiController.add(mainPanel,getBottomButtons());
	}
	
	public void saveButtonClicked(){
		inEditingMode=false;
		currentAttributePanel.stopEditingWithSave();
		uiController.remove(uiController.find(mainPanel,"buttonPanel"));
		uiController.add(mainPanel,getBottomButtons());
	}
	
	public void cancelButtonClicked(){
		inEditingMode=false;
		currentAttributePanel.stopEditingWithoutSave();
		uiController.remove(uiController.find(mainPanel,"buttonPanel"));
		uiController.add(mainPanel,getBottomButtons());
	}

	public void viewWillDisappear() {/* do nothing */}


}