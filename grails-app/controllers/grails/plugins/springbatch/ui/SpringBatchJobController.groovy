package grails.plugins.springbatch.ui

import grails.converters.JSON

import org.springframework.batch.core.JobParameters

class SpringBatchJobController {

    def springBatchUiService
    def springBatchService
	def grailsApplication
	
    static defaultAction = 'list'

	def status() {
		if(!params.job) {
			render ([success:false, message:'No job submitted for status check']) as JSON
			return
		}
		
		Map statuses = [:]
		
		if(params.job instanceof String) {
			String job = params.job
			statuses.put( job, springBatchService.jobStatus(job))
		}else if(params.job instanceof String[]) {
			params.job.each{ String job ->
				statuses.put( job, springBatchService.jobStatus(job))
			}
		}else {
			render ([success:false, message:"Class: ${params.job.class.name}"]) as JSON
			return
		}
		
		render ([success:true, data:statuses]) as JSON
	}
	
    def list() {
		if(!params.max){
			params.max = grailsApplication.config.plugin?.springBatch?.jobListSize ?: 10
		}
        [modelInstances : springBatchUiService.getJobModels(params),
			ready: springBatchService.ready]
    }
	
	def show(String id) {
		if(id) {
			[job: springBatchUiService.jobModel(id),
				jobModelInstances: springBatchUiService.getJobInstanceModels(id, params)]
		} else {
			flash.error = "Please supply a job name"
            redirect(mapping:'batch', controller: "springBatchJob", action: "list")
		}
	}

	def launch(String id){
		JobParameters jobParams = springBatchUiService.buildJobParametersFromRequest(params)
		
		boolean canBeConcurrent = params.canBeConcurrent ? 
				params.canBeConcurrent.toBoolean() : true
		
		Map result = springBatchService.launch(id, canBeConcurrent, jobParams, 
				params.jobLauncherName)

		if(result.success){
			flash.message = result.message
		}else{
			flash.error = result.message
		}
		
		String action = (params.a=='l')?'list':'show'
		
		redirect(mapping:'batch', action:action, id:id)
	}
	
	def enableLaunching() {
		springBatchService.ready = true
		redirect mapping:'batch', action:'list'
	}
	
	def disableLaunching() {
		springBatchService.ready = false
		redirect mapping:'batch', action:'list'
	}

	def stopAllExecutions(String id) {
		if(id) {
			springBatchService.stopAllJobExecutions(id)
			
			flash.message = "Stopped all Job Executions for Job $id"
            redirect(mapping:'batch', action:'show', id:id)
		} else {
			springBatchService.stopAllJobExecutions()

			flash.message = 'Stopping all Job Executions for all Jobs'
            redirect(mapping:'batch', action:"list")
		}
	}
}
