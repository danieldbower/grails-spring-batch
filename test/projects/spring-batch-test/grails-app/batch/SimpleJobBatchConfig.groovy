import springbatchtest.PrintMessageTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'simpleJob') {
        batch.step(id: 'jobStart', next:'jobEnd') {
            batch.tasklet(ref: 'printStartMessage')
        }
        batch.step(id: 'jobEnd' ) {
            batch.tasklet(ref: 'printEndMessage')
        }
    }

    /*
     * Steps are made available to all jobs, they are shared in the same spring context and can be
     * reused if the same bean name is used
     */
    printStartMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Starting Job'
    }

    printEndMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Finishing Job'
    }
}