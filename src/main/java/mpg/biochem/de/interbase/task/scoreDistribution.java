package mpg.biochem.de.interbase.task;

import uk.ac.ebi.enfin.mi.score.distribution.MiscoreDistribution;
import uk.ac.ebi.enfin.mi.score.distribution.MiscoreDistributionImp;

public class scoreDistribution {
	
	public void score(){
		 MiscoreDistribution mD = new MiscoreDistributionImp("*:*","MatrixDB");
	     mD.createChart();
	     mD.saveScores();
	}
	
}
