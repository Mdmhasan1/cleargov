package framework;

import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;
import org.testng.collections.Maps;
import pageObjects.allTemplates.BasePage;

import java.util.Arrays;
import java.util.Map;

/**
 * Custom soft assertion.
 */
public class SoftAssertion extends Assertion {
    private Map<AssertionError, IAssert> m_errors = Maps.newLinkedHashMap();
    private BasePage page = new BasePage();
    public String method;

    @Override
    public void doAssert(IAssert a) {
        onBeforeAssert(a);
        try {
            a.doAssert();
        } catch (AssertionError ex) {
            //took this off because of memory overload
            //String screenshotName = method + DateUtils.getCurrentDate(DatePatterns.MM_dd_HH_mm_ss) + ".png";
            //page.takeScreenshot();
            onAssertFailure(a, ex);
            m_errors.put(ex, a);
        }
    }

    public void assertAll() {
        if (!m_errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("The following asserts failed:\n\t");
            for (Map.Entry<AssertionError, IAssert> ae : m_errors.entrySet()) {
                sb.append("\n\t");
                sb.append(ae.getKey().getMessage()+"\n");
                sb.append("\n\t");
            }
            sb.append("Full stack traces:\n\t ");
            for (Map.Entry<AssertionError, IAssert> ae : m_errors.entrySet()) {
                sb.append("\n\t");
                sb.append(ae.getKey().getMessage()+"\n");
                sb.append("\nSTACK TRACE FOR FAILED ASSERT :\n");
                sb.append(Arrays.toString(ae.getKey().getStackTrace()).replaceAll(",", "\n") + "\n");
            }
            throw new AssertionError(sb.toString());
        }
    }

    public void resetAssertMap() {
        m_errors = null;
    }
}


