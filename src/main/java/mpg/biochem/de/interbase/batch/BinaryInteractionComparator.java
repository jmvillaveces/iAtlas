package mpg.biochem.de.interbase.batch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import mpg.biochem.de.interbase.util.Util;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.Interactor;

public class BinaryInteractionComparator implements Comparator<BinaryInteraction> {

	@Override
	public int compare(BinaryInteraction interactionA, BinaryInteraction interactionB) {
		String idA = getInteractionIdPair(interactionA), idB = getInteractionIdPair(interactionB);
		return idA.compareTo(idB);
	}
	
	private static String getInteractionIdPair(BinaryInteraction interaction){
		
		String intAId="";
		if(interaction.getInteractorA() != null){
			intAId = interaction.getInteractorA().getIdentifiers().get(0).getIdentifier();
		}
		
		String intBId="";
		if(interaction.getInteractorB() != null){
			intBId = interaction.getInteractorB().getIdentifiers().get(0).getIdentifier();
		}
		
		if(intAId.equals("")){
			intAId = intBId;
		}else if(intBId.equals("")){
			intBId = intAId;
		}
		
		int c = intAId.compareToIgnoreCase(intBId);
		if(c > 0)
			return intBId+"_"+intAId;
			
		return intAId+"_"+intBId;
	}
}
