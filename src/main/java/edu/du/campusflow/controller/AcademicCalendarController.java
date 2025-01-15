package edu.du.campusflow.controller;

import edu.du.campusflow.entity.AcademicCalendar;
import edu.du.campusflow.service.AcademicCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/iframe/calendar")
public class AcademicCalendarController {
    
    private static final Logger logger = LoggerFactory.getLogger(AcademicCalendarController.class);

    @Autowired
    private AcademicCalendarService academicCalendarService;

    @GetMapping("/addAcademicCalendar")
    public String showAddCalendarPage(Model model) {
        return "view/iframe/calendar/addAcademicCalendar";
    }

    @GetMapping("/view_event")
    public String showCalendarPage(Model model) {
        List<AcademicCalendar> calendars = academicCalendarService.getAllCalendars();
        model.addAttribute("calendars", calendars);
        return "view/iframe/calendar/view_event";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addAcademicCalendar(@RequestBody AcademicCalendar academicCalendar) {
        logger.info("Received request to add calendar: {}", academicCalendar);
        return academicCalendarService.addAcademicCalendar(academicCalendar);
    }

    @PutMapping("/api/academic-calendar/{id}")
    @ResponseBody
    public ResponseEntity<?> updateAcademicCalendar(
            @PathVariable("id") Long id,
            @RequestBody AcademicCalendar academicCalendar) {
        logger.info("Received request to update calendar {}: {}", id, academicCalendar);
        academicCalendar.setCalendarId(id);
        return academicCalendarService.updateAcademicCalendar(academicCalendar);
    }

    @GetMapping("/api/academic-calendar")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAcademicCalendars() {
        try {
            List<AcademicCalendar> calendars = academicCalendarService.getAllCalendars();
            List<Map<String, Object>> events = calendars.stream()
                .map(calendar -> {
                    Map<String, Object> event = new HashMap<>();
                    event.put("calendarId", calendar.getCalendarId());
                    event.put("title", calendar.getTitle());
                    event.put("startDate", calendar.getStartDate().toLocalDate());
                    event.put("endDate", calendar.getEndDate().toLocalDate());
                    event.put("description", calendar.getDescription());
                    return event;
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to fetch calendars", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/api/academic-calendar/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAcademicCalendar(@PathVariable("id") Long id) {
        logger.info("Received request to delete calendar: {}", id);
        return academicCalendarService.deleteCalendar(id);
    }
}