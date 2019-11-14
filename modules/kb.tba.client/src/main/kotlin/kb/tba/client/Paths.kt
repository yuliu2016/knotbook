// API Version 3.5 

@file:Suppress("unused", "SpellCheckingInspection", "KDocUnresolvedReference", "UNUSED_VARIABLE", "DuplicatedCode")

package kb.tba.client

import com.beust.klaxon.JsonObject


/**
 * Returns API status, and TBA status information.
 */
fun TBA.getStatus(): APIStatus {
    val response = get("/status")
    return APIStatus(
            raw = response,
            current_season = response.int("current_season"),
            max_season = response.int("max_season"),
            is_datafeed_down = response.boolean("is_datafeed_down"),
            down_events = response.stringList("down_events"),
            ios = response.obj("ios")?.let { ios ->
                APIStatusAppVersion(
                        raw = ios,
                        min_app_version = ios.int("min_app_version"),
                        latest_app_version = ios.int("latest_app_version")
                )
            },
            android = response.obj("android")?.let { android ->
                APIStatusAppVersion(
                        raw = android,
                        min_app_version = android.int("min_app_version"),
                        latest_app_version = android.int("latest_app_version")
                )
            }
    )
}


/**
 * Gets a list of `Team` objects, paginated in groups of 500.
 */
fun TBA.getTeams(
        page_num: Int
): List<Team> {
    val response = getArray("/teams/$page_num")
    return response.map { it as JsonObject }.map {
        Team(
                raw = it,
                key = it.string("key"),
                team_number = it.int("team_number"),
                nickname = it.string("nickname"),
                name = it.string("name"),
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                address = it.string("address"),
                postal_code = it.string("postal_code"),
                gmaps_place_id = it.string("gmaps_place_id"),
                gmaps_url = it.string("gmaps_url"),
                lat = it.double("lat"),
                lng = it.double("lng"),
                location_name = it.string("location_name"),
                website = it.string("website"),
                rookie_year = it.int("rookie_year"),
                motto = it.string("motto"),
                home_championship = it.obj("home_championship")
        )
    }
}


/**
 * Gets a list of short form `Team_Simple` objects, paginated in groups of 500.
 */
fun TBA.getTeamsSimple(
        page_num: Int
): List<TeamSimple> {
    val response = getArray("/teams/$page_num/simple")
    return response.map { it as JsonObject }.map {
        TeamSimple(
                raw = it,
                key = it.string("key"),
                team_number = it.int("team_number"),
                nickname = it.string("nickname"),
                name = it.string("name"),
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country")
        )
    }
}


/**
 * Gets a list of Team keys, paginated in groups of 500. (Note, each page will not have 500 teams, but will include the teams within that range of 500.)
 */
fun TBA.getTeamsKeys(
        page_num: Int
): List<String> {
    val response = getArray("/teams/$page_num/keys")
    return response.map { it as String }
}


/**
 * Gets a list of `Team` objects that competed in the given year, paginated in groups of 500.
 */
fun TBA.getTeamsByYear(
        year: Int,
        page_num: Int
): List<Team> {
    val response = getArray("/teams/$year/$page_num")
    return response.map { it as JsonObject }.map {
        Team(
                raw = it,
                key = it.string("key"),
                team_number = it.int("team_number"),
                nickname = it.string("nickname"),
                name = it.string("name"),
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                address = it.string("address"),
                postal_code = it.string("postal_code"),
                gmaps_place_id = it.string("gmaps_place_id"),
                gmaps_url = it.string("gmaps_url"),
                lat = it.double("lat"),
                lng = it.double("lng"),
                location_name = it.string("location_name"),
                website = it.string("website"),
                rookie_year = it.int("rookie_year"),
                motto = it.string("motto"),
                home_championship = it.obj("home_championship")
        )
    }
}


/**
 * Gets a list of short form `Team_Simple` objects that competed in the given year, paginated in groups of 500.
 */
fun TBA.getTeamsByYearSimple(
        year: Int,
        page_num: Int
): List<TeamSimple> {
    val response = getArray("/teams/$year/$page_num/simple")
    return response.map { it as JsonObject }.map {
        TeamSimple(
                raw = it,
                key = it.string("key"),
                team_number = it.int("team_number"),
                nickname = it.string("nickname"),
                name = it.string("name"),
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country")
        )
    }
}


/**
 * Gets a list Team Keys that competed in the given year, paginated in groups of 500.
 */
fun TBA.getTeamsByYearKeys(
        year: Int,
        page_num: Int
): List<String> {
    val response = getArray("/teams/$year/$page_num/keys")
    return response.map { it as String }
}


/**
 * Gets a `Team` object for the team referenced by the given key.
 */
fun TBA.getTeam(
        team_key: String
): Team {
    val response = get("/team/$team_key")
    return Team(
            raw = response,
            key = response.string("key"),
            team_number = response.int("team_number"),
            nickname = response.string("nickname"),
            name = response.string("name"),
            city = response.string("city"),
            state_prov = response.string("state_prov"),
            country = response.string("country"),
            address = response.string("address"),
            postal_code = response.string("postal_code"),
            gmaps_place_id = response.string("gmaps_place_id"),
            gmaps_url = response.string("gmaps_url"),
            lat = response.double("lat"),
            lng = response.double("lng"),
            location_name = response.string("location_name"),
            website = response.string("website"),
            rookie_year = response.int("rookie_year"),
            motto = response.string("motto"),
            home_championship = response.obj("home_championship")
    )
}


/**
 * Gets a `Team_Simple` object for the team referenced by the given key.
 */
fun TBA.getTeamSimple(
        team_key: String
): TeamSimple {
    val response = get("/team/$team_key/simple")
    return TeamSimple(
            raw = response,
            key = response.string("key"),
            team_number = response.int("team_number"),
            nickname = response.string("nickname"),
            name = response.string("name"),
            city = response.string("city"),
            state_prov = response.string("state_prov"),
            country = response.string("country")
    )
}


/**
 * Gets a list of years in which the team participated in at least one competition.
 */
fun TBA.getTeamYearsParticipated(
        team_key: String
): List<Int> {
    val response = getArray("/team/$team_key/years_participated")
    return response.map { it as Int }
}


