package eu.unifiedviews.plugins.transformer.filesfilter;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

public class Dialog extends BaseConfigDialog<Configuration> {

    private static final int OPTION_SYMBOLIC_NAME = 1;

    private static final int OPTION_VIRTUAL_PATH = 2;

    private VerticalLayout mainLayout;

    private OptionGroup optType;

    private TextField txtObject;

    private CheckBox checkUseRegExp;

    public Dialog() {
        super(Configuration.class);
        buildMainLayout();
    }

    private void buildMainLayout() {
        setWidth("100%");
        setHeight("100%");

        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setSpacing(true);

        optType = new OptionGroup("Used filter:");
        optType.addItem(OPTION_SYMBOLIC_NAME);
        optType.setItemCaption(OPTION_SYMBOLIC_NAME, "symbolic name");

        optType.addItem(OPTION_VIRTUAL_PATH);
        optType.setItemCaption(OPTION_VIRTUAL_PATH, "virtual path");

        mainLayout.addComponent(optType);

        txtObject = new TextField("Custom predicate:");
        txtObject.setWidth("100%");
        txtObject.setRequired(true);
        mainLayout.addComponent(txtObject);

        checkUseRegExp = new CheckBox("Use regular expression:");
        mainLayout.addComponent(checkUseRegExp);

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(Configuration c) throws DPUConfigException {

        if (c.getPredicate().compareTo(Configuration.SYMBOLIC_NAME) == 0) {
            optType.setValue(OPTION_SYMBOLIC_NAME);
        } else {
            // TODO We can be more save here ..
            optType.setValue(OPTION_VIRTUAL_PATH);
        }

        txtObject.setValue(c.getObject());
        checkUseRegExp.setValue(c.isUseRegExp());
    }

    @Override
    protected Configuration getConfiguration() throws DPUConfigException {
        if (!txtObject.isValid()) {
            throw new DPUConfigException("All fields must be filled.");
        }

        final Configuration cnf = new Configuration();
        final Integer selected = (Integer) optType.getValue();
        switch (selected) {
            case OPTION_SYMBOLIC_NAME:
                cnf.setPredicate(Configuration.SYMBOLIC_NAME);
                break;
            case OPTION_VIRTUAL_PATH:
                cnf.setPredicate(Configuration.VIRTUAL_PATH);
                break;
            default:
                throw new DPUConfigException("The dialog is broken - unknown selection.");
        }
        cnf.setObject(txtObject.getValue());
        cnf.setUseRegExp(checkUseRegExp.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        desc.append("Filter by : ");

        final Integer selected = (Integer) optType.getValue();
        switch (selected) {
            case OPTION_SYMBOLIC_NAME:
                desc.append("symbolic name");
                break;
            case OPTION_VIRTUAL_PATH:
                desc.append("virtual path");
                break;
        }

        desc.append(" for ");
        desc.append(txtObject.getValue());

        return desc.toString();
    }

}
