package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.db.model.User
import de.dojodev.royalrangermanager.helper.DBHelper
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.Project
import de.dojodev.royalrangermanager.helper.Settings
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.ProgressBar
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.stage.FileChooser
import org.controlsfx.glyphfont.FontAwesome
import org.kordamp.ikonli.javafx.FontIcon
import java.io.File
import java.net.URL
import java.util.*
import kotlin.system.exitProcess

class MainController : Initializable {
    // program menu
    @FXML private lateinit var mnuProgram: Menu
    @FXML private lateinit var mnuProgramSettings: MenuItem
    @FXML private lateinit var mnuProgramProjectCreate: MenuItem
    @FXML private lateinit var mnuProgramProjectOpen: MenuItem
    @FXML private lateinit var mnuProgramProjectRecent: Menu
    @FXML private lateinit var mnuProgramProjectClose: MenuItem
    @FXML private lateinit var mnuProgramClose: MenuItem

    // toolbar
    @FXML private lateinit var cmdHome: Button
    @FXML lateinit var cmdNew: Button
    @FXML lateinit var cmdEdit: Button
    @FXML lateinit var cmdDelete: Button
    @FXML lateinit var cmdSave: Button
    @FXML lateinit var cmdCancel: Button

    // tabs
    @FXML private lateinit var tbcMain: TabPane
    @FXML private lateinit var tbMain: Tab
    @FXML private lateinit var tbSettings: Tab

    @FXML lateinit var pbMain: ProgressBar
    @FXML private lateinit var lblProject: Label
    @FXML private lateinit var lblDB: Label

    @FXML private lateinit var settingsController: SettingsController
    @FXML private lateinit var homeController: HomeController

    private val project = Project.get()
    private var user: User? = null

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        FXHelper.initSubControllers(this, listOf(homeController, settingsController))
        val extension = FileChooser.ExtensionFilter("Project", "*.rrm")
        this.project.getPath().addListener {_,_,v -> lblProject.text = v}
        this.setData()
        this.initIcons()

        this.mnuProgram.setOnShowing {
            try {
                mnuProgramProjectOpen.isDisable = project.isOpen()
                mnuProgramProjectRecent.isDisable = project.isOpen()
                mnuProgramProjectCreate.isDisable = project.isOpen()
                mnuProgramProjectClose.isDisable = !project.isOpen()

                // update recent projects
                this.mnuProgramProjectRecent.items.clear()
                val settings = Settings()
                val recent = settings.getSetting(Settings.KEY_RECENT_LIST, "")
                val items = recent.split(",")

                items.forEach { item ->
                    val file = File(item.trim())
                    if(file.exists() && file.isFile) {
                        val mnuItem = MenuItem()
                        mnuItem.text = file.absolutePath
                        mnuItem.setOnAction {
                            project.close()
                            project.open(file.absolutePath)
                            this.setData()
                        }
                        this.mnuProgramProjectRecent.items.add(mnuItem)
                    }
                }
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.mnuProgramSettings.setOnAction {
            this.tbcMain.selectionModel.select(this.tbSettings)
        }

        this.mnuProgramProjectCreate.setOnAction {
            try {
                val name = FXHelper.getFileChooser(true, extensions = listOf(extension))
                if(name.isNotEmpty()) {
                    project.close()
                    project.create(name)
                    this.setData()
                    project.save()
                    DBHelper.initBatis()
                    FXHelper.printSuccess()
                    val controller = LoginController.createDialog()
                    this.user = controller.getUser()
                }
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.mnuProgramProjectOpen.setOnAction {
            try {
                val name = FXHelper.getFileChooser(false, extensions = listOf(extension))
                if(name.isNotEmpty()) {
                    project.close()
                    project.open(name)
                    this.setData()
                    DBHelper.initBatis()
                    FXHelper.printSuccess()
                    val controller = LoginController.createDialog()
                    this.user = controller.getUser()
                }
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.mnuProgramProjectClose.setOnAction {
            try {
                DBHelper.close()
                project.close()
                this.setData()
                FXHelper.printSuccess()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }


        this.mnuProgramClose.setOnAction {
            try {
                project.close()
                exitProcess(0)
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.tbcMain.selectionModel.selectedItemProperty().addListener { _, _, c ->
            try {
                val name = FXHelper.getBundle().getString("name")
                if(name == c.text) {
                    FXHelper.getStage().title = c.text
                } else {
                    FXHelper.getStage().title = "$name - ${c.text}"
                }
                when(c.text) {
                    p1?.getString("name") -> homeController.initControls()
                    p1?.getString("main.program.settings") -> settingsController.initControls()
                }
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdHome.setOnAction {
            this.tbcMain.selectionModel.select(this.tbMain)
        }
    }

    private fun initIcons() {
        try {
            this.setIcon(this.cmdHome, FontAwesome.Glyph.HOME)
            this.setIcon(this.cmdNew, FontAwesome.Glyph.PLUS)
            this.setIcon(this.cmdEdit, FontAwesome.Glyph.EDIT)
            this.setIcon(this.cmdDelete, FontAwesome.Glyph.MINUS)
            this.setIcon(this.cmdSave, FontAwesome.Glyph.SAVE)
            this.setIcon(this.cmdCancel, FontAwesome.Glyph.CLOSE)
        } catch (ex: Exception) {
            FXHelper.printNotification(ex)
        }
    }

    private fun setIcon(cmd: Button, glyph: FontAwesome.Glyph) {
        val icon = FontIcon("fa-${glyph.name.lowercase()}")
        icon.iconSize = 16
        cmd.graphic = icon
    }

    fun setData() {
        try {
            if(this.project.isOpen()) {
                val props = project.getProperties("config.properties")
                this.lblDB.text = props?.getProperty("url") ?: ""

                DBHelper.close()
                DBHelper.initBatis()
            } else {
                this.lblDB.text = ""
            }
        } catch (ex: Exception) {
            FXHelper.printNotification(ex)
        }
    }
}