/**
 * Gets an array of districts representing each year the team was in a district. Will return an empty array if the team was never in a district.
 */
fun TBA.getTeamDistricts(
        team_key: String
): List<DistrictList> {
    val response = getArray("/team/$team_key/districts")
    return response.map { it as JsonObject }.map {
        DistrictList(
                raw = it,
                abbreviation = it.string("abbreviation"),
                display_name = it.string("display_name"),
                key = it.string("key"),
                year = it.int("year")
        )
    }
}


/**
 * Gets a list of year and robot name pairs for each year that a robot name was provided. Will return an empty array if the team has never named a robot.
 */
fun TBA.getTeamRobots(
        team_key: String
): List<TeamRobot> {
    val response = getArray("/team/$team_key/robots")
    return response.map { it as JsonObject }.map {
        TeamRobot(
                raw = it,
                year = it.int("year"),
                robot_name = it.string("robot_name"),
                key = it.string("key"),
                team_key = it.string("team_key")
        )
    }
}


/**
 * Gets a list of all events this team has competed at.
 */
fun TBA.getTeamEvents(
        team_key: String
): List<Event> {
    val response = getArray("/team/$team_key/events")
    return response.map { it as JsonObject }.map {
        Event(
                raw = it,
                key = it.string("key"),
                name = it.string("name"),
                event_code = it.string("event_code"),
                event_type = it.int("event_type"),
                district = it.obj("district")?.let { district ->
                    DistrictList(
                            raw = district,
                            abbreviation = district.string("abbreviation"),
                            display_name = district.string("display_name"),
                            key = district.string("key"),
                            year = district.int("year")
                    )
                },
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                start_date = it.string("start_date"),
                end_date = it.string("end_date"),
                year = it.int("year"),
                short_name = it.string("short_name"),
                event_type_string = it.string("event_type_string"),
                week = it.int("week"),
                address = it.string("address"),
                postal_code = it.string("postal_code"),
                gmaps_place_id = it.string("gmaps_place_id"),
                gmaps_url = it.string("gmaps_url"),
                lat = it.double("lat"),
                lng = it.double("lng"),
                location_name = it.string("location_name"),
                timezone = it.string("timezone"),
                website = it.string("website"),
                first_event_id = it.string("first_event_id"),
                first_event_code = it.string("first_event_code"),
                webcasts = it.genericArray("webcasts")?.mapToList { webcastsItem ->
                    Webcast(
                            raw = webcastsItem,
                            type = webcastsItem.string("type"),
                            channel = webcastsItem.string("channel"),
                            file = webcastsItem.string("file")
                    )
                },
                division_keys = it.stringList("division_keys"),
                parent_event_key = it.string("parent_event_key"),
                playoff_type = it.int("playoff_type"),
                playoff_type_string = it.string("playoff_type_string")
        )
    }
}


/**
 * Gets a short-form list of all events this team has competed at.
 */
fun TBA.getTeamEventsSimple(
        team_key: String
): List<EventSimple> {
    val response = getArray("/team/$team_key/events/simple")
    return response.map { it as JsonObject }.map {
        EventSimple(
                raw = it,
                key = it.string("key"),
                name = it.string("name"),
                event_code = it.string("event_code"),
                event_type = it.int("event_type"),
                district = it.obj("district")?.let { district ->
                    DistrictList(
                            raw = district,
                            abbreviation = district.string("abbreviation"),
                            display_name = district.string("display_name"),
                            key = district.string("key"),
                            year = district.int("year")
                    )
                },
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                start_date = it.string("start_date"),
                end_date = it.string("end_date"),
                year = it.int("year")
        )
    }
}


/**
 * Gets a list of the event keys for all events this team has competed at.
 */
fun TBA.getTeamEventsKeys(
        team_key: String
): List<String> {
    val response = getArray("/team/$team_key/events/keys")
    return response.map { it as String }
}


/**
 * Gets a list of events this team has competed at in the given year.
 */
fun TBA.getTeamEventsByYear(
        team_key: String,
        year: Int
): List<Event> {
    val response = getArray("/team/$team_key/events/$year")
    return response.map { it as JsonObject }.map {
        Event(
                raw = it,
                key = it.string("key"),
                name = it.string("name"),
                event_code = it.string("event_code"),
                event_type = it.int("event_type"),
                district = it.obj("district")?.let { district ->
                    DistrictList(
                            raw = district,
                            abbreviation = district.string("abbreviation"),
                            display_name = district.string("display_name"),
                            key = district.string("key"),
                            year = district.int("year")
                    )
                },
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                start_date = it.string("start_date"),
                end_date = it.string("end_date"),
                year = it.int("year"),
                short_name = it.string("short_name"),
                event_type_string = it.string("event_type_string"),
                week = it.int("week"),
                address = it.string("address"),
                postal_code = it.string("postal_code"),
                gmaps_place_id = it.string("gmaps_place_id"),
                gmaps_url = it.string("gmaps_url"),
                lat = it.double("lat"),
                lng = it.double("lng"),
                location_name = it.string("location_name"),
                timezone = it.string("timezone"),
                website = it.string("website"),
                first_event_id = it.string("first_event_id"),
                first_event_code = it.string("first_event_code"),
                webcasts = it.genericArray("webcasts")?.mapToList { webcastsItem ->
                    Webcast(
                            raw = webcastsItem,
                            type = webcastsItem.string("type"),
                            channel = webcastsItem.string("channel"),
                            file = webcastsItem.string("file")
                    )
                },
                division_keys = it.stringList("division_keys"),
                parent_event_key = it.string("parent_event_key"),
                playoff_type = it.int("playoff_type"),
                playoff_type_string = it.string("playoff_type_string")
        )
    }
}


/**
 * Gets a short-form list of events this team has competed at in the given year.
 */
fun TBA.getTeamEventsByYearSimple(
        team_key: String,
        year: Int
): List<EventSimple> {
    val response = getArray("/team/$team_key/events/$year/simple")
    return response.map { it as JsonObject }.map {
        EventSimple(
                raw = it,
                key = it.string("key"),
                name = it.string("name"),
                event_code = it.string("event_code"),
                event_type = it.int("event_type"),
                district = it.obj("district")?.let { district ->
                    DistrictList(
                            raw = district,
                            abbreviation = district.string("abbreviation"),
                            display_name = district.string("display_name"),
                            key = district.string("key"),
                            year = district.int("year")
                    )
                },
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                start_date = it.string("start_date"),
                end_date = it.string("end_date"),
                year = it.int("year")
        )
    }
}


