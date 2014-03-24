package mpg.biochem.de.interbase.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.util.ExecutionContextUserSupport;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class PSICQUICReader extends ExecutionContextUserSupport implements ItemReader, ItemStream, InitializingBean{

	private static Log log = LogFactory.getLog(PSICQUICReader.class);
	
	private String psicquicURL;
	
	public PSICQUICReader(){
		setName(ClassUtils.getShortName(PSICQUICReader.class));
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		Assert.state(psicquicURL != null, "psicquicURL must not be null");
		
		log.debug("Opening: " + psicquicURL);
	}

	@Override
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws ItemStreamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		// TODO Auto-generated method stub
		return null;
	}

}
