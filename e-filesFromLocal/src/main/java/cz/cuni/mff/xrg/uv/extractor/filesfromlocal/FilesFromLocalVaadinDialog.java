package cz.cuni.mff.xrg.uv.extractor.filesfromlocal;

import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

public class FilesFromLocalVaadinDialog extends BaseConfigDialog<FilesFromLocalConfig_V1> {

    private TextField txtSource;

    public FilesFromLocalVaadinDialog() {
        super(FilesFromLocalConfig_V1.class);
        buildMainLayout();
    }

    private void buildMainLayout() {
        setWidth("100%");
        setHeight("100%");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");

        mainLayout.addComponent(new Label("File or directory to extract:"));

        txtSource = new TextField("");
        txtSource.setWidth("100%");
        txtSource.setRequired(true);
        txtSource.setNullRepresentation("");
        txtSource.setNullSettingAllowed(false);
        mainLayout.addComponent(txtSource);

        mainLayout.addComponent(new Label("If directory is provided then all files </br>and files in subdirectories are extracted."));

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(FilesFromLocalConfig_V1 c) throws DPUConfigException {
        txtSource.setValue(c.getSource());
    }

    @Override
    protected FilesFromLocalConfig_V1 getConfiguration() throws DPUConfigException {
        if (!txtSource.isValid()) {
            throw new DPUConfigException("All values must be filled.");
        }

        final FilesFromLocalConfig_V1 cnf = new FilesFromLocalConfig_V1();
        cnf.setSource(txtSource.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        desc.append("Extract from ");
        desc.append(txtSource.getValue());

        return desc.toString();
    }

}
