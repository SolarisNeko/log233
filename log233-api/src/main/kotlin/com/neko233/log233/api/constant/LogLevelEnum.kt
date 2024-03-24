package com.neko233.log233.api.constant

/**
 *
 *
 * @author SolarisNeko
 * Date on 2024-01-01
 * */
enum class LogLevelEnum(
    val level: Int
) {
    // debug info
    DEBUG(1),

    // normal info
    INFO(2),

    // waning info
    WARN(3),

    // error info
    ERROR(4),
    ;
}