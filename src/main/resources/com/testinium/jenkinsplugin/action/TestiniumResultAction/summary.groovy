package com.testinium.jenkinsplugin.action.TestiniumResultAction

import lib.JenkinsTagLib

t = namespace(JenkinsTagLib.class)

t.summary(icon: '/plugin/testinium/icons/testinium-icon.png') {
    raw("<a href=\"${my.getUrlName()}\">${my.getDisplayName()}</a>")
    p {
        for (Map.Entry<String, Integer> summary : my.getResultSummary()) {
            raw("${summary.key} : ${summary.value}</br>")
        }
    }
}
