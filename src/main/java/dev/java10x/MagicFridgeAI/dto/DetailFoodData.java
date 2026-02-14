package dev.java10x.MagicFridgeAI.dto;

import dev.java10x.MagicFridgeAI.enums.Categoria;
import dev.java10x.MagicFridgeAI.model.Food;

import java.time.LocalDateTime;

public record DetailFoodData(
        Long id,
        String nome,
        int quantidade,
        Categoria categoria,
        LocalDateTime validade
) {
    public DetailFoodData (
          Food food
    ) {
        this(
                food.getId(),
                food.getNome(),
                food.getQuantidade(),
                food.getCategoria(),
                food.getValidade()
        );
    }
}
