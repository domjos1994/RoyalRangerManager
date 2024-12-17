package de.dojodev.royalrangermanager.db.mapper

import de.dojodev.royalrangermanager.db.model.AgeGroup
import org.apache.ibatis.annotations.Select

interface AgeGroupMapper {

    @Select("SELECT * FROM ageGroups")
    fun getAgeGroups(): List<AgeGroup>
}