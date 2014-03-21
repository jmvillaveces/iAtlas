package mpg.biochem.de.interbase.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration({"/test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class UniProtIndexTest {

	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private Job job;
	
	private String[] F4BT08 = new String[]{"F4BT08_METCG", "10461048", "YP_004383936.1"};
	
	@Test
	public void testSimpleProperties() throws Exception {
		assertNotNull(jobLauncher);
		assertNotNull(job);
	}
	
	@Test
	public void createIndex() throws Exception {
		UniProtIndex index = new UniProtIndex("data/mapping_index");
		System.out.println(index.search(F4BT08));
	}
}
