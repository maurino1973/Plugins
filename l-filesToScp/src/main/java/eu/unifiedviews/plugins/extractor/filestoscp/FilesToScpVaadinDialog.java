package eu.unifiedviews.plugins.extractor.filestoscp;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

public class FilesToScpVaadinDialog extends BaseConfigDialog<FilesToScpConfig_V1> {

    private VerticalLayout mainLayout;

    private TextField txtHost;

    private TextField txtPort;

    private TextField txtUser;

    private PasswordField txtPassword;

    private TextField txtDestination;

    private CheckBox chbSoftFail;

    public FilesToScpVaadinDialog() {
        super(FilesToScpConfig_V1.class);
        buildLayout();
    }

    private void buildLayout() {
        mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        txtHost = new TextField();
        txtHost.setWidth("100%");
        txtHost.setHeight("-1px");
        txtHost.setCaption("Host:");
        txtHost.setRequired(true);
        mainLayout.addComponent(txtHost);

        txtPort = new TextField();
        txtPort.setWidth("100%");
        txtPort.setHeight("-1px");
        txtPort.setCaption("Port:");
        txtPort.setRequired(true);
        mainLayout.addComponent(txtPort);

        txtUser = new TextField();
        txtUser.setWidth("100%");
        txtUser.setHeight("-1px");
        txtUser.setCaption("Username:");
        txtUser.setRequired(true);
        mainLayout.addComponent(txtUser);

        txtPassword = new PasswordField();
        txtPassword.setWidth("100%");
        txtPassword.setHeight("-1px");
        txtPassword.setCaption("Password:");
        txtPassword.setRequired(true);
        txtPassword.setNullRepresentation("");
        mainLayout.addComponent(txtPassword);

        txtDestination = new TextField();
        txtDestination.setWidth("100%");
        txtDestination.setHeight("-1px");
        txtDestination.setCaption("Destination: (must exist and not end with '/')");
        txtDestination.setRequired(true);
        txtDestination.setNullRepresentation("");
        mainLayout.addComponent(txtDestination);

        mainLayout.addComponent(new Label("Note: You need rights to access the target directory and all its parent directories"));

        chbSoftFail = new CheckBox();
        chbSoftFail.setCaption("Soft failure");
        mainLayout.addComponent(chbSoftFail);

        mainLayout.addComponent(new Label(
                "If 'Soft failure' is checked and upload failed, "
                        + "then pipeline continue, otherwise the pipeline is stopped."));

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(FilesToScpConfig_V1 c) throws DPUConfigException {
        txtHost.setValue(c.getHostname());
        txtPort.setValue(c.getPort().toString());
        txtUser.setValue(c.getUsername());
        txtPassword.setValue(c.getPassword());
        txtDestination.setValue(c.getDestination());
        chbSoftFail.setValue(c.isSoftFail());
    }

    @Override
    protected FilesToScpConfig_V1 getConfiguration() throws DPUConfigException {
        if (!txtHost.isValid()) {
            throw new DPUConfigException("Destination host must be specified!");
        }

        FilesToScpConfig_V1 cnf = new FilesToScpConfig_V1();
        cnf.setHostname(txtHost.getValue());
        try {
            cnf.setPort(Integer.parseInt(txtPort.getValue()));
        } catch (NumberFormatException e) {
            throw new DPUConfigException("Port must be a number.");
        }
        cnf.setUsername(txtUser.getValue());
        cnf.setPassword(txtPassword.getValue());
        cnf.setDestination(getDestination());
        cnf.setSoftFail(chbSoftFail.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        desc.append("Upload into ");
        desc.append(txtUser.getValue());
        desc.append("@");
        desc.append(txtHost.getValue());
        desc.append(":");
        desc.append(getDestination());

        return desc.toString();
    }

    private String getDestination() {
        String dest = txtDestination.getValue().replace('\\', '/');
        if (dest.endsWith("/")) {
            dest = dest.substring(0, dest.length() - 1);
        }
        return dest;
    }

}
