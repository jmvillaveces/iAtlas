package mpg.biochem.de.interbase.task;

import mpg.biochem.de.interbase.util.PushOverNotificationManager;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class PushOverMsgTask implements Tasklet {

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		//TODO if pushover allowed
		PushOverNotificationManager.sendNotification("Finished iAtlas job");
		return RepeatStatus.FINISHED;
	}
}
