package grails.plugins.springbatch.ui

import org.springframework.batch.core.JobExecutionException

class SpringBatchJobExecutionController {

    def springBatchUiService
	def springBatchService

    static defaultAction = 'show'
	
	def show(Long id) {
		if(id) {
			if(!params.max) {
				params.max = grailsApplication.config.plugin?.springBatch?.stepListSize ?: 10
			}

			[jobExecution: springBatchUiService.jobExecutionModel(id),
				modelInstances: springBatchUiService.getStepExecutionModels(id, params)]
		} else {
			flash.error = "Please supply a job execution id"
            redirect(mapping:'batch', controller: "springBatchJob", action: "list")
		}
	}

	def restart(Long id) {
		if(id) {
			try{
				springBatchService.restart(id)
				flash.message = "Restarted Job Execution"
			}catch (JobExecutionException jee){
				flash.error = jee.message
			}
            redirect(mapping:'batch', action: "show", id:id)
		} else {
			flash.error = "Please supply a job execution id"
            redirect(mapping:'batch', controller: "springBatchJob", action: "list")
		}
	}

	def stop(Long id) {
		if(id) {
			try{
				springBatchService.stop(id)
				flash.message = "Stopped Job Execution"
			}catch (JobExecutionException jee){
				flash.error = jee.message
			}
            redirect(mapping:'batch', action: "show", id:id)
		} else {
			flash.error = "Please supply a job execution id"
            redirect(mapping:'batch', controller: "springBatchJob", action: "list")
		}
	}
}
