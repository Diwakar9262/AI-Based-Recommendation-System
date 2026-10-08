package com.guvi.recommendation.controller;

import com.guvi.recommendation.model.Item;
import com.guvi.recommendation.repository.InMemoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin
public class ItemController {
    private final InMemoryRepository repository;

    public ItemController(InMemoryRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Item> items() { return repository.getItems(); }
}
