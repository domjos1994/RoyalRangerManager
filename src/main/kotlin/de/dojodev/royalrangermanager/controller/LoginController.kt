package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.db.model.User
import de.dojodev.royalrangermanager.helper.FXHelper
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
    @FXML private lateinit var txtPasswordRepeat: PasswordField
    @FXML private lateinit var lblPasswordRepeat: Label


    private var user: User? = null

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        txtPasswordRepeat.isVisible = updatePassword
        lblPasswordRepeat.isVisible = updatePassword
        val repository = UserRepository()

        this.cmdLogin.setOnAction {
            try {
                this.user = repository.login(
                    txtUserName.text,
                    txtPassword.text
                )
                (this.cmdLogin.scene.window as Stage).close()
            } catch (ex: Exception) {
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

        fun createDialog(updatePassword: Boolean = false): LoginController {
            this.updatePassword = updatePassword

            val loader = FXMLLoader(LoginController::class.java.getResource("/fxml/login.fxml"), FXHelper.getBundle())
            val root = loader.load<Parent>()
            val scene = Scene(root)
            val stage = Stage()
            stage.initModality(Modality.NONE)
            stage.initStyle(StageStyle.DECORATED)
            stage.initOwner(FXHelper.getStage())
            stage.scene = scene
            stage.showAndWait()
            return loader.getController() as LoginController
        }
    }
}