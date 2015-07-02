import springbatchtest.PrintMessageTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'simpleJob3') {
        batch.step(id: 'jobStart', next:'jobEnd') {
            batch.tasklet(ref: 'printStartMessage')
        }
        batch.step(id: 'jobEnd' ) {
            batch.tasklet(ref: 'printEndMessage')
        }
    }
}