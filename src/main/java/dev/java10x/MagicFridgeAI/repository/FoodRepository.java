package dev.java10x.MagicFridgeAI.repository;

import dev.java10x.MagicFridgeAI.dto.DetailFoodData;
import dev.java10x.MagicFridgeAI.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findFirstByNomeIgnoreCase(String nome);

    @Query("""
       select new dev.java10x.MagicFridgeAI.dto.DetailFoodData(food)
           from Food food
    """)
    List<DetailFoodData> findAllFood();

}
