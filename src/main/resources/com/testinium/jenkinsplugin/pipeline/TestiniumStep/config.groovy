package com.testinium.jenkinsplugin.pipeline.TestiniumStep

import lib.FormTagLib

f = namespace(FormTagLib.class)

f.entry(title: _("company"), field: 'companyId') {
    f.select()
}

f.entry(title: _("project"), field: 'projectId') {
    f.select()
}

f.entry(title: _("plan"), field: 'planId') {
    f.select()
}

f.entry(title: _("timeout"), field: 'timeoutSeconds') {
    f.textbox()
}

f.entry(title: _("failOnTimeout"), field: 'failOnTimeout') {
    f.checkbox()
}

f.entry(title: _("ignoreInactive"), field: 'ignoreInactive') {
    f.checkbox()
}

f.entry(title: _("failOnError"), field: 'abortOnError') {
    f.checkbox(default: true)
}

f.entry(title: _("failOnTestFail"), field: 'abortOnFailed') {
    f.checkbox(default: true)
}
