package com.example.rivarly.dto.team;

import com.example.rivarly.entity.Person;
import lombok.Data;

import java.util.Set;

/**
 * A DTO representing the response structure for a team.
 * Contains essential details such as team ID, name, captain, members, icon path, and its temporary status.
 */
@Data
public class TeamResponse {
    /**
     * Unique identifier for the team.
     */
    private Long teamId;

    /**
     * Name of the team.
     */
    private String teamName;

    /**
     * The captain of the team.
     */
    private Person captain;

    /**
     * Members of the team.
     */
    private Set<Person> members;

    /**
     * Path to the icon representing the team.
     */
    private String iconPath;

    /**
     * Indicates whether the team is temporary.
     */
    private boolean temporary;
}