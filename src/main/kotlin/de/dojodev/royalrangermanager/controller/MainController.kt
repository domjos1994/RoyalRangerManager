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
import javafx.scene.control.ToolBar
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

    // program system
    @FXML private lateinit var mnuSystem: Menu
    @FXML private lateinit var mnuSystemUsers: MenuItem
    @FXML private lateinit var mnuSystemTeams: MenuItem

    // toolbar
    @FXML lateinit var toolMain: ToolBar
    @FXML private lateinit var cmdHome: Button

    // tabs
    @FXML private lateinit var tbcMain: TabPane
    @FXML private lateinit var tbMain: Tab
    @FXML private lateinit var tbSettings: Tab
    @FXML private lateinit var tbUsers: Tab
    @FXML private lateinit var tbTeams: Tab

    @FXML lateinit var pbMain: ProgressBar
    @FXML private lateinit var lblProject: Label
    @FXML private lateinit var lblDB: Label

    @FXML private lateinit var settingsController: SettingsController
    @FXML private lateinit var homeController: HomeController
    @FXML private lateinit var usersController: UsersController
    @FXML private lateinit var teamsController: TeamsController

    private var controllers = mutableMapOf<String, SubController>()

    private val project = Project.get()
    private var user: User? = null

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        controllers.clear()
        controllers[FXHelper.getBundle().getString("name")] = homeController
        controllers[FXHelper.getBundle().getString("main.program.settings")] = settingsController
        controllers[FXHelper.getBundle().getString("main.system.user")] = usersController
        controllers[FXHelper.getBundle().getString("main.system.teams")] = teamsController
        FXHelper.initSubControllers(this, controllers.values.toList())
        this.homeController.initIsActive()
        this.homeController.initControls()

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
                    this.homeController.initControls()
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
                    this.homeController.initControls()
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


        this.mnuSystem.setOnShowing {
            try {
                val user = Project.get().getUser()
                this.mnuSystemUsers.isDisable = user == null
                if(user == null) {
                    this.mnuSystemTeams.isDisable = true
                } else {
                    this.mnuSystemTeams.isDisable = !user.isSeniorLeader()
                }
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.mnuSystemUsers.setOnAction {
            this.tbcMain.selectionModel.select(this.tbUsers)
        }

        this.mnuSystemTeams.setOnAction {
            this.tbcMain.selectionModel.select(this.tbTeams)
        }

        this.tbcMain.selectionModel.selectedItemProperty().addListener { _, _, c ->
            try {
                val name = FXHelper.getBundle().getString("name")
                if(name == c.text) {
                    FXHelper.getStage().title = c.text
                } else {
                    FXHelper.getStage().title = "$name - ${c.text}"
                }

                controllers.forEach { (key, value) ->
                    if(key == c.text) {
                        value.initIsActive()
                        value.initControls()
                    } else {
                        value.initIsInActive()
                    }
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
            val icon = FontIcon("fa-${FontAwesome.Glyph.HOME.name.lowercase()}")
            icon.iconSize = 16
            this.cmdHome.graphic = icon
        } catch (ex: Exception) {
            FXHelper.printNotification(ex)
        }
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