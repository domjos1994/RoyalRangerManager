package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.Project
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.stage.FileChooser
import java.net.URL
import java.util.*
import kotlin.system.exitProcess

class MainController : Initializable {
    // program menu
    @FXML
    private lateinit var mnuProgram: Menu
    @FXML
    private lateinit var mnuProgramProjectCreate: MenuItem
    @FXML
    private lateinit var mnuProgramProjectOpen: MenuItem
    @FXML
    private lateinit var mnuProgramProjectRecent: MenuItem
    @FXML
    private lateinit var mnuProgramProjectClose: MenuItem
    @FXML
    private lateinit var mnuProgramClose: MenuItem

    @FXML
    private lateinit var lblProject: Label
    @FXML
    private lateinit var lblDB: Label

    private val project = Project.get()

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        val extension = FileChooser.ExtensionFilter("Project", "*.rrm")
        this.setData()

        this.mnuProgram.setOnShowing {
            mnuProgramProjectOpen.isDisable = project.isOpen()
            mnuProgramProjectRecent.isDisable = project.isOpen()
            mnuProgramProjectCreate.isDisable = project.isOpen()
            mnuProgramProjectClose.isDisable = !project.isOpen()
        }

        this.mnuProgramProjectCreate.setOnAction {
            val name = FXHelper.getFileChooser(true, extensions = listOf(extension))
            if(name.isNotEmpty()) {
                project.close()
                project.create(name)
                this.setData()
                project.save()
            }
        }

        this.mnuProgramProjectOpen.setOnAction {
            val name = FXHelper.getFileChooser(false, extensions = listOf(extension))
            if(name.isNotEmpty()) {
                project.close()
                project.open(name)
                this.setData()
            }
        }

        this.mnuProgramProjectClose.setOnAction {
            project.close()
            this.setData()
        }


        mnuProgramClose.setOnAction {
            project.close()
            exitProcess(0)
        }
    }


    private fun setData() {
        if(this.project.isOpen()) {
            this.lblProject.text = this.project.getPath()
            val props = project.getProperties("config.properties")
            this.lblDB.text = props?.getProperty("url") ?: ""
        } else {
            this.lblProject.text = ""
            this.lblDB.text = ""
        }
    }
}