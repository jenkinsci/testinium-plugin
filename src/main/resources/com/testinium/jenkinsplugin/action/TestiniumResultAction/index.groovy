package com.testinium.jenkinsplugin.action.TestiniumResultAction

import com.testinium.jenkinsplugin.configuration.TestiniumPluginConfiguration
import com.testinium.jenkinsplugin.model.ExecutionResult
import com.testinium.jenkinsplugin.service.model.TestResult
import com.testinium.jenkinsplugin.util.CustomTimeDuration
import groovy.time.TimeCategory
import lib.LayoutTagLib

import java.text.SimpleDateFormat

st = namespace("jelly:stapler")
l = namespace(LayoutTagLib.class)

l.layout(title: "${my.getDisplayName()}") {
    def build = my.owningRun
    if (build != null) {
        st.include(page: 'sidepanel', it: build, optional: true)
    }

    l.main_panel {
        createHeader()
        createTable()
    }
}

private void createHeader() {
    link(rel: 'stylesheet', href: my.getResource('css/testiniumResultAction.css'))
    div(class: 'header') {
        img(class: 'logo', src: "${my.getResource('icons/testinium-logo.png')}")
        h1 { raw("Execution ID: ${my.getExecution().id}") }
        if (my.getExecution().getPlan() != null) {
            br()
            h1 { raw("Plan ID: ${my.getExecution().getPlan().getPlanName()}") }
        }
    }
}

private void createTable() {
    table(class: 'test-table') {
        thead {
            tr {
                th(class: 'id') { raw(_("ID_COLUMN")) }
                th(class: 'status') { raw(_("STATUS_COLUMN")) }
                th(class: 'run-date') { raw(_("RUN_DATE_COLUMN")) }
                th(class: 'runtime') { raw(_("RUN_TIME_COLUMN")) }
                th(class: 'message') { raw(_("MESSAGE_COLUMN")) }
            }
        }
        tbody {
            ExecutionResult execution = my.getExecution()
            for (TestResult testResult : execution.testResults) {
                tableRow(testResult)
            }
        }
    }
}

private void tableRow(testResult) {
    tr {
        td {
            span(class: 'mobile-th') { raw("${_("ID_COLUMN")}:") }
            raw("#${testResult.id}")
        }
        td {
            span(class: 'mobile-th') { raw("${_("STATUS_COLUMN")}:") }
            span(class: "badge badge-${testResult.level.toLowerCase()}") {
                raw(testResult.level)
            }
        }

        td {
            span(class: 'mobile-th') { raw("${_("RUN_DATE_COLUMN")}:") }
            String dateFormat = TestiniumPluginConfiguration.get(my.owningRun.getParent()).getDateTimeFormat()
            raw(new SimpleDateFormat(dateFormat).format(testResult.startDate))
        }

        def base = new Date(0)
        def duration
        def runTime = new Date(testResult.runtime)
        use(TimeCategory) {
            duration = new CustomTimeDuration(runTime - base)
        }

        td {
            span(class: 'mobile-th') { raw("${_("RUN_TIME_COLUMN")}:") }
            if (testResult.runtime != 0) {
                raw("${duration}")
            } else {
                raw("-")
            }
        }

        td(class: 'message-column') {
            p("${testResult.message}")
        }
    }
}

