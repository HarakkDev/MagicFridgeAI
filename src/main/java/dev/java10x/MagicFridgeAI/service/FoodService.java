package dev.java10x.MagicFridgeAI.service;

import dev.java10x.MagicFridgeAI.dto.DetailFoodData;
import dev.java10x.MagicFridgeAI.model.Food;
import dev.java10x.MagicFridgeAI.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodService {

    @Autowired
    FoodRepository foodRepository;

    public ResponseEntity<String> criar(Food food) {
        var existenteOpt = foodRepository.findFirstByNomeIgnoreCase(food.getNome());
        if (existenteOpt.isPresent()) {
            Food existente = existenteOpt.get();
            Integer qtdAtual = existente.getQuantidade() != null ? existente.getQuantidade() : 0;
            Integer qtdNova  = food.getQuantidade() != null ? food.getQuantidade() : 0;
            existente.setQuantidade(qtdAtual + qtdNova);
            existente.setValidade(food.getValidade());
            existente.setCategoria(food.getCategoria());
            foodRepository.save(existente);
            return ResponseEntity.status(HttpStatus.OK).body("Quantidade do ingrediente atualizada com sucesso!");
        }

        foodRepository.save(food);
        return ResponseEntity.status(HttpStatus.CREATED).body("Ingrediente adicionado com sucesso!");
    }

    public ResponseEntity<List<DetailFoodData>> listar(){
        List<DetailFoodData> comidas = this.foodRepository.findAllFood();
        return ResponseEntity.ok(comidas);
    }

    public ResponseEntity<String> buscar(Long id) {
        Food comida = foodRepository.findById(id).orElse(null);
        if(comida != null){
            return ResponseEntity.ok("Ingrediente selecionado: " + comida.getNome());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingrediente não encontrada para o ID " + id);
    }

    public ResponseEntity<String> deletar(Long id) {
        if (!foodRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingrediente não encontrado para o ID " + id);
        }
        foodRepository.deleteById(id);
        return ResponseEntity.ok("Ingrediente deletado com sucesso!");
    }

    public ResponseEntity<String> atualizar(Long id, Food food) {
        if(!foodRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingrediente não encontrado para o ID " + id);
        }
        food.setId(food.getId());
        foodRepository.save(food);
        return ResponseEntity.status(HttpStatus.OK).body("Ingrediente " + food.getNome() + "atualizado com sucesso!");
    }

    public ResponseEntity<String> deletarTodos() {
        foodRepository.deleteAll();
        return ResponseEntity.ok("Todos os ingredientes foram removidos com sucesso!");
    }
}
