package site.madhavjha.covidtracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import site.madhavjha.covidtracker.model.CovidStat;
import site.madhavjha.covidtracker.service.CovidDataService;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CovidDataService covidDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<CovidStat> stats = covidDataService.getStats();
        long totalReportedCases = stats.stream().mapToLong(s->s.getLatestTotalCases()).sum();
        long totalNewCases =  stats.stream().mapToLong(s->s.getDiffFromPrevDay()).sum();;
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
        model.addAttribute("stats", stats);
        return "home";
    }
}
