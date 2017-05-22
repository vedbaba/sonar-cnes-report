import fr.cnes.sonar.report.model.*;
import fr.cnes.sonar.report.params.Params;
import fr.cnes.sonar.report.providers.IssuesProvider;
import fr.cnes.sonar.report.providers.MeasureProvider;
import fr.cnes.sonar.report.providers.QualityGateProvider;
import fr.cnes.sonar.report.providers.QualityProfileProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Test the creation of a docx file from a template
 * @author garconb
 */
public class DataProviderTest extends MasterTest {

    @Test
    public void getAllQualityGateTest() throws Exception {
        QualityGateProvider qgp = new QualityGateProvider(params);
        List<QualityGate> qgates = qgp.getQualityGates();
        for (QualityGate qg: qgates) {
            System.out.println(qg.toString());
        }
    }

    @Test
    public void getProjectQualityGateTest() throws Exception {
        QualityGateProvider qgp = new QualityGateProvider(params);
        QualityGate qgate = qgp.getProjectQualityGate();
        System.out.println(qgate.toString());
    }

    @Test
    public void getAllQualityProfileTest() throws Exception {
        QualityProfileProvider profileProvider = new QualityProfileProvider(params);
        List<QualityProfile> qualityProfileList = profileProvider.getQualityProfiles();
        for (QualityProfile qualityProfile: qualityProfileList) {
            System.out.println(qualityProfile.toString());
        }
    }

    @Test
    public void getProjectQualityProfileTest() throws Exception {
        QualityProfileProvider profileProvider = new QualityProfileProvider(params);
        QualityProfile qualityProfile = profileProvider.getProjectQualityProfile();
        System.out.println(qualityProfile.toString());
    }

    @Test
    public void getMeasureTest() throws Exception {
        MeasureProvider measureProvider = new MeasureProvider(params);
        List<Measure> measureList = measureProvider.getMeasures();
        for (Measure measure: measureList) {
            System.out.println(measure.toString());
        }
    }

    @Test
    public void getIssuesTest() throws Exception {
        IssuesProvider issuesProvider = new IssuesProvider(params);
        List<Issue> issueList = issuesProvider.getIssues();
        for (Issue issue: issueList) {
            System.out.println(issue.toString());
        }
    }

    @Test
    public void getFacetsTest() throws Exception {
        IssuesProvider issuesProvider = new IssuesProvider(params);
        List<Facet> facetList = issuesProvider.getFacets();
        for (Facet facet: facetList) {
            System.out.println(facet.toString());
        }
    }

}