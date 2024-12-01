package com.example.services

import com.example.configDb.DataBaseFactory.dbQuery
import com.example.configDb.DataBaseFactory.getConnection
import com.example.dto.RoutesDto
import com.example.models.Routes
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.sql.Types

public class RoutesService {


    suspend fun getCurrentlyInRoute(pRouteId: Int): Int {
        var result: Int = 0
        val query = "CALL get_currently_in_route(?, ?)"
        val callableStatement = getConnection().prepareCall(query)
        callableStatement.setInt(1, pRouteId)
        callableStatement.registerOutParameter(2, Types.INTEGER)
        callableStatement.execute()
        result = callableStatement.getInt(2)
        callableStatement.close()
        return result
    }

    @Serializable
    data class ShortestTimeResult(val shortestTime: String, val fastestCarId: String)

    suspend fun getShortestTime(pRouteId: Int): ShortestTimeResult {
        val query = "CALL get_shortest_time(?, ?, ?)"
        val callableStatement = getConnection().prepareCall(query)

        callableStatement.setInt(1, pRouteId)

        callableStatement.registerOutParameter(
            2,
            Types.VARCHAR
        )
        callableStatement.registerOutParameter(
            3,
            Types.INTEGER
        )

        callableStatement.execute()

        val shortestTime = callableStatement.getString(2)
        val fastestCarId = callableStatement.getInt(3)
        callableStatement.close()

        val results = ShortestTimeResult(shortestTime, fastestCarId.toString())
        return results
    }

    suspend fun getAllRoutes(): List<RoutesDto> {
        return dbQuery {
            Routes.selectAll().map(::toRoutesDto)
        }
    }

    suspend fun getRoutesById(id: Int): RoutesDto? {
        return dbQuery {
            Routes.select { Routes.id eq id }.map(::toRoutesDto).singleOrNull()
        }
    }

    suspend fun addRoute(routeDto: RoutesDto): Int {
        return dbQuery {
            Routes.insertAndGetId {
                it[name] = routeDto.name
            }.value
        }
    }


    suspend fun updateRoute(routeDto: RoutesDto): Boolean {
        return dbQuery {
            val count = Routes.update({ Routes.id eq routeDto.id }) {
                it[name] = routeDto.name
            }
            count > 0
        }
    }

    suspend fun deleteRoute(id: Int): Boolean {
        return dbQuery {
            val count = Routes.deleteWhere { Routes.id eq id }
            count > 0
        }
    }


    private fun toRoutesDto(row: ResultRow): RoutesDto {
        return RoutesDto(
            id = row[Routes.id].value,
            name = row[Routes.name]
        )
    }
}
