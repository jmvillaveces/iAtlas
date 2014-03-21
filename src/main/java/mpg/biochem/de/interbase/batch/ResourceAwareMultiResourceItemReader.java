package mpg.biochem.de.interbase.batch;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.MultiResourceItemReader;

public class ResourceAwareMultiResourceItemReader extends MultiResourceItemReader {

	private ExecutionContext executionContext;
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		this.executionContext = executionContext;
		super.open(executionContext);
	}
	
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		this.executionContext = executionContext;
		super.update(executionContext);
	}

	@Override
	public Object read() throws Exception, UnexpectedInputException, ParseException {
		
		Object obj = super.read();
		
		if(super.getCurrentResource() != null){
			executionContext.put("current.resource.name", super.getCurrentResource().getFilename());
			System.out.println(super.getCurrentResource().getFilename());
		}
		return obj;
	}
}