/**
 * Gets a list of the event keys for events this team has competed at in the given year.
 */
fun TBA.getTeamEventsByYearKeys(
        team_key: String,
        year: Int
): List<String> {
    val response = getArray("/team/$team_key/events/$year/keys")
    return response.map { it as String }
}


/**
 * Gets a key-value list of the event statuses for events this team has competed at in the given year.
 */
fun TBA.getTeamEventsStatusesByYear(
        team_key: String,
        year: Int
): Map<String, TeamEventStatus?> {
    val response = get("/team/$team_key/events/$year/statuses")
    return response.mapValues { (it as JsonObject?)!! }.mapValues {
        TeamEventStatus(
                raw = it.value,
                qual = it.value.obj("qual")?.let { qual ->
                    TeamEventStatusRank(
                            raw = qual,
                            num_teams = qual.int("num_teams"),
                            ranking = qual.obj("ranking"),
                            sort_order_info = qual.objList("sort_order_info"),
                            status = qual.string("status")
                    )
                },
                alliance = it.value.obj("alliance")?.let { alliance ->
                    TeamEventStatusAlliance(
                            raw = alliance,
                            name = alliance.string("name"),
                            number = alliance.int("number"),
                            backup = alliance.obj("backup")?.let { backup ->
                                TeamEventStatusAllianceBackup(
                                        raw = backup,
                                        out = backup.string("out"),
                                        _in = backup.string("in")
                                )
                            },
                            pick = alliance.int("pick")
                    )
                },
                playoff = it.value.obj("playoff")?.let { playoff ->
                    TeamEventStatusPlayoff(
                            raw = playoff,
                            level = playoff.string("level"),
                            current_level_record = playoff.obj("current_level_record")?.let { current_level_record ->
                                WLTRecord(
                                        raw = current_level_record,
                                        losses = current_level_record.int("losses"),
                                        wins = current_level_record.int("wins"),
                                        ties = current_level_record.int("ties")
                                )
                            },
                            record = playoff.obj("record")?.let { record ->
                                WLTRecord(
                                        raw = record,
                                        losses = record.int("losses"),
                                        wins = record.int("wins"),
                                        ties = record.int("ties")
                                )
                            },
                            status = playoff.string("status"),
                            playoff_average = playoff.int("playoff_average")
                    )
                },
                alliance_status_str = it.value.string("alliance_status_str"),
                playoff_status_str = it.value.string("playoff_status_str"),
                overall_status_str = it.value.string("overall_status_str"),
                next_match_key = it.value.string("next_match_key"),
                last_match_key = it.value.string("last_match_key")
        )
    }
}


/**
 * Gets a list of matches for the given team and event.
 */
fun TBA.getTeamEventMatches(
        team_key: String,
        event_key: String
): List<Match> {
    val response = getArray("/team/$team_key/event/$event_key/matches")
    return response.map { it as JsonObject }.map {
        Match(
                raw = it,
                key = it.string("key"),
                comp_level = it.string("comp_level"),
                set_number = it.int("set_number"),
                match_number = it.int("match_number"),
                alliances = Alliances(
                        blue = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        },
                        red = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        }
                ),
                winning_alliance = it.string("winning_alliance"),
                event_key = it.string("event_key"),
                time = it.int("time"),
                actual_time = it.int("actual_time"),
                predicted_time = it.int("predicted_time"),
                post_result_time = it.int("post_result_time"),
                score_breakdown = it.obj("score_breakdown"),
                videos = it.objList("videos")
        )
    }
}


/**
 * Gets a short-form list of matches for the given team and event.
 */
fun TBA.getTeamEventMatchesSimple(
        team_key: String,
        event_key: String
): List<Match> {
    val response = getArray("/team/$team_key/event/$event_key/matches/simple")
    return response.map { it as JsonObject }.map {
        Match(
                raw = it,
                key = it.string("key"),
                comp_level = it.string("comp_level"),
                set_number = it.int("set_number"),
                match_number = it.int("match_number"),
                alliances = Alliances(
                        blue = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        },
                        red = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        }
                ),
                winning_alliance = it.string("winning_alliance"),
                event_key = it.string("event_key"),
                time = it.int("time"),
                actual_time = it.int("actual_time"),
                predicted_time = it.int("predicted_time"),
                post_result_time = it.int("post_result_time"),
                score_breakdown = it.obj("score_breakdown"),
                videos = it.objList("videos")
        )
    }
}


/**
 * Gets a list of match keys for matches for the given team and event.
 */
fun TBA.getTeamEventMatchesKeys(
        team_key: String,
        event_key: String
): List<String> {
    val response = getArray("/team/$team_key/event/$event_key/matches/keys")
    return response.map { it as String }
}


/**
 * Gets a list of awards the given team won at the given event.
 */
fun TBA.getTeamEventAwards(
        team_key: String,
        event_key: String
): List<Award> {
    val response = getArray("/team/$team_key/event/$event_key/awards")
    return response.map { it as JsonObject }.map {
        Award(
                raw = it,
                name = it.string("name"),
                award_type = it.int("award_type"),
                event_key = it.string("event_key"),
                recipient_list = it.genericArray("recipient_list")?.mapToList { recipient_listItem ->
                    AwardRecipient(
                            raw = recipient_listItem,
                            team_key = recipient_listItem.string("team_key"),
                            awardee = recipient_listItem.string("awardee")
                    )
                },
                year = it.int("year")
        )
    }
}


/**
 * Gets the competition rank and status of the team at the given event.
 */
