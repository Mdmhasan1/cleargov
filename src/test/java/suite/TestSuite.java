package suite;

import courgette.api.CourgetteOptions;
import courgette.api.CourgetteRunLevel;
import courgette.api.testng.TestNGCourgette;
import cucumber.api.CucumberOptions;
import org.testng.annotations.Test;

@Test
@CourgetteOptions(
        threads = 10,
        runLevel = CourgetteRunLevel.SCENARIO,
        showTestOutput = true,
        rerunFailedScenarios = true,
        reportTargetDir = "build",
        cucumberOptions = @CucumberOptions(
                features = "src/test/resources/features",
                glue = "stepDefinitions",
                tags = {"@mpt and not @mobileOnly"},
                monochrome = true,
                plugin = {
                        "pretty",
                        "json:build/cucumber-report/cucumber.json",
                        "html:build/cucumber-report/cucumber.html"},
                strict = true
        ))

public class TestSuite extends TestNGCourgette {

}