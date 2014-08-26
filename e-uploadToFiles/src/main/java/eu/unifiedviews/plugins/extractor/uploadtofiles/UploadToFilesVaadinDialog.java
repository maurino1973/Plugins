package eu.unifiedviews.plugins.extractor.uploadtofiles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class UploadToFilesVaadinDialog extends BaseConfigDialog<UploadToFilesConfig_V1> {
    private static final long serialVersionUID = 2397849673588724L;

    private Map<String, String> symbolicNameToURIMap = new HashMap<String, String>();

    private File destinationDir;

    private VerticalLayout filesLayout;

    public UploadToFilesVaadinDialog() {
        super(UploadToFilesConfig_V1.class);
        destinationDir = new File(System.getProperty("user.home"));
        if (!destinationDir.exists() || !destinationDir.isDirectory()) {
            Notification.show("System property not set.", "System property user.home isn't set to "
                    + "existing directory.", Notification.Type.ERROR_MESSAGE);
            return;
        }
        initialize();
    }

    private void initialize() {
        // top-level component properties
        setSizeFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);

        Panel panel = new Panel();
        panel.setSizeFull();

        filesLayout = new VerticalLayout();
        panel.setContent(filesLayout);

        MultiFileUpload upload = new MultiFileUpload(new UploadFinishedHandler() {

            @Override
            public void handleFile(InputStream fileIS, String fileName, String contentType, long size) {
                File outputFile = null; //new File(destinationDir, fileName);
                try {
                    CopyOption co = StandardCopyOption.REPLACE_EXISTING;
                    outputFile = File.createTempFile("unifiedviews_upload_", null, destinationDir);
                    Files.copy(fileIS, outputFile.toPath(), co);

                    symbolicNameToURIMap.put(fileName, outputFile.toURI().toASCIIString());
                    refreshFiles();
                } catch (IOException e) {
                    Notification.show("Failed to upload file.", e.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        }, new UploadStateWindow());
        upload.setImmediate(true);
        upload.setUploadButtonCaptions("Upload file", "Upload files");

        mainLayout.addComponent(upload);
        mainLayout.addComponent(panel);
        mainLayout.setExpandRatio(panel, 1);

        setCompositionRoot(mainLayout);
    }

    private void refreshFiles() {
        filesLayout.removeAllComponents();
        HorizontalLayout layout;
        for (String fileName : symbolicNameToURIMap.keySet()) {
            layout = new HorizontalLayout();
            layout.setSpacing(true);
            Button removeButton = new Button("X");
            layout.addComponent(removeButton);
            layout.addComponent(new Label(fileName));
            if (!fileExists(fileName)) {
                layout.addComponent(new Label("<b style=\"color:red;\">"
                        + "File doesn't exist.</b>", ContentMode.HTML));
            }
            removeButton.addClickListener(new RemoveFileClickListener(fileName));
            filesLayout.addComponent(layout);
        }
    }

    private boolean fileExists(String fileName) {
        if (new File(URI.create(symbolicNameToURIMap.get(fileName))).exists()) {
            return true;
        }
        return false;
    }

    private class RemoveFileClickListener implements Button.ClickListener {
        private static final long serialVersionUID = 3088780607157858375L;

        private String fileName = null;

        public RemoveFileClickListener(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void buttonClick(ClickEvent event) {
            symbolicNameToURIMap.remove(fileName);
            refreshFiles();
        }

    }

    @Override
    public void setConfiguration(UploadToFilesConfig_V1 conf) throws DPUConfigException {
        symbolicNameToURIMap.clear();
        symbolicNameToURIMap.putAll(conf.getSymbolicNameToURIMap());

        refreshFiles();
    }

    @Override
    public UploadToFilesConfig_V1 getConfiguration() throws DPUConfigException {

        UploadToFilesConfig_V1 conf = new UploadToFilesConfig_V1();
        conf.setSymbolicNameToURIMap(symbolicNameToURIMap);

        return conf;
    }
}
