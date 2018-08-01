package com.testinium.jenkinsplugin.configuration.TestiniumPluginFolderConfiguration

import lib.CredentialsTagLib
import lib.FormTagLib

f = namespace(FormTagLib.class)
c = namespace(CredentialsTagLib.class)

style(".credentials-add-menu {width: 100%;}")

f.section(title: "Testinium Plugin") {
    f.entry(title: _("testiniumHost"), field: 'testiniumHost') {
        f.textbox()
    }

    f.entry(title: _("testiniumClientID"), field: 'testiniumClientID') {
        f.textbox()
    }

    f.entry(title: _("credentialsId"), field: 'credentialsId') {
        c.select()
    }
    f.entry(title: _("datetimeFormat"), field: 'datetimeFormat') {
        f.textbox()
    }
}