package com.example.rivarly.service;

import com.example.rivarly.dto.team.TeamResponse;
import com.example.rivarly.entity.Person;
import com.example.rivarly.entity.Team;
import com.example.rivarly.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

   // public TeamResponse createTeam();
}
