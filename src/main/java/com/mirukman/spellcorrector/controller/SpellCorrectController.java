package com.mirukman.spellcorrector.controller;

import java.util.Set;

import com.google.gson.JsonArray;
import com.mirukman.spellcorrector.item.MovieItem;
import com.mirukman.spellcorrector.service.SpellCorrectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpellCorrectController {
    
    private final SpellCorrectService spellCorrectService;

    @Autowired
    public SpellCorrectController(SpellCorrectService spellCorrectService) {
        this.spellCorrectService = spellCorrectService;
    }

    @GetMapping("/search")
    public String find(@RequestParam String query) {
        
        Set<MovieItem> results = spellCorrectService.find(query);
        JsonArray array = new JsonArray();
        results.forEach(item -> {
            array.add(item.toJson());
        });

        return array.toString();
    }

}
