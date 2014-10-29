package eu.unifiedviews.plugins.extractor.uploadtofiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
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
import eu.unifiedviews.plugins.extractor.uploadtofiles.OnDemandFileDownloader.OnDemandStreamResource;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class UploadToFilesVaadinDialog extends BaseConfigDialog<UploadToFilesConfig_V1> {
    private static final long serialVersionUID = 2397849673588724L;

    private static final Logger LOG = LoggerFactory.getLogger(UploadToFilesVaadinDialog.class);

    private Map<String, String> symbolicNameToURIMap = new HashMap<String, String>();

    private Map<String, String> symbolicNameToVirtualPathMap = new HashMap<String, String>();

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
                    symbolicNameToVirtualPathMap.put(fileName, fileName); // TODO
                    refreshFiles();
                } catch (IOException ex) {
                    Notification.show("Failed to upload file.", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    LOG.error("Failed to upload file.", ex);
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
        OnDemandStreamResource fileSource = null;

        for (String fileName : symbolicNameToURIMap.keySet()) {
            layout = new HorizontalLayout();
            layout.setSpacing(true);

            Button removeButton = new Button();
            removeButton.addStyleName("small_button");
            removeButton.setIcon(new ThemeResource("icons/trash.png"));
            removeButton.addClickListener(new RemoveFileClickListener(fileName));
            layout.addComponent(removeButton);

            fileSource = createSource(fileName);
            Button downloadButton = new Button();
            downloadButton.addStyleName("small_button");
            downloadButton.setIcon(new ThemeResource("icons/download.png"));
            new OnDemandFileDownloader(fileSource).extend(downloadButton);
            layout.addComponent(downloadButton);

            Label label = new Label(fileName);
            layout.addComponent(label);
            layout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);

            if (!fileExists(fileName)) {
                label = new Label("<b style=\"color:red;\">"
                        + "File doesn't exist.</b>", ContentMode.HTML);
                layout.addComponent(label);
                layout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
            }

            filesLayout.addComponent(layout);
        }
    }

    private OnDemandStreamResource createSource(final String fileName) {
        return new OnDemandStreamResource() {
            private static final long serialVersionUID = 3163461986720496196L;

            @Override
            public InputStream getStream() {
                String uri = symbolicNameToURIMap.get(fileName);
                File file = new File(URI.create(uri));
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    LOG.error("Failed to download file: " + fileName, e);
                    Notification.show("Failed to download file.", "Couldn't find file: " + fileName, Notification.Type.ERROR_MESSAGE);
                    return null;
                }
            }

            @Override
            public String getFilename() {
                return fileName;
            }
        };
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
        symbolicNameToVirtualPathMap.clear();
        symbolicNameToVirtualPathMap.putAll(conf.getSymbolicNameToVirtualPathMap());

        refreshFiles();
    }

    @Override
    public UploadToFilesConfig_V1 getConfiguration() throws DPUConfigException {

        UploadToFilesConfig_V1 conf = new UploadToFilesConfig_V1();
        conf.setSymbolicNameToURIMap(symbolicNameToURIMap);
        conf.setSymbolicNameToVirtualPathMap(symbolicNameToVirtualPathMap);

        return conf;
    }
}
