package com.example.calcapp.controller;

import com.example.calcapp.model.Calculation;
import com.example.calcapp.repository.CalculationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CalcController {

    private final CalculationRepository repository;

    public CalcController(CalculationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("history", repository.findAll());
        return "index";
    }
    

    @PostMapping("/calculate")
    public String calculate(
            @RequestParam double value1,
            @RequestParam(required = false) Double value2,
            @RequestParam String operation,
            @RequestParam(required = false) String unitFrom,
            @RequestParam(required = false) String unitTo,
            Model model
    ) {
        double result = 0;
        String description = "";

        switch (operation) {
            case "add":
                result = value1 + (value2 != null ? value2 : 0);
                description = "Dodawanie: " + value1 + " + " + value2 + " = " + result;
                break;
            case "subtract":
                result = value1 - (value2 != null ? value2 : 0);
                description = "Odejmowanie: " + value1 + " - " + value2 + " = " + result;
                break;
            case "multiply":
                result = value1 * (value2 != null ? value2 : 1);
                description = "Mnożenie: " + value1 + " × " + value2 + " = " + result;
                break;
            case "divide":
                result = (value2 != null && value2 != 0) ? value1 / value2 : 0;
                description = "Dzielenie: " + value1 + " ÷ " + value2 + " = " + result;
                break;
            case "sqrt":
                result = Math.sqrt(value1);
                description = "Pierwiastek kwadratowy z " + value1 + " = " + result;
                break;
            case "pow":
                result = Math.pow(value1, (value2 != null ? value2 : 2));
                description = value1 + " do potęgi " + value2 + " = " + result;
                break;
            case "sin":
                result = Math.sin(Math.toRadians(value1));
                description = "sin(" + value1 + ") = " + result;
                break;
            case "cos":
                result = Math.cos(Math.toRadians(value1));
                description = "cos(" + value1 + ") = " + result;
                break;

            case "mass":
                result = convertMass(value1, unitFrom, unitTo);
                description = value1 + " " + unitFrom + " = " + result + " " + unitTo;
                break;

            case "length":
                result = convertLength(value1, unitFrom, unitTo);
                description = value1 + " " + unitFrom + " = " + result + " " + unitTo;
                break;

            case "time":
                result = convertTime(value1, unitFrom, unitTo);
                description = value1 + " " + unitFrom + " = " + result + " " + unitTo;
                break;
        }

        Calculation calc = new Calculation();
        calc.setDescription(description);
        calc.setTimestamp(LocalDateTime.now());
        repository.save(calc);

        model.addAttribute("result", result);
        model.addAttribute("history", repository.findAll());
        return "index";
    }

    private double convertMass(double value, String from, String to) {
        double inKg = switch (from) {
            case "g" -> value / 1000;
            case "t" -> value * 1000;
            case "lb" -> value * 0.453592;
            default -> value;
        };
        return switch (to) {
            case "g" -> inKg * 1000;
            case "t" -> inKg / 1000;
            case "lb" -> inKg / 0.453592;
            default -> inKg;
        };
    }

    private double convertLength(double value, String from, String to) {
        double inMeters = switch (from) {
            case "cm" -> value / 100;
            case "mm" -> value / 1000;
            case "ft" -> value * 0.3048;
            case "in" -> value * 0.0254;
            default -> value;
        };
        return switch (to) {
            case "cm" -> inMeters * 100;
            case "mm" -> inMeters * 1000;
            case "ft" -> inMeters / 0.3048;
            case "in" -> inMeters / 0.0254;
            default -> inMeters;
        };
    }

    private double convertTime(double value, String from, String to) {
        double inSeconds = switch (from) {
            case "min" -> value * 60;
            case "h" -> value * 3600;
            default -> value;
        };
        return switch (to) {
            case "min" -> inSeconds / 60;
            case "h" -> inSeconds / 3600;
            default -> inSeconds;
        };
    }
}
