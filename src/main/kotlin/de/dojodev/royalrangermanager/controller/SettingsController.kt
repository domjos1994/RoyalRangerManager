package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.Project
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.stage.FileChooser

class SettingsController : SubController() {
    private val project = Project.get()

    private var sqlite = ""
    private var mysql = ""

    @FXML private lateinit var cmbDriver: ComboBox<String>
    @FXML private lateinit var txtUrl: TextField
    @FXML private lateinit var txtDb: TextField
    @FXML private lateinit var cmdDb: Button
    @FXML private lateinit var txtUsername: TextField
    @FXML private lateinit var txtPassword: TextField

    private lateinit var cmdSave: Button

    override fun initControls() {
        this.initDropDown()
        this.initData()

        this.cmdDb.setOnAction {
            try {
                val filter = FileChooser.ExtensionFilter(this.lang?.getString("settings.db"), "*.db")
                val result = FXHelper.getFileChooser(true, extensions = listOf(filter))
                if(result.isNotEmpty()) {
                    txtDb.text = result
                }
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
    }

    override fun initButtons() {
        this.cmdSave = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.SAVE) {
            try {
                val props = this.project.getProperties("config.properties")
                when(this.cmbDriver.selectionModel.selectedItem) {
                    this.sqlite -> {
                        props?.setProperty("driver", "org.sqlite.JDBC")
                        props?.setProperty("url", "jdbc:sqlite:${txtDb.text}")
                        props?.setProperty("username", "")
                        props?.setProperty("password", "")
                    }
                    this.mysql -> {
                        props?.setProperty("driver", "com.mysql.jdbc.Driver")
                        props?.setProperty("url", "jdbc:mysql://${txtUrl.text}/${txtDb.text}")
                        props?.setProperty("username", txtUsername.text)
                        props?.setProperty("password", txtPassword.text)
                    }
                }
                this.project.saveProperties("config.properties", props!!)
                this.project.save()
                this.mainController.setData()
                FXHelper.printSuccess()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
    }

    override fun initBindings() {}

    private fun initData() {
        try {
            val props = this.project.getProperties("config.properties")
            val driver = props?.getProperty("driver") ?: ""
            val url = props?.getProperty("url") ?: ""
            val username = props?.getProperty("username") ?: ""
            val password = props?.getProperty("password") ?: ""
            when(driver) {
                "org.sqlite.JDBC" -> {
                    this.cmbDriver.selectionModel.select(this.sqlite)
                    this.txtDb.text = url.replace("jdbc:sqlite:", "")
                }
                "com.mysql.jdbc.Driver" -> {
                    this.cmbDriver.selectionModel.select(this.mysql)
                    val data = url.replace("jdbc:mysql://", "").split("/")
                    if(data.size == 2) {
                        this.txtUrl.text = data[0]
                        this.txtDb.text = data[1]
                    }
                }
                else -> {
                    this.cmbDriver.selectionModel.clearSelection()
                }
            }
            this.txtUsername.text = username
            this.txtPassword.text = password
        } catch (ex: Exception) {
            FXHelper.printNotification(ex)
        }
    }

    private fun initDropDown() {
        try {
            this.sqlite = super.lang?.getString("settings.db.driver.sqlite") ?: ""
            this.mysql = super.lang?.getString("settings.db.driver.mysql") ?: ""
            this.cmbDriver.items.add(this.sqlite)
            this.cmbDriver.items.add(this.mysql)

            this.cmbDriver.selectionModel.selectedItemProperty().addListener { _,_,c ->
                when(c) {
                    this.sqlite -> {
                        this.cmdDb.isVisible = true
                        AnchorPane.setRightAnchor(this.txtDb, 35.0)
                        this.txtUrl.isDisable = true
                        this.txtUsername.isDisable = true
                        this.txtPassword.isDisable = true
                    }
                    this.mysql -> {
                        this.cmdDb.isVisible = false
                        AnchorPane.setRightAnchor(this.txtDb, 0.0)
                        this.txtUrl.isDisable = false
                        this.txtUsername.isDisable = false
                        this.txtPassword.isDisable = false

                    }
                    else -> {
                        this.cmdDb.isVisible = true
                        AnchorPane.setRightAnchor(this.txtDb, 35.0)
                        this.txtUrl.isDisable = false
                        this.txtUsername.isDisable = false
                        this.txtPassword.isDisable = false
                    }
                }
            }
        } catch (ex: Exception) {
            FXHelper.printNotification(ex)
        }
    }
}