fun TBA.getTeamEventStatus(
        team_key: String,
        event_key: String
): TeamEventStatus {
    val response = get("/team/$team_key/event/$event_key/status")
    return TeamEventStatus(
            raw = response,
            qual = response.obj("qual")?.let { qual ->
                TeamEventStatusRank(
                        raw = qual,
                        num_teams = qual.int("num_teams"),
                        ranking = qual.obj("ranking"),
                        sort_order_info = qual.objList("sort_order_info"),
                        status = qual.string("status")
                )
            },
            alliance = response.obj("alliance")?.let { alliance ->
                TeamEventStatusAlliance(
                        raw = alliance,
                        name = alliance.string("name"),
                        number = alliance.int("number"),
                        backup = alliance.obj("backup")?.let { backup ->
                            TeamEventStatusAllianceBackup(
                                    raw = backup,
                                    out = backup.string("out"),
                                    _in = backup.string("in")
                            )
                        },
                        pick = alliance.int("pick")
                )
            },
            playoff = response.obj("playoff")?.let { playoff ->
                TeamEventStatusPlayoff(
                        raw = playoff,
                        level = playoff.string("level"),
                        current_level_record = playoff.obj("current_level_record")?.let { current_level_record ->
                            WLTRecord(
                                    raw = current_level_record,
                                    losses = current_level_record.int("losses"),
                                    wins = current_level_record.int("wins"),
                                    ties = current_level_record.int("ties")
                            )
                        },
                        record = playoff.obj("record")?.let { record ->
                            WLTRecord(
                                    raw = record,
                                    losses = record.int("losses"),
                                    wins = record.int("wins"),
                                    ties = record.int("ties")
                            )
                        },
                        status = playoff.string("status"),
                        playoff_average = playoff.int("playoff_average")
                )
            },
            alliance_status_str = response.string("alliance_status_str"),
            playoff_status_str = response.string("playoff_status_str"),
            overall_status_str = response.string("overall_status_str"),
            next_match_key = response.string("next_match_key"),
            last_match_key = response.string("last_match_key")
    )
}


/**
 * Gets a list of awards the given team has won.
 */
fun TBA.getTeamAwards(
        team_key: String
): List<Award> {
    val response = getArray("/team/$team_key/awards")
    return response.map { it as JsonObject }.map {
        Award(
                raw = it,
                name = it.string("name"),
                award_type = it.int("award_type"),
                event_key = it.string("event_key"),
                recipient_list = it.genericArray("recipient_list")?.mapToList { recipient_listItem ->
                    AwardRecipient(
                            raw = recipient_listItem,
                            team_key = recipient_listItem.string("team_key"),
                            awardee = recipient_listItem.string("awardee")
                    )
                },
                year = it.int("year")
        )
    }
}


/**
 * Gets a list of awards the given team has won in a given year.
 */
fun TBA.getTeamAwardsByYear(
        team_key: String,
        year: Int
): List<Award> {
    val response = getArray("/team/$team_key/awards/$year")
    return response.map { it as JsonObject }.map {
        Award(
                raw = it,
                name = it.string("name"),
                award_type = it.int("award_type"),
                event_key = it.string("event_key"),
                recipient_list = it.genericArray("recipient_list")?.mapToList { recipient_listItem ->
                    AwardRecipient(
                            raw = recipient_listItem,
                            team_key = recipient_listItem.string("team_key"),
                            awardee = recipient_listItem.string("awardee")
                    )
                },
                year = it.int("year")
        )
    }
}


/**
 * Gets a list of matches for the given team and year.
 */
fun TBA.getTeamMatchesByYear(
        team_key: String,
        year: Int
): List<Match> {
    val response = getArray("/team/$team_key/matches/$year")
    return response.map { it as JsonObject }.map {
        Match(
                raw = it,
                key = it.string("key"),
                comp_level = it.string("comp_level"),
                set_number = it.int("set_number"),
                match_number = it.int("match_number"),
                alliances = Alliances(
                        blue = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        },
                        red = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        }
                ),
                winning_alliance = it.string("winning_alliance"),
                event_key = it.string("event_key"),
                time = it.int("time"),
                actual_time = it.int("actual_time"),
                predicted_time = it.int("predicted_time"),
                post_result_time = it.int("post_result_time"),
                score_breakdown = it.obj("score_breakdown"),
                videos = it.objList("videos")
        )
    }
}


/**
 * Gets a short-form list of matches for the given team and year.
 */
fun TBA.getTeamMatchesByYearSimple(
        team_key: String,
        year: Int
): List<MatchSimple> {
    val response = getArray("/team/$team_key/matches/$year/simple")
    return response.map { it as JsonObject }.map {
        MatchSimple(
                raw = it,
                key = it.string("key"),
                comp_level = it.string("comp_level"),
                set_number = it.int("set_number"),
                match_number = it.int("match_number"),
                alliances = Alliances(
                        blue = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        },
                        red = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        }
                ),
                winning_alliance = it.string("winning_alliance"),
                event_key = it.string("event_key"),
                time = it.int("time"),
                predicted_time = it.int("predicted_time"),
                actual_time = it.int("actual_time")
        )
    }
}


/**
 * Gets a list of match keys for matches for the given team and year.
 */
fun TBA.getTeamMatchesByYearKeys(
        team_key: String,
        year: Int
): List<String> {
    val response = getArray("/team/$team_key/matches/$year/keys")
    return response.map { it as String }
}


/**
 * Gets a list of Media (videos / pictures) for the given team and year.
 */
fun TBA.getTeamMediaByYear(
        team_key: String,
        year: Int
): List<Media> {
    val response = getArray("/team/$team_key/media/$year")
    return response.map { it as JsonObject }.map {
        Media(
                raw = it,
                key = it.string("key"),
                type = it.string("type"),
                foreign_key = it.string("foreign_key"),
                details = it.obj("details"),
                preferred = it.boolean("preferred"),
                direct_url = it.string("direct_url"),
                view_url = it.string("view_url")
        )
    }
}


/**
 * Gets a list of Media (videos / pictures) for the given team and tag.
 */
fun TBA.getTeamMediaByTag(
        team_key: String,
        media_tag: String
): List<Media> {
    val response = getArray("/team/$team_key/media/tag/$media_tag")
    return response.map { it as JsonObject }.map {
        Media(
                raw = it,
                key = it.string("key"),
                type = it.string("type"),
                foreign_key = it.string("foreign_key"),
                details = it.obj("details"),
                preferred = it.boolean("preferred"),
                direct_url = it.string("direct_url"),
                view_url = it.string("view_url")
        )
    }
}


