import grails.plugins.springbatch.springbatchadmin.patch.PatchedSimpleJobServiceFactoryBean
import grails.plugins.springbatch.ReloadableJobRegistryBeanPostProcessor
import groovy.sql.Sql

import java.sql.Connection
import java.sql.Statement

import org.springframework.batch.core.configuration.support.MapJobRegistry
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.core.launch.support.SimpleJobOperator
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.SyncTaskExecutor

import org.springframework.batch.core.repository.dao.AbstractJdbcBatchMetadataDao


class SpringBatchGrailsPlugin {
    def version = "2.5.0"
    def groupId = 'org.grails.plugins'
    def grailsVersion = "2.5 > *"
    def title = "Grails Spring Batch Plugin"
    def author = "John Engelman"
    def authorEmail = "john.r.engelman@gmail.com"
    def description = 'Adds the Spring Batch framework to application. Allows for job configuration using Spring Bean DSL. See documentation at https://github.com/johnrengelman/grails-spring-batch for details.'

    def documentation = "https://github.com/johnrengelman/grails-spring-batch"
    def license = "APACHE"
	def developers = [
		[name: "Daniel Bower", email: "daniel.bower@infinum.com"],
		]
    def issueManagement = [ system: "JIRA", url: "https://github.com/johnrengelman/grails-spring-batch/issues" ]
    def scm = [ url: "https://github.com/johnrengelman/grails-spring-batch" ]

    //From Platform Core
    def doWithConfigOptions = {
        //'dataSource'(type: String, defaultValue: "dataSource")
        //'tablePrefix'(type: String, defaultValue: "BATCH")
        //'maxVarCharLength'(type: Integer, defaultValue: AbstractJdbcBatchMetadataDao.DEFAULT_EXIT_MESSAGE_LENGTH)
        //'loadTables'(type: Boolean, defaultValue: false)
        'database'(type: String, defaultValue: 'h2', validator: { v ->
            v ? null : 'batch.specify.database.type'
        })
    }

    def doWithSpring = {
        def conf = application.config.plugin.springBatch

        conf.dataSource = conf.dataSource ?: "dataSource"
        conf.tablePrefix = conf.tablePrefix ?: "BATCH"
        conf.maxVarCharLength = conf.maxVarCharLength ?: AbstractJdbcBatchMetadataDao.DEFAULT_EXIT_MESSAGE_LENGTH
        conf.loadTables = conf.loadTables ?: false
        conf.database = conf.database ?: 'h2'

        String tablePrefix = conf.tablePrefix ? (conf.tablePrefix + '_' ) : ''
        def dataSourceBean = conf.dataSource
		def maxVarCharLength = conf.maxVarCharLength

        String tablePrefixVal = tablePrefix
        String dbType = conf.database
        int maxVarCharLengthVal = maxVarCharLength

		jobRepository(JobRepositoryFactoryBean) {
            dataSource = ref(dataSourceBean)
            transactionManager = ref("transactionManager")
            tablePrefix = tablePrefixVal
            databaseType = dbType
            maxVarCharLength = maxVarCharLengthVal
            //isolationLevelForCreate = "SERIALIZABLE"
        }

		/*
		 * Async launcher to use by default
		 */
        jobLauncher(SimpleJobLauncher){
            jobRepository = ref("jobRepository")
            taskExecutor = { SimpleAsyncTaskExecutor executor -> }
        }

		/*
		 * Additional Job Launcher to support synchronous scheduling
		 */
		syncJobLauncher(SimpleJobLauncher){
			jobRepository = ref("jobRepository")
			taskExecutor = { SyncTaskExecutor executor -> }
		}

        jobExplorer(JobExplorerFactoryBean) {
            dataSource = ref(dataSourceBean)
            tablePrefix = tablePrefixVal
        }
        jobRegistry(MapJobRegistry) { }
        //Use a custom bean post processor that will unregister the job bean before trying to initializing it again
        //This could cause some problems if you define a job more than once, you'll probably end up with 1 copy
        //of the last definition processed instead of getting a DuplicateJobException
        //Had to do this to get reloading to work
        jobRegistryPostProcessor(ReloadableJobRegistryBeanPostProcessor) {
            jobRegistry = ref("jobRegistry")
        }

        jobOperator(SimpleJobOperator) {
            jobRepository = ref("jobRepository")
            jobLauncher = ref("jobLauncher")
            jobRegistry = ref("jobRegistry")
            jobExplorer = ref("jobExplorer")
        }
        jobService(PatchedSimpleJobServiceFactoryBean) {
            jobRepository = ref("jobRepository")
            jobLauncher = ref("jobLauncher")
            jobLocator = ref("jobRegistry")
            dataSource = ref(dataSourceBean)
            tablePrefix = tablePrefixVal
        }

        def loadConfig = loadBatchConfig.clone()
        loadConfig.delegate = delegate
        xmlns(batch:"http://www.springframework.org/schema/batch")
        loadConfig()
    }

    def doWithApplicationContext = { applicationContext ->
        def conf = application.config.plugin.springBatch
        String dataSourceName = conf.dataSource
        def database = conf.database
        def loadTables = conf.loadTables
        if(loadTables) {
            if(database) {
                def ds = applicationContext.getBean(dataSourceName)
                def sql = new Sql(ds)
                sql.withTransaction { Connection conn ->
                    Statement statement = conn.createStatement()
                    def script = "org/springframework/batch/core/schema-drop-${database}.sql"
                    def text = applicationContext.classLoader.getResourceAsStream(script).text
                    text.split(";").each { line ->
                        if(line.trim()) {
                            statement.execute(line.trim())
                        }
                    }

                    script = "org/springframework/batch/core/schema-${database}.sql"
                    text = applicationContext.classLoader.getResourceAsStream(script).text
                    text.split(";").each { line ->
                        if(line.trim()) {
                            statement.execute(line.trim())
                        }
                    }
                    statement.close()
                    conn.commit()
                }
                sql.close()
            } else {
                log.error("Must specify plugin.springBatch.database variable if plugin.springBatch.loadTables = true")
                throw new RuntimeException("Must specify plugin.springBatch.database variable if plugin.springBatch.loadTables = true")
            }
        }
    }

    def loadBatchConfig = { ->
        loadBeans 'classpath*:/batch/*BatchConfig.groovy'
    }
}
