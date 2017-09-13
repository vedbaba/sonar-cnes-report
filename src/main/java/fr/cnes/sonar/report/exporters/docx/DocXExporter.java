/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report.exporters.docx;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.exporters.IExporter;
import fr.cnes.sonar.report.input.Params;
import fr.cnes.sonar.report.input.StringManager;
import fr.cnes.sonar.report.model.Report;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Exports the report in .docx format
 * @author lequal
 */
public class DocXExporter implements IExporter {

    /**
     *  Name of the property containing the destination path of the report
     */
    private static final String REPORT_TEMPLATE = "report.template";
    /**
     * Placeholder for the table containing detailed issues
     */
    private static final String DETAILS_TABLE_PLACEHOLDER = "$ISSUES_DETAILS";
    /**
     * Placeholder for the table containing counts of issues by type and severity
     */
    private static final String COUNT_TABLE_PLACEHOLDER = "$ISSUES_COUNT";
    /**
     * Placeholder for the table containing counts of issues by type and severity
     */
    private static final String VOLUME_TABLE_PLACEHOLDER = "$VOLUME";
    /**
     * Just a slash
     */
    private static final String SLASH = "/";
    /**
     * Name of the property giving the path to the report
     */
    private static final String REPORT_PATH_PROPERTY_KEY = "report.path";
    /**
     * Name of the property giving the path header's number
     */
    private static final String HEADER_NUMBER = "header.number";
    /**
     * Name of the columns in issues table
     */
    private static final String[] HEADER_FIELDS = {StringManager.string("header.name"),
            StringManager.string("header.description"),
            StringManager.string("header.type"),
            StringManager.string("header.severity"),
            StringManager.string(HEADER_NUMBER)};
    /**
     * Name of the columns in volumes table
     */
    private static final String[] VOLUMES_HEADER = {StringManager.string("header.language"),
            StringManager.string(HEADER_NUMBER)};
    /**
     * Start index of the sub array in the headers array for the the second table
     */
    private static final int HEADER_START_INDEX = 2;
    /**
     * End index of the sub array in the headers array for the the second table
     */
    private static final int HEADER_END_INDEX = 5;

    /**
     * Overridden export for docX
     * @param data Data to export as Report
     * @param params Program's parameters
     * @param path Path where to export the file
     * @param filename Name of the file to export
     * @throws BadExportationDataTypeException Data has not the good type
     * @throws UnknownParameterException report.path is not set
     * @throws OpenXML4JException ...
     * @throws IOException ...
     * @throws XmlException ...
     */
    @Override
    public void export(Object data, Params params, String path, String filename)
            throws BadExportationDataTypeException, UnknownParameterException,
            OpenXML4JException, IOException, XmlException {
        // check resources type
        if (!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        // resources casting
        final Report report = (Report) data;

        // open excel file from the path given in the parameters
        final File file = new File(params.get(REPORT_TEMPLATE));
        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            OPCPackage opcPackage = OPCPackage.open(fileInputStream);
            XWPFDocument document = new XWPFDocument(opcPackage)
        ) {

            // Fill charts
            DocXTools.fillCharts(opcPackage, document,report.getFacets());

            // Add issues
            final List<List<String>> issues = DataAdapter.getIssues(report);
            final String[] issuesArrayFr = HEADER_FIELDS;
            final List<String> headerIssues = new ArrayList<>(Arrays.asList(issuesArrayFr));
            DocXTools.fillTable(document, headerIssues, issues, DETAILS_TABLE_PLACEHOLDER);

            // Add issues count by type and severity
            final List<List<String>> types = DataAdapter.getTypes(report);
            DocXTools.fillTable(document,
                    headerIssues.subList(HEADER_START_INDEX, HEADER_END_INDEX),
                    types, COUNT_TABLE_PLACEHOLDER);

            // Add volumes by language
            final List<String> volumesHeader = new ArrayList<>(Arrays.asList(VOLUMES_HEADER));
            final List<List<String>> volumes = DataAdapter.getVolumes(report);
            DocXTools.fillTable(document, volumesHeader, volumes, VOLUME_TABLE_PLACEHOLDER);

            // Map which contains all values to replace
            // the key is the placeholder and the value is the value to write over
            final Map<String, String> replacementValues = DataAdapter.loadPlaceholdersMap(report);

            // replace all placeholder in the document (head, body, foot) with the map
            DocXTools.replacePlaceholder(document, replacementValues);

            // Save the result by creating a new file in the directory given by report.path property
            final String outputName = params.get(REPORT_PATH_PROPERTY_KEY) + SLASH + filename;
            final FileOutputStream out = new FileOutputStream(outputName);
            // close open resources
            document.write(out);
            out.close();
            document.close();
        }
    }


}
