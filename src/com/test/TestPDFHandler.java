package com.test;

import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.export.GenericElementHandler;
import net.sf.jasperreports.engine.export.GenericElementHandlerBundle;
import net.sf.jasperreports.engine.export.GenericElementPdfHandler;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterContext;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.SingletonExtensionRegistry;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfAction;

public class TestPDFHandler implements GenericElementPdfHandler, GenericElementHandlerBundle, ExtensionsRegistryFactory {

    @Override
    public boolean toExport(JRGenericPrintElement element) {
        return true;
    }

    @Override
    public void exportElement(JRPdfExporterContext exporterContext,
            JRGenericPrintElement element) {
        Phrase phrase = new Phrase();
        Chunk chunk = new Chunk("print me");
        chunk.setAction(PdfAction.javaScript("this.print(null,this.pageNum,this.pageNum)", exporterContext.getPdfWriter()));
        phrase.add(chunk);

        ColumnText colText = new ColumnText(exporterContext.getPdfWriter().getDirectContent());
        colText.setSimpleColumn(phrase,
                element.getX(),
                exporterContext.getExportedReport().getPageHeight() - element.getY(),
                element.getX() + element.getWidth(),
                exporterContext.getExportedReport().getPageHeight() - element.getY() - element.getHeight(),
                0,
                Element.ALIGN_LEFT);

        try {
            colText.go();
        } catch (DocumentException e) {
            throw new JRRuntimeException(e);
        }

    }

    @Override
    public String getNamespace() {
        return "http://jasperreports.sourceforge.net/jasperreports/customPDFexpoter";
    }

    @Override
    public GenericElementHandler getHandler(String elementName, String exporterKey) {
        if (elementName.equals("print") && exporterKey.equals(JRPdfExporter.PDF_EXPORTER_KEY)) {
            return this;
        }

        return null;
    }

    @Override
    public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties) {
        return new SingletonExtensionRegistry<GenericElementHandlerBundle>(GenericElementHandlerBundle.class, this);
    }
}
