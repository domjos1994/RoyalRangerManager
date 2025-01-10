package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.db.model.User
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.Project
import de.dojodev.royalrangermanager.repositories.UserRepository
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.net.URL
import java.util.*
import kotlin.system.exitProcess


class LoginController : Initializable {
    @FXML private lateinit var cmdLogin: Button
    @FXML private lateinit var cmdCancel: Button

    @FXML private lateinit var txtUserName: TextField
    @FXML private lateinit var txtPassword: PasswordField
    @FXML private lateinit var txtPasswordNew: PasswordField
    @FXML private lateinit var lblPasswordNew: Label
    @FXML private lateinit var txtPasswordRepeat: PasswordField
    @FXML private lateinit var lblPasswordRepeat: Label


    private var user: User? = null

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        txtPasswordRepeat.isVisible = updatePassword
        lblPasswordRepeat.isVisible = updatePassword
        txtPasswordNew.isVisible = updatePassword
        lblPasswordNew.isVisible = updatePassword
        txtUserName.isDisable = LoginController.user.isNotEmpty()
        txtUserName.text = LoginController.user
        txtPassword.isDisable = empty

        val repository = UserRepository()
        if(!updatePassword) {
            this.txtUserName.requestFocus()
        }

        this.txtPassword.setOnKeyReleased {
            if(it.code == KeyCode.ENTER) {
                this.cmdLogin.fire()
            }
        }

        this.txtPasswordRepeat.setOnKeyReleased {
            if(it.code == KeyCode.ENTER) {
                this.cmdLogin.fire()
            }
        }

        this.cmdLogin.setOnAction {
            try {
                if(updatePassword) {
                    if(txtPasswordNew.text.equals(txtPasswordRepeat.text)) {
                        this.user = repository.updatePassword(
                            txtUserName.text,
                            txtPassword.text,
                            txtPasswordNew.text
                        )
                        Project.get().setUser(this.user)
                        result = 1
                        (this.cmdLogin.scene.window as Stage).close()
                    } else {
                        throw Exception(FXHelper.getBundle().getString("sys.user.dataPasswordSame"))
                    }
                } else {
                    this.user = repository.login(
                        txtUserName.text,
                        txtPassword.text
                    )
                    Project.get().setUser(this.user)
                    result = 1
                    (this.cmdLogin.scene.window as Stage).close()
                }
            } catch (ex: Exception) {
                result = -1
                FXHelper.printNotification(ex)
            }
        }

        this.cmdCancel.setOnAction {
            exitProcess(0)
        }
    }

    fun getUser(): User? {
        return this.user
    }

    companion object {
        private var updatePassword: Boolean = false
        private var result = 0
        private var empty: Boolean = false
        private var user: String = ""

        fun createDialog(updatePassword: Boolean = false, user: String = "", empty: Boolean = false): LoginController {
            this.updatePassword = updatePassword
            this.empty = empty
            this.user = user

            val loader = FXMLLoader(LoginController::class.java.getResource("/fxml/login.fxml"), FXHelper.getBundle())
            val root = loader.load<Parent>()
            val scene = Scene(root)
            val stage = Stage()
            stage.icons.add(Image(this::class.java.getResourceAsStream("/icons/icon.png")))
            stage.title = FXHelper.getBundle().getString("name")
            stage.setOnHiding {
                if(!updatePassword && result != 1) {
                    exitProcess(0)
                }
            }
            stage.initModality(Modality.NONE)
            stage.initStyle(StageStyle.DECORATED)
            stage.initOwner(FXHelper.getStage())
            stage.scene = scene
            stage.showAndWait()
            return loader.getController() as LoginController
        }
    }
}