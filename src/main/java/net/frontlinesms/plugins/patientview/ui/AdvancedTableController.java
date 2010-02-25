package net.frontlinesms.plugins.patientview.ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.frontlinesms.events.EventNotifier;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEvent;
import net.frontlinesms.events.impl.DidSaveNotification;
import net.frontlinesms.events.impl.DidUpdateNotification;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class AdvancedTableController implements EventObserver{
	
	/** the thinlet table**/
	private Object table;
	
	private boolean useTableMethod;
	
	/** the headers for the table **/
	private Map<Class,Object> headers;
		
	private AdvancedTableActionDelegate delegate;
	private AdvancedTableDataSource dataSource;
	
	public AdvancedTableDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(AdvancedTableDataSource dataSource) {
		this.dataSource = dataSource;
	}

	private UiGeneratorController uiController;
	
	private Class currentClass;
	
	/** The size of the results array */
	private int resultsSize;
	
	/** Objects for determining text width**/
	private ImageIcon icon;
	private Graphics graphics;
	private Font font;
	private FontMetrics metrics;
	
	public AdvancedTableController(AdvancedTableActionDelegate delegate, UiGeneratorController uiController, boolean useTableMethod){
		this.uiController = uiController;
		this.useTableMethod = useTableMethod;
		this.delegate = delegate;
		if(!useTableMethod){
			table = uiController.create("table");
			uiController.setInteger(table, "weightx", 1);
			uiController.setInteger(table, "weighty", 1);
		}
		uiController.setAction(getTable(), "tableSelectionChange()", null, this);
		uiController.setPerform(getTable(), "doubleClick()",null,this);
		uiController.setChoice(getTable(), "selection", "single");
		headers = new HashMap<Class, Object>();
		
		//initialize stuff for determining font width
		icon = new ImageIcon();
		icon.setImage(new BufferedImage(10,10,BufferedImage.OPAQUE));
		graphics = icon.getImage().getGraphics();
		font= new Font("Sans Serif",Font.PLAIN,14);
		metrics = graphics.getFontMetrics(font);
	}
	
	public AdvancedTableController(AdvancedTableActionDelegate delegate, UiGeneratorController uiController, boolean useTableMethod, ApplicationContext appcon, AdvancedTableDataSource dataSource){
		this.uiController = uiController;
		this.useTableMethod = useTableMethod;
		((EventNotifier) appcon.getBean("eventNotifier")).registerObserver(this);
		this.dataSource = dataSource;
		this.delegate = delegate;
		if(!useTableMethod){
			table = uiController.create("table");
			uiController.setInteger(table, "weightx", 1);
			uiController.setInteger(table, "weighty", 1);
		}
		uiController.setAction(getTable(), "tableSelectionChange()", null, this);
		uiController.setPerform(getTable(), "doubleClick()",null,this);
		uiController.setChoice(getTable(), "selection", "single");
		headers = new HashMap<Class, Object>();
		
		//initialize stuff for determining font width
		icon = new ImageIcon();
		icon.setImage(new BufferedImage(10,10,BufferedImage.OPAQUE));
		graphics = icon.getImage().getGraphics();
		font= new Font("Sans Serif",Font.PLAIN,14);
		metrics = graphics.getFontMetrics(font);
	}
	
	/** creates a new header option for the specified class**/
	@SuppressWarnings("static-access")
	public void putHeader(Class headerClass, String[] columnNames, String[] columnMethods){
		Object header = uiController.create("header");
		for(int i = 0; i < columnNames.length; i++){
			uiController.add(header, uiController.createColumn(columnNames[i], columnMethods[i]));
		}
		uiController.setAction(header,"headerClicked()",null,this);
		headers.put(getRealClass(headerClass), header);
	}
	
	
	/**
	 *  sets the results of the table
	 * if the header for the class of the results has already been set, it will create the proper 
	 * header and autofit it to the width of the results
	 * @param results
	 */
	public void setResults(List results){
		resultsSize = results.size();
		if(results.size() == 0){
			uiController.removeAll(getTable());
			Object header = uiController.create("header");
			uiController.add(header,uiController.createColumn("No results to display", null));
			uiController.add(getTable(),header);
			Object row = uiController.createTableRow(null);
			uiController.add(row, uiController.createTableCell("There were no results matching your search..."));
			uiController.add(getTable(),row);
			delegate.resultsChanged();
			return;
		}
		if(useTableMethod){
			uiController.setAction(getTable(), "tableSelectionChange()", null, this);
			uiController.setPerform(getTable(), "doubleClick()",null,this);
		}
		uiController.removeAll(getTable());
		currentClass = getRealClass(results.get(0).getClass());
		uiController.add(getTable(),getAutoFitHeader(results));
		List<Method> methods = getMethodsForClass(currentClass);
		for(Object result: results){
			Object row = uiController.createTableRow(result);
			for(Method m:methods){
				try {
					uiController.add(row,uiController.createTableCell((String) m.invoke(result,null)));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			uiController.add(getTable(),row);
		}
		delegate.resultsChanged();
	}
	
	public static Class getRealClass(Class c){
		String s = c.getName();
		if(s.indexOf("_$$_javassist") != -1){
			s = s.substring(0, s.indexOf("_$$_javassist"));
		}
		try {
			return Class.forName(s);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * get all of the methods for the columns that are in the result display of Class c
	 * @param c
	 * @return
	 */
	private List<Method> getMethodsForClass(Class c){
		ArrayList<Method> results = new ArrayList<Method>();
		Object [] columns = uiController.getItems(headers.get(c));
		for(Object column : columns){
			try {
				results.add(c.getMethod((String) uiController.getAttachedObject(column), null));
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
	
	/**
	 * get the auto-fitted header for the class of the objects in results
	 * @param results
	 * @return
	 */
	private Object getAutoFitHeader(List results){
		Class c = getRealClass(results.get(0).getClass());
		Object tempHeader = headers.get(c);
		for(Object column :uiController.getItems(tempHeader)){
			uiController.setWidth(column, getColumnWidth(column,results,c));
		}
		return tempHeader;
	}
	
	private int getColumnWidth(Object column, List results, Class c){
		int result = getStringWidth(uiController.getText(column));
		Method m=null;
		try {
			m = c.getMethod((String) uiController.getAttachedObject(column), null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		m.setAccessible(true);
		for(Object r :results){
			int tempWidth = 0;
			try {
				String s = (String) m.invoke(r,null);
				tempWidth = getStringWidth(s);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			result = Math.max(result, tempWidth);
		}
		return result;
	}
	
	private int getStringWidth(String text){
		return metrics.stringWidth(text);
	}
	
	public void setTable(Object table){
		useTableMethod = false;
		uiController.removeAll(table);
		this.table = table;
		uiController.setAction(table, "tableSelectionChange()", null, this);
		uiController.setPerform(table, "doubleClick()",null,this);
	}
	
	public void tableSelectionChange(){
		Object entity = uiController.getAttachedObject(uiController.getSelectedItem(getTable()));
		delegate.selectionChanged(entity);
	}
	
	public void doubleClick(){		
		Object entity = uiController.getAttachedObject(uiController.getSelectedItem(getTable()));
		delegate.doubleClickAction(entity);
	}
	
	public Object getTable(){
		if(useTableMethod){
			return delegate.getTable();
		}else{
			return table;
		}
	}
	
	/**
	 * Selects the row at "index"
	 * @param index
	 */
	public void setSelected(int index){
		if(index < resultsSize){
			uiController.setSelectedIndex(getTable(),index);
			tableSelectionChange();
		}
	}
	
	/**
	 * @return the header object that is currently in use
	 */
	private Object getCurrentHeader(){
		return headers.get(currentClass);
	}
	
	public void headerClicked(){
//		int index = uiController.getSelectedIndex(getCurrentHeader());
//		String sort = uiController.getChoice(uiController.getSelectedItem(getCurrentHeader()), "sort");
//		boolean sortOrder = (sort.equals("ascent"))? true:false;
//		delegate.getQueryGenerator().setSort(, sortOrder);
	}
	
	public void clearResults(){
		uiController.removeAll(getTable());
	}

	public void notify(FrontlineEvent event) {
		if(event instanceof DidUpdateNotification || event instanceof DidSaveNotification){
			if(dataSource !=null){
				int selectedIndex = uiController.getSelectedIndex(getTable());
				dataSource.refreshResults();
				uiController.setSelectedIndex(getTable(), selectedIndex);
			}
		}
	}
}
