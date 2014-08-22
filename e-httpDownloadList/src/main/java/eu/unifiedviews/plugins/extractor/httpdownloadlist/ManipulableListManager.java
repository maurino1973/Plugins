package eu.unifiedviews.plugins.extractor.httpdownloadlist;

import java.util.LinkedList;
import java.util.List;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Manager for manipulating list of components. This
 * creates vaadin component with option to add (+ button)
 * or remove (- button) "rows". The class creating this
 * manager should implement {@link ManipolableListComponentProvider}.
 * This class manages the process of adding and removing components
 * from the list and not the components itself.
 * 
 * @author mvi
 *
 */
public class ManipulableListManager {
	private String buttonWidth = "55px";
	private List<Component> dataList;
	private ManipolableListComponentProvider componentProvider;
	private GridLayout mainLayout;
	
	public ManipulableListManager(ManipolableListComponentProvider componentProvider) {
		this.componentProvider = componentProvider;
	}
	
	/**
	 * 
	 * @param list with components or null
	 * @return
	 */
	public GridLayout initList(List<Component> list) {
		this.dataList = list != null ? list : new LinkedList<Component>();
		
		mainLayout = new GridLayout();
		mainLayout.setSpacing(true);
        mainLayout.setImmediate(false);
        mainLayout.setMargin(false);
        mainLayout.setColumns(2);
        mainLayout.setColumnExpandRatio(0, 0.95f);
        mainLayout.setColumnExpandRatio(1, 0.05f);

        refreshData();
		return mainLayout;
	}
	
	private void refreshData() {
		mainLayout.removeAllComponents();

		if (dataList.size() < 1) {
			dataList.add(componentProvider.createNewComponent());
		}
		
		int row = 0;
		mainLayout.setRows(dataList.size() + 1);
		for (Component component : dataList) {
			
			Button removeButton = new Button();
			removeButton.setEnabled(dataList.size() > 1);
			removeButton.setWidth(buttonWidth);
			removeButton.setCaption("-");
			removeButton.setData(row);
			removeButton.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 607166912536251164L;

				@Override
				public void buttonClick(ClickEvent event) {
					Integer row = (Integer) event.getButton().getData();
					removeData(row);
					refreshData();
				}
			});
			
			mainLayout.addComponent(component, 0, row);
			mainLayout.addComponent(removeButton, 1, row);
			mainLayout.setComponentAlignment(removeButton, Alignment.TOP_RIGHT);
			row++;
		}
		
		// add button
		Button addButton = new Button();
		addButton.setCaption("+");
		addButton.setImmediate(true);
		addButton.setWidth(buttonWidth);
		addButton.setHeight("-1px");
		addButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 3112570238711166625L;

			@Override
			public void buttonClick(ClickEvent event) {
				dataList.add(componentProvider.createNewComponent());
				refreshData();
			}
		});
		mainLayout.addComponent(addButton, 0, row);
	}

	private void removeData(int row) {
		if (dataList.size() > 1) {
			dataList.remove(row);
		}
	}

	public List<Component> getDataList() {
		return dataList;
	}

	public void setDataList(List<Component> dataList) {
		this.dataList = dataList;
		refreshData();
	}
}
