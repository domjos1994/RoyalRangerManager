package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.db.model.User
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.Project
import de.dojodev.royalrangermanager.repositories.UserRepository
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback

class UsersController : SubController() {

    @FXML private lateinit var tblUsers: TableView<User>

    private var users = mutableListOf<User>()
    private var userRepository: UserRepository? = null

    override fun initControls() {
        super.cmdNew?.setOnAction {
            this.users.add(User(0, "", "", 0, "", 0, 0, 0, 0, 0, 0))
        }

        val userNameCol = TableColumn<User, String>()
        userNameCol.text = FXHelper.getBundle().getString("user.userName")
        userNameCol.cellValueFactory = PropertyValueFactory("userName")
        userNameCol.cellFactory = TextFieldTableCell.forTableColumn()
        userNameCol.onEditCommit = EventHandler {
            try {
                this.users[it.tablePosition.row].userName = it.newValue
                this.userRepository!!.insertOrUpdate(this.users[it.tablePosition.row])
                FXHelper.printSuccess()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
        this.tblUsers.columns.add(userNameCol)

        val emailCol = TableColumn<User, String>()
        emailCol.text = FXHelper.getBundle().getString("user.email")
        emailCol.cellValueFactory = PropertyValueFactory("email")
        emailCol.cellFactory = TextFieldTableCell.forTableColumn()
        emailCol.onEditCommit = EventHandler {
            try {
                this.users[it.tablePosition.row].email = it.newValue
                this.userRepository!!.insertOrUpdate(this.users[it.tablePosition.row])
                FXHelper.printSuccess()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
        this.tblUsers.columns.add(emailCol)

        val genderCol = TableColumn<User, String>()
        genderCol.text = FXHelper.getBundle().getString("user.gender")
        genderCol.cellValueFactory = Callback { data ->
            val user = data.value
            val stringProperty = SimpleStringProperty(if(user.gender == 0) FXHelper.getBundle().getString("user.gender.m") else FXHelper.getBundle().getString("user.gender.m"))
            stringProperty.addListener {_,_, new ->
                user.gender = if(new==FXHelper.getBundle().getString("user.gender.m")) 0 else 1
            }
            stringProperty
        }
        genderCol.cellFactory =
            ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(
                    FXHelper.getBundle().getString("user.gender.m"),
                    FXHelper.getBundle().getString("user.gender.f")
                )
            )
        this.tblUsers.columns.add(genderCol)

        val adminCol = TableColumn<User, Boolean>()
        adminCol.text = FXHelper.getBundle().getString("user.admin")
        adminCol.cellValueFactory = Callback { data ->
            val user = data.value
            val booleanProperty = SimpleBooleanProperty(user.admin == 1)
            booleanProperty.addListener {_, _, new ->
                user.admin = if(new) 1 else 0
            }
            booleanProperty
        }
        adminCol.cellFactory = CheckBoxTableCell.forTableColumn(adminCol)
        this.tblUsers.columns.add(adminCol)

    }

    override fun init() {
        super.cmdEdit?.isVisible = false
        super.cmdCancel?.isVisible = false
        super.cmdSave?.isVisible = false
        super.cmdDelete?.isVisible = false

        val user = Project.get().getUser()

        if(user != null) {
            this.userRepository = UserRepository()
            this.users.clear()

            if(user.admin == 1 || user.trunkLeader == 1 || user.trunkWait == 1 || user.trunkHelper == 1) {
                users.addAll(this.userRepository!!.getUsers())
            } else {
                this.users.add(user)
            }
            this.tblUsers.items.addAll(this.users)
        }
    }
}