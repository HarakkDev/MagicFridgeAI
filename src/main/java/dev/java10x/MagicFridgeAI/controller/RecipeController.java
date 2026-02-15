package dev.java10x.MagicFridgeAI.controller;

import dev.java10x.MagicFridgeAI.dto.DetailFoodData;
import dev.java10x.MagicFridgeAI.service.ChatGptService;
import dev.java10x.MagicFridgeAI.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/receita")
public class RecipeController {

    @Autowired
    private ChatGptService chatGptService;

    @Autowired
    private FoodService foodService;

    @GetMapping(value="/gerarReceitaImagem", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> gerarReceitaImagem() {
        List<DetailFoodData> ingredientes = foodService.listar().getBody();
        if (ingredientes == null || ingredientes.isEmpty()) {
            return Mono.just("<html><body><h2>Sem ingredientes cadastrados.</h2></body></html>");
        }
        return chatGptService.gerarReceitaEImagem(ingredientes)
                .map(data -> chatGptService.buildHtml(data));
    }
}