/**
 * Gets a list of Media (videos / pictures) for the given team, tag and year.
 */
fun TBA.getTeamMediaByTagYear(
        team_key: String,
        media_tag: String,
        year: Int
): List<Media> {
    val response = getArray("/team/$team_key/media/tag/$media_tag/$year")
    return response.map { it as JsonObject }.map {
        Media(
                raw = it,
                key = it.string("key"),
                type = it.string("type"),
                foreign_key = it.string("foreign_key"),
                details = it.obj("details"),
                preferred = it.boolean("preferred"),
                direct_url = it.string("direct_url"),
                view_url = it.string("view_url")
        )
    }
}


/**
 * Gets a list of Media (social media) for the given team.
 */
fun TBA.getTeamSocialMedia(
        team_key: String
): List<Media> {
    val response = getArray("/team/$team_key/social_media")
    return response.map { it as JsonObject }.map {
        Media(
                raw = it,
                key = it.string("key"),
                type = it.string("type"),
                foreign_key = it.string("foreign_key"),
                details = it.obj("details"),
                preferred = it.boolean("preferred"),
                direct_url = it.string("direct_url"),
                view_url = it.string("view_url")
        )
    }
}


/**
 * Gets a list of events in the given year.
 */
fun TBA.getEventsByYear(
        year: Int
): List<Event> {
    val response = getArray("/events/$year")
    return response.map { it as JsonObject }.map {
        Event(
                raw = it,
                key = it.string("key"),
                name = it.string("name"),
                event_code = it.string("event_code"),
                event_type = it.int("event_type"),
                district = it.obj("district")?.let { district ->
                    DistrictList(
                            raw = district,
                            abbreviation = district.string("abbreviation"),
                            display_name = district.string("display_name"),
                            key = district.string("key"),
                            year = district.int("year")
                    )
                },
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                start_date = it.string("start_date"),
                end_date = it.string("end_date"),
                year = it.int("year"),
                short_name = it.string("short_name"),
                event_type_string = it.string("event_type_string"),
                week = it.int("week"),
                address = it.string("address"),
                postal_code = it.string("postal_code"),
                gmaps_place_id = it.string("gmaps_place_id"),
                gmaps_url = it.string("gmaps_url"),
                lat = it.double("lat"),
                lng = it.double("lng"),
                location_name = it.string("location_name"),
                timezone = it.string("timezone"),
                website = it.string("website"),
                first_event_id = it.string("first_event_id"),
                first_event_code = it.string("first_event_code"),
                webcasts = it.genericArray("webcasts")?.mapToList { webcastsItem ->
                    Webcast(
                            raw = webcastsItem,
                            type = webcastsItem.string("type"),
                            channel = webcastsItem.string("channel"),
                            file = webcastsItem.string("file")
                    )
                },
                division_keys = it.stringList("division_keys"),
                parent_event_key = it.string("parent_event_key"),
                playoff_type = it.int("playoff_type"),
                playoff_type_string = it.string("playoff_type_string")
        )
    }
}


/**
 * Gets a short-form list of events in the given year.
 */
fun TBA.getEventsByYearSimple(
        year: Int
): List<EventSimple> {
    val response = getArray("/events/$year/simple")
    return response.map { it as JsonObject }.map {
        EventSimple(
                raw = it,
                key = it.string("key"),
                name = it.string("name"),
                event_code = it.string("event_code"),
                event_type = it.int("event_type"),
                district = it.obj("district")?.let { district ->
                    DistrictList(
                            raw = district,
                            abbreviation = district.string("abbreviation"),
                            display_name = district.string("display_name"),
                            key = district.string("key"),
                            year = district.int("year")
                    )
                },
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                start_date = it.string("start_date"),
                end_date = it.string("end_date"),
                year = it.int("year")
        )
    }
}


/**
 * Gets a list of event keys in the given year.
 */
fun TBA.getEventsByYearKeys(
        year: Int
): List<String> {
    val response = getArray("/events/$year/keys")
    return response.map { it as String }
}


/**
 * Gets an Event.
 */
fun TBA.getEvent(
        event_key: String
): Event {
    val response = get("/event/$event_key")
    return Event(
            raw = response,
            key = response.string("key"),
            name = response.string("name"),
            event_code = response.string("event_code"),
            event_type = response.int("event_type"),
            district = response.obj("district")?.let { district ->
                DistrictList(
                        raw = district,
                        abbreviation = district.string("abbreviation"),
                        display_name = district.string("display_name"),
                        key = district.string("key"),
                        year = district.int("year")
                )
            },
            city = response.string("city"),
            state_prov = response.string("state_prov"),
            country = response.string("country"),
            start_date = response.string("start_date"),
            end_date = response.string("end_date"),
            year = response.int("year"),
            short_name = response.string("short_name"),
            event_type_string = response.string("event_type_string"),
            week = response.int("week"),
            address = response.string("address"),
            postal_code = response.string("postal_code"),
            gmaps_place_id = response.string("gmaps_place_id"),
            gmaps_url = response.string("gmaps_url"),
            lat = response.double("lat"),
            lng = response.double("lng"),
            location_name = response.string("location_name"),
            timezone = response.string("timezone"),
            website = response.string("website"),
            first_event_id = response.string("first_event_id"),
            first_event_code = response.string("first_event_code"),
            webcasts = response.genericArray("webcasts")?.mapToList { webcastsItem ->
                Webcast(
                        raw = webcastsItem,
                        type = webcastsItem.string("type"),
                        channel = webcastsItem.string("channel"),
                        file = webcastsItem.string("file")
                )
            },
            division_keys = response.stringList("division_keys"),
            parent_event_key = response.string("parent_event_key"),
            playoff_type = response.int("playoff_type"),
            playoff_type_string = response.string("playoff_type_string")
    )
}


/**
 * Gets a short-form Event.
 */
