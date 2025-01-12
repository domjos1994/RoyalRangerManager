package de.dojodev.royalrangermanager.reporting

import de.dojodev.royalrangermanager.helper.DBHelper
import javafx.stage.Window
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperFillManager
import win.zqxu.jrviewer.JRViewerFX

class ReportHelper(resource: String, window: Window) {

    init {
        val stream = this::class.java.getResourceAsStream(resource)
        val report = JasperCompileManager.compileReport(stream)
        val print = JasperFillManager.fillReport(report, mutableMapOf(), DBHelper.getSession()?.connection)
        JRViewerFX.preview(window, print)
    }
}