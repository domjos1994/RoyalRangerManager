package de.dojodev.royalrangermanager.db.mapper

import de.dojodev.royalrangermanager.db.model.Rule
import org.apache.ibatis.annotations.*

interface RuleMapper {

    @Select("SELECT * FROM rules")
    fun getRules(): List<Rule>

    @Select("SELECT * FROM rules WHERE id=#{id}")
    fun getRule(id: Int): Rule?

    @Insert(
        "INSERT INTO rules(" +
                "childNumber, price, start" +
        ") VALUES(" +
                "#{childNumber}, #{price}, #{start}" +
        ")"
    )
    @SelectKey(
        statement=["select max(id) as id from rules"],
        keyProperty="id", before=false, resultType=Int::class
    )
    fun insertRule(rule: Rule): Int

    @Update(
        "UPDATE rules SET " +
                "childNumber=#{childNumber}, price=#{price}, start=#{start} " +
        "WHERE id=#{id}"
    )
    fun updateRule(rule: Rule)

    @Delete("DELETE FROM rules WHERE id=#{id}")
    fun deleteRule(rule: Rule)
}