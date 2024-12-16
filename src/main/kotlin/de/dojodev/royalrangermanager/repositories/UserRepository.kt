package de.dojodev.royalrangermanager.repositories

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

    fun insertOrUpdate(user: User): Int {
        if(user.id != 0) {
            this.userMapper?.update(user)
        } else {
            user.password = encrypt(user.password)
            user.id = this.userMapper?.insert(user) ?: 0
        }
        return user.id
    }

    fun delete(user: User) {
        this.userMapper?.delete(user)
    }
}