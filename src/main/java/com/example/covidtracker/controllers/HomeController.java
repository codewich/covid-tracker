package com.example.covidtracker.controllers;

import com.example.covidtracker.models.RegionalStat;
import com.example.covidtracker.services.CovidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CovidDataService covidDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<RegionalStat> allStats= covidDataService.getAllStats();
        ZonedDateTime updateTime = covidDataService.getUpdateTime();
        model.addAttribute("regionalStats", allStats);
        model.addAttribute("totalCases", allStats.stream().mapToInt(RegionalStat::getLatestTotalCases).sum());
        model.addAttribute("totalNewCases", allStats.stream().mapToInt(RegionalStat::getDiffFromPrevDay).sum());
        DateTimeFormatter updateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z");
        String formattedUpdateTime = updateTime.format(updateTimeFormatter);
        model.addAttribute("updateTime", formattedUpdateTime);
        return "home";
    }
}
