package de.dojodev.royalrangermanager.db.mapper

import de.dojodev.royalrangermanager.db.model.User
import org.apache.ibatis.annotations.*

interface UserMapper {

    @Select("SELECT * FROM users")
    fun getUsers(): List<User>

    @Select("SELECT * FROM users WHERE id=#{id}")
    fun getUserById(@Param("id") id: Int): User?

    @Select("SELECT * FROM users where userName=#{user}")
    fun getUserByName(@Param("user") user: String): User?

    @Select("SELECT * FROM users WHERE userName=#{user} AND password=#{password}")
    fun login(@Param("user") user: String, @Param("password") password: String): User?

    @Insert(
        "INSERT INTO " +
                "users(" +
                    "userName, password, gender, email, " +
                    "admin, trunkLeader, trunkWait, trunkHelper, leader, juniorLeader" +
                ") " +
                "VALUES(" +
                    "#{userName}, #{password}, #{gender}, #{email}," +
                    "#{admin}, #{trunkLeader}, #{trunkWait}, #{trunkHelper}, #{leader}, #{juniorLeader}" +
                ")"
    )
    @SelectKey(statement=["select max(id) as id from users"], keyProperty="id", before=false, resultType=Int::class)
    fun insert(user: User): Int

    @Update(
        "UPDATE users SET " +
                "userName=#{userName}, password=#{password}, gender=#{gender}, email=#{email}," +
                "admin=#{admin}, trunkLeader=#{trunkLeader}, trunkWait=#{trunkWait}, trunkHelper=#{trunkHelper}," +
                "leader=#{leader}, juniorLeader=#{juniorLeader} WHERE id=#{id}"
    )
    fun update(user: User)

    @Delete("DELETE FROM users WHERE id=#{id}")
    fun delete(user: User)
}