fun TBA.getEventSimple(
        event_key: String
): EventSimple {
    val response = get("/event/$event_key/simple")
    return EventSimple(
            raw = response,
            key = response.string("key"),
            name = response.string("name"),
            event_code = response.string("event_code"),
            event_type = response.int("event_type"),
            district = response.obj("district")?.let { district ->
                DistrictList(
                        raw = district,
                        abbreviation = district.string("abbreviation"),
                        display_name = district.string("display_name"),
                        key = district.string("key"),
                        year = district.int("year")
                )
            },
            city = response.string("city"),
            state_prov = response.string("state_prov"),
            country = response.string("country"),
            start_date = response.string("start_date"),
            end_date = response.string("end_date"),
            year = response.int("year")
    )
}


/**
 * Gets a list of Elimination Alliances for the given Event.
 */
fun TBA.getEventAlliances(
        event_key: String
): List<EliminationAlliance> {
    val response = getArray("/event/$event_key/alliances")
    return response.map { it as JsonObject }.map {
        EliminationAlliance(
                raw = it,
                name = it.string("name"),
                backup = it.obj("backup"),
                declines = it.stringList("declines"),
                picks = it.stringList("picks"),
                status = it.obj("status")
        )
    }
}


/**
 * Gets a set of Event-specific insights for the given Event.
 */
fun TBA.getEventInsights(
        event_key: String
): EventInsights {
    val response = get("/event/$event_key/insights")
    return EventInsights(
            raw = response,
            qual = response.obj("qual"),
            playoff = response.obj("playoff")
    )
}


/**
 * Gets a set of Event OPRs (including OPR, DPR, and CCWM) for the given Event.
 */
fun TBA.getEventOPRs(
        event_key: String
): EventOPRs {
    val response = get("/event/$event_key/oprs")
    return EventOPRs(
            raw = response,
            oprs = response.obj("oprs"),
            dprs = response.obj("dprs"),
            ccwms = response.obj("ccwms")
    )
}


/**
 * Gets information on TBA-generated predictions for the given Event. Contains year-specific information. *WARNING* This endpoint is currently under development and may change at any time.
 */
fun TBA.getEventPredictions(
        event_key: String
): EventPredictions {
    val response = get("/event/$event_key/predictions")
    return EventPredictions(
            raw = response
    )
}


/**
 * Gets a list of team rankings for the Event.
 */
fun TBA.getEventRankings(
        event_key: String
): EventRanking {
    val response = get("/event/$event_key/rankings")
    return EventRanking(
            raw = response,
            rankings = response.objList("rankings"),
            extra_stats_info = response.objList("extra_stats_info"),
            sort_order_info = response.objList("sort_order_info")
    )
}


/**
 * Gets a list of team rankings for the Event.
 */
fun TBA.getEventDistrictPoints(
        event_key: String
): EventDistrictPoints {
    val response = get("/event/$event_key/district_points")
    return EventDistrictPoints(
            raw = response,
            points = response.obj("points"),
            tiebreakers = response.obj("tiebreakers")
    )
}


/**
 * Gets a list of `Team` objects that competed in the given event.
 */
fun TBA.getEventTeams(
        event_key: String
): List<Team> {
    val response = getArray("/event/$event_key/teams")
    return response.map { it as JsonObject }.map {
        Team(
                raw = it,
                key = it.string("key"),
                team_number = it.int("team_number"),
                nickname = it.string("nickname"),
                name = it.string("name"),
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                address = it.string("address"),
                postal_code = it.string("postal_code"),
                gmaps_place_id = it.string("gmaps_place_id"),
                gmaps_url = it.string("gmaps_url"),
                lat = it.double("lat"),
                lng = it.double("lng"),
                location_name = it.string("location_name"),
                website = it.string("website"),
                rookie_year = it.int("rookie_year"),
                motto = it.string("motto"),
                home_championship = it.obj("home_championship")
        )
    }
}


/**
 * Gets a short-form list of `Team` objects that competed in the given event.
 */
fun TBA.getEventTeamsSimple(
        event_key: String
): List<TeamSimple> {
    val response = getArray("/event/$event_key/teams/simple")
    return response.map { it as JsonObject }.map {
        TeamSimple(
                raw = it,
                key = it.string("key"),
                team_number = it.int("team_number"),
                nickname = it.string("nickname"),
                name = it.string("name"),
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country")
        )
    }
}


/**
 * Gets a list of `Team` keys that competed in the given event.
 */
fun TBA.getEventTeamsKeys(
        event_key: String
): List<String> {
    val response = getArray("/event/$event_key/teams/keys")
    return response.map { it as String }
}


/**
 * Gets a key-value list of the event statuses for teams competing at the given event.
 */
fun TBA.getEventTeamsStatuses(
        event_key: String
): Map<String, TeamEventStatus?> {
    val response = get("/event/$event_key/teams/statuses")
    return response.mapValues { (it as JsonObject?)!! }.mapValues {
        TeamEventStatus(
                raw = it.value,
                qual = it.value.obj("qual")?.let { qual ->
                    TeamEventStatusRank(
                            raw = qual,
                            num_teams = qual.int("num_teams"),
                            ranking = qual.obj("ranking"),
                            sort_order_info = qual.objList("sort_order_info"),
                            status = qual.string("status")
                    )
                },
                alliance = it.value.obj("alliance")?.let { alliance ->
                    TeamEventStatusAlliance(
                            raw = alliance,
                            name = alliance.string("name"),
                            number = alliance.int("number"),
                            backup = alliance.obj("backup")?.let { backup ->
                                TeamEventStatusAllianceBackup(
                                        raw = backup,
                                        out = backup.string("out"),
                                        _in = backup.string("in")
                                )
                            },
                            pick = alliance.int("pick")
                    )
                },
                playoff = it.value.obj("playoff")?.let { playoff ->
                    TeamEventStatusPlayoff(
                            raw = playoff,
                            level = playoff.string("level"),
                            current_level_record = playoff.obj("current_level_record")?.let { current_level_record ->
                                WLTRecord(
                                        raw = current_level_record,
                                        losses = current_level_record.int("losses"),
                                        wins = current_level_record.int("wins"),
                                        ties = current_level_record.int("ties")
                                )
                            },
                            record = playoff.obj("record")?.let { record ->
                                WLTRecord(
                                        raw = record,
                                        losses = record.int("losses"),
                                        wins = record.int("wins"),
                                        ties = record.int("ties")
                                )
                            },
                            status = playoff.string("status"),
                            playoff_average = playoff.int("playoff_average")
                    )
                },
                alliance_status_str = it.value.string("alliance_status_str"),
                playoff_status_str = it.value.string("playoff_status_str"),
                overall_status_str = it.value.string("overall_status_str"),
                next_match_key = it.value.string("next_match_key"),
                last_match_key = it.value.string("last_match_key")
        )
    }
}


