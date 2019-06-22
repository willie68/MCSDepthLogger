import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTimeFormat {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    Date date = new Date();
    DateFormat format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM,
        Locale.getDefault());
    System.out.println(format.format(date));
    // fail("Not yet implemented");
  }

}
