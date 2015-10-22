package springbatchtest

import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext

class ThrowExceptionTasklet implements Tasklet {
	
    RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        throw new RuntimeException("This is a runtime error")
    }
}