/**
 * Gets a list of matches for the given event.
 */
fun TBA.getEventMatches(
        event_key: String
): List<Match> {
    val response = getArray("/event/$event_key/matches")
    return response.map { it as JsonObject }.map {
        Match(
                raw = it,
                key = it.string("key"),
                comp_level = it.string("comp_level"),
                set_number = it.int("set_number"),
                match_number = it.int("match_number"),
                alliances = Alliances(
                        blue = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        },
                        red = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        }
                ),
                winning_alliance = it.string("winning_alliance"),
                event_key = it.string("event_key"),
                time = it.int("time"),
                actual_time = it.int("actual_time"),
                predicted_time = it.int("predicted_time"),
                post_result_time = it.int("post_result_time"),
                score_breakdown = it.obj("score_breakdown"),
                videos = it.objList("videos")
        )
    }
}


/**
 * Gets a short-form list of matches for the given event.
 */
fun TBA.getEventMatchesSimple(
        event_key: String
): List<MatchSimple> {
    val response = getArray("/event/$event_key/matches/simple")
    return response.map { it as JsonObject }.map {
        MatchSimple(
                raw = it,
                key = it.string("key"),
                comp_level = it.string("comp_level"),
                set_number = it.int("set_number"),
                match_number = it.int("match_number"),
                alliances = Alliances(
                        blue = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        },
                        red = it.obj("blue")?.let { alliance ->
                            MatchAlliance(
                                    raw = alliance,
                                    score = alliance.int("score"),
                                    team_keys = alliance.stringList("team_keys"),
                                    surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                    dq_team_keys = alliance.stringList("dq_team_keys")
                            )
                        }
                ),
                winning_alliance = it.string("winning_alliance"),
                event_key = it.string("event_key"),
                time = it.int("time"),
                predicted_time = it.int("predicted_time"),
                actual_time = it.int("actual_time")
        )
    }
}


/**
 * Gets a list of match keys for the given event.
 */
fun TBA.getEventMatchesKeys(
        event_key: String
): List<String> {
    val response = getArray("/event/$event_key/matches/keys")
    return response.map { it as String }
}


/**
 * Gets an array of Match Keys for the given event key that have timeseries data. Returns an empty array if no matches have timeseries data.
 *WARNING:* This is *not* official data, and is subject to a significant possibility of error, or missing data. Do not rely on this data for any purpose. In fact, pretend we made it up.
 *WARNING:* This endpoint and corresponding data models are under *active development* and may change at any time, including in breaking ways.
 */
fun TBA.getEventMatchTimeseries(
        event_key: String
): List<String> {
    val response = getArray("/event/$event_key/matches/timeseries")
    return response.map { it as String }
}


/**
 * Gets a list of awards from the given event.
 */
fun TBA.getEventAwards(
        event_key: String
): List<Award> {
    val response = getArray("/event/$event_key/awards")
    return response.map { it as JsonObject }.map {
        Award(
                raw = it,
                name = it.string("name"),
                award_type = it.int("award_type"),
                event_key = it.string("event_key"),
                recipient_list = it.genericArray("recipient_list")?.mapToList { recipient_listItem ->
                    AwardRecipient(
                            raw = recipient_listItem,
                            team_key = recipient_listItem.string("team_key"),
                            awardee = recipient_listItem.string("awardee")
                    )
                },
                year = it.int("year")
        )
    }
}


/**
 * Gets a `Match` object for the given match key.
 */
fun TBA.getMatch(
        match_key: String
): Match {
    val response = get("/match/$match_key")
    return Match(
            raw = response,
            key = response.string("key"),
            comp_level = response.string("comp_level"),
            set_number = response.int("set_number"),
            match_number = response.int("match_number"),
            alliances = Alliances(
                    blue = response.obj("blue")?.let { alliance ->
                        MatchAlliance(
                                raw = alliance,
                                score = alliance.int("score"),
                                team_keys = alliance.stringList("team_keys"),
                                surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                dq_team_keys = alliance.stringList("dq_team_keys")
                        )
                    },
                    red = response.obj("blue")?.let { alliance ->
                        MatchAlliance(
                                raw = alliance,
                                score = alliance.int("score"),
                                team_keys = alliance.stringList("team_keys"),
                                surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                dq_team_keys = alliance.stringList("dq_team_keys")
                        )
                    }
            ),
            winning_alliance = response.string("winning_alliance"),
            event_key = response.string("event_key"),
            time = response.int("time"),
            actual_time = response.int("actual_time"),
            predicted_time = response.int("predicted_time"),
            post_result_time = response.int("post_result_time"),
            score_breakdown = response.obj("score_breakdown"),
            videos = response.objList("videos")
    )
}


/**
 * Gets a short-form `Match` object for the given match key.
 */
fun TBA.getMatchSimple(
        match_key: String
): MatchSimple {
    val response = get("/match/$match_key/simple")
    return MatchSimple(
            raw = response,
            key = response.string("key"),
            comp_level = response.string("comp_level"),
            set_number = response.int("set_number"),
            match_number = response.int("match_number"),
            alliances = Alliances(
                    blue = response.obj("blue")?.let { alliance ->
                        MatchAlliance(
                                raw = alliance,
                                score = alliance.int("score"),
                                team_keys = alliance.stringList("team_keys"),
                                surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                dq_team_keys = alliance.stringList("dq_team_keys")
                        )
                    },
                    red = response.obj("blue")?.let { alliance ->
                        MatchAlliance(
                                raw = alliance,
                                score = alliance.int("score"),
                                team_keys = alliance.stringList("team_keys"),
                                surrogate_team_keys = alliance.stringList("surrogate_team_keys"),
                                dq_team_keys = alliance.stringList("dq_team_keys")
                        )
                    }
            ),
            winning_alliance = response.string("winning_alliance"),
            event_key = response.string("event_key"),
            time = response.int("time"),
            predicted_time = response.int("predicted_time"),
            actual_time = response.int("actual_time")
    )
}


