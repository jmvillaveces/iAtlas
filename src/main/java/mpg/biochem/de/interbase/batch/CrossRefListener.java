package mpg.biochem.de.interbase.batch;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.CrossReferenceImpl;

public class CrossRefListener implements ItemReadListener<BinaryInteraction> {

	
	private String source;
	
	public CrossRefListener(){
	}
	
	public void setSource(String source){
		this.source = source;
	}
	
	@Override
	public void afterRead(BinaryInteraction interaction) {
		
		if(interaction != null){
			CrossReference cr = new CrossReferenceImpl();
			cr.setDatabase("iAtlas");
			cr.setIdentifier(source);
			interaction.getSourceDatabases().add(cr);
		}
	}

	@Override
	public void beforeRead() {}

	@Override
	public void onReadError(Exception ex) {}
}
