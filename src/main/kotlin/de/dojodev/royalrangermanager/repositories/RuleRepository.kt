package de.dojodev.royalrangermanager.repositories

import de.dojodev.royalrangermanager.db.mapper.RuleMapper
import de.dojodev.royalrangermanager.db.model.Rule
import de.dojodev.royalrangermanager.helper.DBHelper
import org.apache.ibatis.session.SqlSession

class RuleRepository(
    sqlSession: SqlSession? = DBHelper.getSession()
) {
    private var ruleMapper: RuleMapper? = null

    init {
        this.ruleMapper = sqlSession?.getMapper(RuleMapper::class.java)
    }

    fun getRules(): List<Rule> {
        return this.ruleMapper?.getRules() ?: listOf()
    }

    fun insertOrUpdate(rule: Rule) {
        if(rule.id != 0) {
            this.ruleMapper?.updateRule(rule)
        } else {
            this.ruleMapper?.insertRule(rule)
        }
    }

    fun deleteRule(rule: Rule) {
        this.ruleMapper?.deleteRule(rule)
    }
}