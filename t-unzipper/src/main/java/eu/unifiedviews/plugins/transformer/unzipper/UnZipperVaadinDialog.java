package eu.unifiedviews.plugins.transformer.unzipper;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * @author Škoda Petr
 */
public class UnZipperVaadinDialog extends BaseConfigDialog<UnZipperConfig_V1> {

    private CheckBox checkNotPrefix;

    public UnZipperVaadinDialog() {
        super(UnZipperConfig_V1.class);
        buildMainLayout();
    }

    private void buildMainLayout() {
        setWidth("100%");
        setHeight("100%");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");

        checkNotPrefix = new CheckBox("Do not prefix symbolic name");
        checkNotPrefix.setDescription("If checked then output symbolic names of output files are not prefixed with symbolic name of unzipped file."
                + "Uncheck to prevent symbolic names collision if multiple zip files with same structure are unzipped. If you do not know, then uncheck this.");
        mainLayout.addComponent(checkNotPrefix);

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(UnZipperConfig_V1 c) throws DPUConfigException {
        checkNotPrefix.setValue(c.isNotPrefixed());
    }

    @Override
    protected UnZipperConfig_V1 getConfiguration() throws DPUConfigException {
        final UnZipperConfig_V1 cnf = new UnZipperConfig_V1();
        cnf.setNotPrefixed(checkNotPrefix.getValue() == null ? false : checkNotPrefix.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        if (checkNotPrefix.getValue() == true) {
            // is true, then we do not use prefixes
            desc.append("Not prefixed.");
        } else {
            // if false prefix is not used
            desc.append("Prefixed.");
        }

        return desc.toString();
    }
}
