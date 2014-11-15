grails.project.work.dir = 'target'
grails.project.target.level = 1.7
grails.project.source.level = 1.7

//Upgrading to SB 2.2.0.RELEASE will require a dependency on Grails 2.1.1
//This is because SB 2.2.0.RELEASE requires Spring 3.1.2.RELEASE which was introduced in
//Grails 2.1.1
springBatchVersion = '3.0.2.RELEASE'   //'2.1.9.RELEASE'
springBatchAdminVersion = '1.3.0.RELEASE' //'1.2.2.RELEASE'

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {

        def excludes = {
            excludes 'junit', 'spring-core', 'spring-oxm', 'spring-test', 'spring-tx', 'slf4j-log4j12', 'log4j'
        }

        compile "org.springframework.batch:spring-batch-core:${springBatchVersion}",
                "org.springframework.batch:spring-batch-infrastructure:${springBatchVersion}",
                "org.springframework.batch:spring-batch-admin-resources:${springBatchAdminVersion}",
                "org.springframework.batch:spring-batch-admin-manager:${springBatchAdminVersion}",
               excludes

        test "org.springframework.batch:spring-batch-test:${springBatchVersion}", excludes
    }

    plugins {
         build(":release:3.0.1", ":rest-client-builder:1.0.3") {
            export = false
        }

        compile ':platform-core:1.0.0'

        runtime(":hibernate4:latest.release") { //4.3.5.4
            export = false
        }
		
		provided(":codenarc:0.20"){
			exclude "junit"
		}
    }
}

codenarc.ruleSetFiles="file:grails-app/conf/GrailsSpringBatchCodeNarcRules.groovy"
codenarc.processTestUnit=false
codenarc.processTestIntegration=false
codenarc.reports = {
	xmlReport('xml') {
		outputFile = 'target/CodeNarc-Report.xml'
	}
	htmlReport('html') {
		outputFile = 'target/CodeNarc-Report.html'
	}
}
