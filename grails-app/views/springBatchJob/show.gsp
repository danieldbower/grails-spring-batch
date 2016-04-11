<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="batch.job.show.label" args="[job.name]"/></title>

		<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
		<script type="text/javascript">
			google.charts.load('current', {packages: ['corechart']});
			google.charts.setOnLoadCallback(drawChart);

			function drawChart() {
				var data = google.visualization.arrayToDataTable([
					[ {label: 'Time of Run', id: 'timeOfRun', type: 'string'},
						{label: 'Duration', id: 'duration', type: 'number'} ],
						<g:applyCodec encodeAs="none">
						<%= durationReport.durations.collect{"['" + it.startTime + "',"+ it.duration + "]"}?.join(',') %>
						</g:applyCodec>
					]);

				var options = {
					title: 'Job Execution Duration over Time',
					hAxis: {title: 'Time of Job Execution'},
					vAxis: {title: 'Minutes'},
					legend: 'none',
					trendlines: {0: {}}    // Draw a trendline for data series 0.
				};

				var chart = new google.visualization.ScatterChart(document.getElementById('durationReportChart'));
				chart.draw(data, options);
			}

		</script>

	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link mapping="batch" action="list" >List</g:link></li>
				<g:if test="${job.launchable}"><li><g:link  mapping="batch" action="launch" id="${job.name}">
					<g:message code="batch.job.launch.label"/></g:link></li></g:if>
				<li><g:link mapping="batch" action="stopAllExecutions" id="${job.name}">
					<g:message code="batch.job.stopall.label"/></g:link></li>
			</ul>
		</div>
		<div id="show-jobModel" class="content scaffold-show" role="main">
			<h1><g:message code="batch.job.show.label" args="[job.name]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:if test="${flash.error}">
			<div class="errors" role="status">${flash.error}</div>
			</g:if>

			<ol class="property-list example">
				<li class="fieldcontain">
					<span id="jobInstanceCount-label" class="property-label"><g:message code="batch.job.jobInstanceCount.label"/></span>
						<span class="property-value" aria-labelledby="jobInstanceCount-label">${job.jobInstanceCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="executionCount-label" class="property-label"><g:message code="batch.job.executionCount.label"/></span>
						<span class="property-value" aria-labelledby="executionCount-label">${job.executionCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="incrementable-label" class="property-label"><g:message code="batch.job.incrementable.label"/></span>
						<span class="property-value" aria-labelledby="incrementable-label">${job.incrementable}</span>
				</li>
				<li class="fieldcontain">
					<span id="currentlyRunning-label" class="property-label"><g:message code="batch.job.currentlyRunning.label"/></span>
						<span class="property-value" aria-labelledby="currentlyRunning-label">${job.currentlyRunning}</span>
				</li>
				<li class="fieldcontain">
					<span id="steps-label" class="property-label"><g:message code="batch.job.steps.label"/></span>
					<span class="property-value" aria-labelledby="currentlyRunning-label">${job.stepNames.join(', ')}</span>
				</li>
				<g:if test="${job.mostRecentJobExecution}">
				<li class="fieldcontain">
					<span id="mostRecentExecution-label" class="property-label"><g:message code="batch.job.mostRecentJobExecution.label"/></span>
					<g:link mapping="batch" controller="springBatchJobExecution" action="show" id="${job.mostRecentJobExecution.id}">
						<g:message code="batch.jobExecution.startDateTime.label"/> - 
							<span class="property-value" aria-labelledby="startDateTime-label">${job.mostRecentJobExecution.startDateTime}</span>    
						<g:message code="batch.jobExecution.duration.label"/> - 
							<span class="property-value" aria-labelledby="duration-label">
								<batch:durationPrint duration="${job.mostRecentJobExecution.duration}"/></span>    
						<g:message code="batch.jobExecution.status.label"/> - 
							<span class="property-value" aria-labelledby="status-label">${job.mostRecentJobExecution.status}</span>    
						<g:message code="batch.jobExecution.exitStatus.label"/> - 
							<span class="property-value" aria-labelledby="exitStatus-label">${job.mostRecentJobExecution.exitStatus.exitCode}</span>
					</g:link>
				</li>
				</g:if>
				
			</ol>
			
			<h2><g:message code="batch.job.instances.label"/></h2>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="id" title="${message(code: 'batch.jobInstance.id.label')}" />
						<g:sortableColumn property="jobExecutionCount" title="${message(code: 'batch.jobInstance.jobExecutionCount.label')}" />
						<g:sortableColumn property="lastJobExecutionStatus" title="${message(code: 'batch.jobInstance.lastJobExecutionStatus.label')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${jobModelInstances}" status="i" var="jobInstanceModelInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link mapping="batch" controller="springBatchJobInstance" action="show" id="${jobInstanceModelInstance.id}" params="[jobName: job.name]">${fieldValue(bean: jobInstanceModelInstance, field: "id")}</g:link></td>
						<td>${fieldValue(bean: jobInstanceModelInstance, field: "jobExecutionCount")}</td>
						<td>${fieldValue(bean: jobInstanceModelInstance, field: "lastJobExecutionStatus")}</td>
					</tr>
				</g:each>
				</tbody>
			</table>

			<div>
				<div id="durationReportChart" style="width: 900px; height: 600px;"></div>
				<p>Chart Size: <g:link action="show" params="[id:id, durationReportSize:(durationReportSize + 50)]">+50</g:link> /
				<g:link action="show" params="[id:id, durationReportSize:(durationReportSize - 50)]">-50</g:link></p>
			</div>

			<div class="pagination">
				<g:paginate total="${jobModelInstances.resultsTotalCount}" id="$job.name" />
			</div>
		</div>
	</body>
</html>
