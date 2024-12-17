package de.dojodev.royalrangermanager.repositories

import de.dojodev.royalrangermanager.controller.LoginController
import de.dojodev.royalrangermanager.db.mapper.UserMapper
import de.dojodev.royalrangermanager.db.model.User
import de.dojodev.royalrangermanager.helper.DBHelper
import de.dojodev.royalrangermanager.helper.FXHelper
import org.apache.ibatis.session.SqlSession
import java.security.MessageDigest
import kotlin.jvm.Throws

class UserRepository(
    private val sqlSession: SqlSession? = DBHelper.getSession()
) {
    private var userMapper: UserMapper? = null

    companion object {
        private var md5: MessageDigest? = null

        fun encrypt(value: String): String {
            if(this.md5 == null) {
                this.md5 = MessageDigest.getInstance("MD5")
            }

            this.md5?.update(value.encodeToByteArray())
            val digest = this.md5?.digest()
            return digest?.decodeToString() ?: ""
        }
    }

    init {
        this.userMapper = this.sqlSession?.getMapper(UserMapper::class.java)

        val admin = encrypt("admin")
        val users = this.userMapper?.getUsers()
        if(users?.isEmpty() != false) {
            val user = User(0, "admin", admin, 0, "", 1, 0, 0, 0, 0, 0)
            this.userMapper?.insert(user)
        }
    }

    fun getUsers(): List<User> {
        return this.userMapper?.getUsers() ?: listOf()
    }

    fun getUsersById(id: Int): User? {
        return this.userMapper?.getUserById(id)
    }

    @Throws(Exception::class)
    fun login(user: String, password: String): User {
        val md5Pwd = encrypt(password)

        var userObj = this.userMapper?.login(user, md5Pwd)
        if(userObj != null) {
            return userObj
        }
        userObj = this.userMapper?.getUserByName(user)
        if(userObj != null) {
            throw Exception(FXHelper.getBundle().getString("sys.user.dataPassword"))
        } else {
            throw Exception(FXHelper.getBundle().getString("sys.user.dataIncorrect"))
        }
    }

    @Throws(Exception::class)
    fun updatePassword(user: String, password: String, newPassword: String): User {
        val md5Pwd = encrypt(password)

        var userObj = this.userMapper?.login(user, md5Pwd)
        if(userObj != null) {
            userObj.password = encrypt(newPassword)
            this.userMapper?.update(userObj)
            return userObj
        }
        userObj = this.userMapper?.getUserByName(user)
        if(userObj != null) {
            throw Exception(FXHelper.getBundle().getString("sys.user.dataPassword"))
        } else {
            throw Exception(FXHelper.getBundle().getString("sys.user.dataIncorrect"))
        }
    }

    @Throws(Exception::class)
    fun insertOrUpdate(user: User): Int {

        // load old data
        val oldUsers = this.userMapper?.getUsers()

        if(user.id != 0) {

            // load old user
            val oldUser = getUsersById(user.id)

            // check if admin is more than one time
            if(oldUser?.admin == 1 && user.admin==0) {
                val count = oldUsers?.count { it.admin == 1 } ?: 0
                if(count <= 1) {
                    throw Exception(FXHelper.getBundle().getString("sys.user.dataAdmin"))
                }
            }

            // check if username exists
            if(oldUser?.userName != user.userName) {
                val count = oldUsers?.count { it.userName == user.userName && it.id != user.id} ?: 0
                if(count >= 1) {
                    throw Exception(FXHelper.getBundle().getString("sys.user.dataUsername"))
                }
            }

            // check if email exists
            if(oldUser?.email != user.email && user.email.isNotEmpty()) {
                val count = oldUsers?.count { it.email == user.email && it.id != user.id} ?: 0
                if(count >= 1) {
                    throw Exception(FXHelper.getBundle().getString("sys.user.dataEmail"))
                }
            }

            this.userMapper?.update(user)
        } else {

            // check if username exists
            if(user.userName.isNotEmpty()) {
                val count = oldUsers?.count { it.userName == user.userName && it.id != user.id} ?: 0
                if(count >= 1) {
                    throw Exception(FXHelper.getBundle().getString("sys.user.dataUsername"))
                }
            } else {
                throw Exception(FXHelper.getBundle().getString("sys.user.dataUsernameNotEmpty"))
            }

            // check if email exists
            if(user.email.isNotEmpty()) {
                val count = oldUsers?.count { it.email == user.email && it.id != user.id} ?: 0
                if(count >= 1) {
                    throw Exception(FXHelper.getBundle().getString("sys.user.dataEmail"))
                }
            }

            user.password = encrypt(user.password)
            user.id = this.userMapper?.insert(user) ?: 0

            LoginController.createDialog(true, user.userName, true)
        }
        return user.id
    }

    @Throws(Exception::class)
    fun delete(user: User) {
        // load old data
        val oldUsers = this.userMapper?.getUsers()
        val oldUser = getUsersById(user.id)

        // check admin
        if(oldUser?.admin == 1) {
            val count = oldUsers?.count { it.admin == 1 } ?: 0
            if(count <= 1) {
                throw Exception(FXHelper.getBundle().getString("sys.user.dataAdmin"))
            }
        }

        this.userMapper?.delete(user)
    }
}