package dev.java10x.MagicFridgeAI.dto;

import dev.java10x.MagicFridgeAI.enums.Categoria;
import dev.java10x.MagicFridgeAI.model.Food;

import java.time.LocalDate;

public record DetailFoodData(
        Long id,
        String nome,
        int quantidade,
        Categoria categoria,
        LocalDate validade
) {
    public DetailFoodData(Food food) {
        this(
                food.getId(),
                food.getNome(),
                food.getQuantidade(),
                food.getCategoria(),
                food.getValidade()
        );
    }
}
