package eu.unifiedviews.plugins.loader.filestovirtuoso;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class VirtuosoLoaderVaadinDialog extends BaseConfigDialog<VirtuosoLoaderConfig_V1> {
    /**
     * 
     */
    private static final long serialVersionUID = -5666075836909428L;

    private static final String VIRTUOSO_URL_LABEL = "Virtuoso JDBC URL";

    private static final String USERNAME_LABEL = "Username";

    private static final String PASSWORD_LABEL = "Password";

    private static final String CLEAR_DESTINATION_GRAPH_LABEL = "Clear destination graph before loading";

    private static final String LOAD_DIRECTORY_PATH_LABEL = "Directory to load path";

    private static final String INCLUDE_SUBDIRECTORIES_LABEL = "Include subdirectories";

    private static final String LOAD_FILE_PATTERN_LABEL = "File name pattern";

    private static final String TARGET_CONTEXT_LABEL = "Target graph";

    private static final String TARGET_TEMP_CONTEXT_LABEL = "Target temporary graph";

    private static final String STATUS_UPDATE_INTERVAL_LABEL = "Update status interval (s)";

    private static final String THREAD_COUNT_LABEL = "Thread count";

    private static final String SKIP_ON_ERROR_LABEL = "Skip file on error";

    private ObjectProperty<String> virtuosoUrl = new ObjectProperty<String>("");

    private ObjectProperty<String> username = new ObjectProperty<String>("");

    private ObjectProperty<String> password = new ObjectProperty<String>("");

    private ObjectProperty<Boolean> clearDestinationGraph = new ObjectProperty<Boolean>(false);

    private ObjectProperty<String> loadDirectoryPath = new ObjectProperty<String>("");

    private ObjectProperty<Boolean> includeSubdirectories = new ObjectProperty<Boolean>(true);

    private ObjectProperty<String> loadFilePattern = new ObjectProperty<String>("");

    private ObjectProperty<String> targetContext = new ObjectProperty<String>("");

//    private ObjectProperty<String> targetTempContext = new ObjectProperty<String>("");

    private ObjectProperty<Long> statusUpdateInterval = new ObjectProperty<Long>(60L);

    private ObjectProperty<Integer> threadCount = new ObjectProperty<Integer>(1);

    private ObjectProperty<Boolean> skipOnError = new ObjectProperty<Boolean>(
            false);

    public VirtuosoLoaderVaadinDialog() {
        super(VirtuosoLoaderConfig_V1.class);
        initialize();
    }

    private void initialize() {
        FormLayout mainLayout = new FormLayout();

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        mainLayout.addComponent(createTextField(VIRTUOSO_URL_LABEL, virtuosoUrl));
        mainLayout.addComponent(createTextField(USERNAME_LABEL, username));
        mainLayout.addComponent(createTextField(PASSWORD_LABEL, password));
        mainLayout.addComponent(new CheckBox(CLEAR_DESTINATION_GRAPH_LABEL, clearDestinationGraph));
        mainLayout.addComponent(createTextField(LOAD_DIRECTORY_PATH_LABEL, loadDirectoryPath));
        mainLayout.addComponent(createTextField(INCLUDE_SUBDIRECTORIES_LABEL, includeSubdirectories));
        mainLayout.addComponent(createTextField(LOAD_FILE_PATTERN_LABEL, loadFilePattern));
        mainLayout.addComponent(createTextField(TARGET_CONTEXT_LABEL, targetContext));
//        mainLayout.addComponent(createTextField(TARGET_TEMP_CONTEXT_LABEL, targetTempContext));
        mainLayout.addComponent(createTextField(STATUS_UPDATE_INTERVAL_LABEL, statusUpdateInterval));
        mainLayout.addComponent(createTextField(THREAD_COUNT_LABEL, threadCount));
        mainLayout.addComponent(new CheckBox(SKIP_ON_ERROR_LABEL, skipOnError));

        setCompositionRoot(mainLayout);
    }

    private <T> TextField createTextField(String caption, ObjectProperty<T> property) {
        final TextField result = new TextField(caption, property);
        result.setWidth("100%");
        return result;
    }

    @Override
    public void setConfiguration(VirtuosoLoaderConfig_V1 conf) throws DPUConfigException {
        virtuosoUrl.setValue(conf.getVirtuosoUrl());
        username.setValue(conf.getUsername());
        password.setValue(conf.getPassword());
        clearDestinationGraph.setValue(conf.isClearDestinationGraph());
        loadDirectoryPath.setValue(conf.getLoadDirectoryPath());
        includeSubdirectories.setValue(conf.isIncludeSubdirectories());
        loadFilePattern.setValue(conf.getLoadFilePattern());
        targetContext.setValue(conf.getTargetContext());
//        targetTempContext.setValue(conf.getTargetTempContext());
        statusUpdateInterval.setValue(conf.getStatusUpdateInterval());
        threadCount.setValue(conf.getThreadCount());
        skipOnError.setValue(conf.isSkipOnError());
    }

    @Override
    public VirtuosoLoaderConfig_V1 getConfiguration() throws DPUConfigException {
        VirtuosoLoaderConfig_V1 conf = new VirtuosoLoaderConfig_V1();
        conf.setVirtuosoUrl(virtuosoUrl.getValue());
        conf.setUsername(username.getValue());
        conf.setPassword(password.getValue());
        conf.setClearDestinationGraph(clearDestinationGraph.getValue());
        conf.setLoadDirectoryPath(loadDirectoryPath.getValue());
        conf.setIncludeSubdirectories(includeSubdirectories.getValue());
        conf.setLoadFilePattern(loadFilePattern.getValue());
        conf.setTargetContext(targetContext.getValue());
//        conf.setTargetTempContext(targetTempContext.getValue());
        conf.setStatusUpdateInterval(statusUpdateInterval.getValue());
        conf.setThreadCount(threadCount.getValue());
        conf.setSkipOnError(skipOnError.getValue());
        return conf;
    }

}