/**
 * Gets an array of game-specific Match Timeseries objects for the given match key or an empty array if not available.
 *WARNING:* This is *not* official data, and is subject to a significant possibility of error, or missing data. Do not rely on this data for any purpose. In fact, pretend we made it up.
 *WARNING:* This endpoint and corresponding data models are under *active development* and may change at any time, including in breaking ways.
 */
fun TBA.getMatchTimeseries(
        match_key: String
): List<Map<String, Any?>> {
    val response = getArray("/match/$match_key/timeseries")
    return response.map { it as JsonObject }
}


/**
 * Gets a list of districts and their corresponding district key, for the given year.
 */
fun TBA.getDistrictsByYear(
        year: Int
): List<DistrictList> {
    val response = getArray("/districts/$year")
    return response.map { it as JsonObject }.map {
        DistrictList(
                raw = it,
                abbreviation = it.string("abbreviation"),
                display_name = it.string("display_name"),
                key = it.string("key"),
                year = it.int("year")
        )
    }
}


/**
 * Gets a list of events in the given district.
 */
fun TBA.getDistrictEvents(
        district_key: String
): List<Event> {
    val response = getArray("/district/$district_key/events")
    return response.map { it as JsonObject }.map {
        Event(
                raw = it,
                key = it.string("key"),
                name = it.string("name"),
                event_code = it.string("event_code"),
                event_type = it.int("event_type"),
                district = it.obj("district")?.let { district ->
                    DistrictList(
                            raw = district,
                            abbreviation = district.string("abbreviation"),
                            display_name = district.string("display_name"),
                            key = district.string("key"),
                            year = district.int("year")
                    )
                },
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                start_date = it.string("start_date"),
                end_date = it.string("end_date"),
                year = it.int("year"),
                short_name = it.string("short_name"),
                event_type_string = it.string("event_type_string"),
                week = it.int("week"),
                address = it.string("address"),
                postal_code = it.string("postal_code"),
                gmaps_place_id = it.string("gmaps_place_id"),
                gmaps_url = it.string("gmaps_url"),
                lat = it.double("lat"),
                lng = it.double("lng"),
                location_name = it.string("location_name"),
                timezone = it.string("timezone"),
                website = it.string("website"),
                first_event_id = it.string("first_event_id"),
                first_event_code = it.string("first_event_code"),
                webcasts = it.genericArray("webcasts")?.mapToList { webcastsItem ->
                    Webcast(
                            raw = webcastsItem,
                            type = webcastsItem.string("type"),
                            channel = webcastsItem.string("channel"),
                            file = webcastsItem.string("file")
                    )
                },
                division_keys = it.stringList("division_keys"),
                parent_event_key = it.string("parent_event_key"),
                playoff_type = it.int("playoff_type"),
                playoff_type_string = it.string("playoff_type_string")
        )
    }
}


/**
 * Gets a short-form list of events in the given district.
 */
fun TBA.getDistrictEventsSimple(
        district_key: String
): List<EventSimple> {
    val response = getArray("/district/$district_key/events/simple")
    return response.map { it as JsonObject }.map {
        EventSimple(
                raw = it,
                key = it.string("key"),
                name = it.string("name"),
                event_code = it.string("event_code"),
                event_type = it.int("event_type"),
                district = it.obj("district")?.let { district ->
                    DistrictList(
                            raw = district,
                            abbreviation = district.string("abbreviation"),
                            display_name = district.string("display_name"),
                            key = district.string("key"),
                            year = district.int("year")
                    )
                },
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                start_date = it.string("start_date"),
                end_date = it.string("end_date"),
                year = it.int("year")
        )
    }
}


/**
 * Gets a list of event keys for events in the given district.
 */
fun TBA.getDistrictEventsKeys(
        district_key: String
): List<String> {
    val response = getArray("/district/$district_key/events/keys")
    return response.map { it as String }
}


/**
 * Gets a list of `Team` objects that competed in events in the given district.
 */
fun TBA.getDistrictTeams(
        district_key: String
): List<Team> {
    val response = getArray("/district/$district_key/teams")
    return response.map { it as JsonObject }.map {
        Team(
                raw = it,
                key = it.string("key"),
                team_number = it.int("team_number"),
                nickname = it.string("nickname"),
                name = it.string("name"),
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country"),
                address = it.string("address"),
                postal_code = it.string("postal_code"),
                gmaps_place_id = it.string("gmaps_place_id"),
                gmaps_url = it.string("gmaps_url"),
                lat = it.double("lat"),
                lng = it.double("lng"),
                location_name = it.string("location_name"),
                website = it.string("website"),
                rookie_year = it.int("rookie_year"),
                motto = it.string("motto"),
                home_championship = it.obj("home_championship")
        )
    }
}


/**
 * Gets a short-form list of `Team` objects that competed in events in the given district.
 */
fun TBA.getDistrictTeamsSimple(
        district_key: String
): List<TeamSimple> {
    val response = getArray("/district/$district_key/teams/simple")
    return response.map { it as JsonObject }.map {
        TeamSimple(
                raw = it,
                key = it.string("key"),
                team_number = it.int("team_number"),
                nickname = it.string("nickname"),
                name = it.string("name"),
                city = it.string("city"),
                state_prov = it.string("state_prov"),
                country = it.string("country")
        )
    }
}


/**
 * Gets a list of `Team` objects that competed in events in the given district.
 */
fun TBA.getDistrictTeamsKeys(
        district_key: String
): List<String> {
    val response = getArray("/district/$district_key/teams/keys")
    return response.map { it as String }
}


/**
 * Gets a list of team district rankings for the given district.
 */
fun TBA.getDistrictRankings(
        district_key: String
): List<DistrictRanking> {
    val response = getArray("/district/$district_key/rankings")
    return response.map { it as JsonObject }.map {
        DistrictRanking(
                raw = it,
                team_key = it.string("team_key"),
                rank = it.int("rank"),
                rookie_bonus = it.int("rookie_bonus"),
                point_total = it.int("point_total"),
                event_points = it.objList("event_points")
        )
    }
}
