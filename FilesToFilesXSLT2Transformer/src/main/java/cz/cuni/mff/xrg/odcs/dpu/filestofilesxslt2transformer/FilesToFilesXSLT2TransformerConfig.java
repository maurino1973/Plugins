package cz.cuni.mff.xrg.odcs.dpu.filestofilesxslt2transformer;

public class FilesToFilesXSLT2TransformerConfig {

    private String xslTemplate = "";

    private String xslTemplateFileNameShownInDialog = "";

    private boolean skipOnError = false;

    private String xlstParametersMapName = "xlstParameters";

    public FilesToFilesXSLT2TransformerConfig() {
    }

    public String getXslTemplate() {
        return xslTemplate;
    }

    public void setXslTemplate(String xslTemplate) {
        this.xslTemplate = xslTemplate;
    }

    public String getXslTemplateFileNameShownInDialog() {
        return xslTemplateFileNameShownInDialog;
    }

    public void setXslTemplateFileNameShownInDialog(
            String xslTemplateFileNameShownInDialog) {
        this.xslTemplateFileNameShownInDialog = xslTemplateFileNameShownInDialog;
    }

    public boolean isSkipOnError() {
        return skipOnError;
    }

    public void setSkipOnError(boolean skipOnError) {
        this.skipOnError = skipOnError;
    }

    public String getXlstParametersMapName() {
        return xlstParametersMapName;
    }

    public void setXlstParametersMapName(String xlstParametersMapName) {
        this.xlstParametersMapName = xlstParametersMapName;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[templateFile=" + xslTemplateFileNameShownInDialog + ",skipOnError=" + String.valueOf(skipOnError) + ",xsltParametersMapName=" + xlstParametersMapName + "]";
    }
}
