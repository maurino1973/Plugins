package eu.unifiedviews.plugins.extractor.httpdownload;

import com.vaadin.ui.Component;

/**
 * @author mvi
 */
public interface ManipulableListComponentProvider {

    /**
     * Creating new component with unset value
     * 
     * @return
     */
    Component createNewComponent();

    /**
     * creates new component with values set
     * 
     * @param values
     * @return
     */
    Component createNewComponent(String[] values);

}
