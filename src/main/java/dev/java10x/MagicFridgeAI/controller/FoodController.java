package dev.java10x.MagicFridgeAI.controller;

import dev.java10x.MagicFridgeAI.dto.DetailFoodData;
import dev.java10x.MagicFridgeAI.model.Food;
import dev.java10x.MagicFridgeAI.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodController {

    @Autowired
    FoodService foodService;

    @PostMapping("/criar")
    public ResponseEntity<String> criar (@RequestBody Food food){
        return foodService.criar(food);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<DetailFoodData>> lista(){
        return foodService.listar();
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<String> buscar(@PathVariable Long id){
        return foodService.buscar(id);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id){
        return foodService.deletar(id);
    }

    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody Food food) {
        return foodService.atualizar(id, food);
    }
}
