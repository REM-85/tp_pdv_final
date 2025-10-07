package com.ucaba.reservas.controller;

import com.ucaba.reservas.service.ForecastService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/monitor/forecast")
public class ForecastMonitorController {

    private final ForecastService forecastService;

    public ForecastMonitorController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping
    public String view(Model model) {
        model.addAttribute("snapshots", forecastService.monitor());
        return "monitor/forecast";
    }
}
