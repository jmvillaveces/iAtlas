package mpg.biochem.de.interbase.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.builder.MitabWriterUtils;
import psidev.psi.mi.tab.model.builder.PsimiTabVersion;

public class PSIMITabWriter implements ItemStreamWriter<BinaryInteraction>{

	private String filePath;
	private FileOutputStream fop;
	
	public PSIMITabWriter(String filePath){
		this.filePath = filePath;
	}
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		try{
			/* Save to file */
		    File file = new File(filePath);
			fop = new FileOutputStream(file);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		
	}

	@Override
	public void close() throws ItemStreamException {
		try {
			fop.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(List<? extends BinaryInteraction> items) throws Exception {
		for(BinaryInteraction bi : items){
			String intStr = MitabWriterUtils.buildLine(bi, PsimiTabVersion.v2_5);
			fop.write(intStr.getBytes());
	    }
	    fop.flush();
	}
}
