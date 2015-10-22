import springbatchtest.ThrowExceptionTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'alwaysFailJob') {
        batch.step(id: 'alwaysFailStep', next:'jobEnd') {
            batch.tasklet(ref: 'throwExceptionTasklet')
        }
        /*
         * Should never get to this next step.
         */
        batch.step(id: 'jobEnd' ) {
            batch.tasklet(ref: 'printEndMessage')
        }
    }

    throwExceptionTasklet(ThrowExceptionTasklet) { bean ->
        bean.autowire = "byName"
    }

}