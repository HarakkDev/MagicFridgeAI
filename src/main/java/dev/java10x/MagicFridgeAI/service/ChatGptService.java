package dev.java10x.MagicFridgeAI.service;

import dev.java10x.MagicFridgeAI.dto.DetailFoodData;
import dev.java10x.MagicFridgeAI.dto.RecipeWithImageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatGptService {

    @Autowired
    private WebClient webClient;

    private final String apiKey = System.getenv("CHATGPT_API_KEY");

    private String ingredientesToText(List<DetailFoodData> ingredientes) {
        return ingredientes.stream()
                .map(i -> {
                    String nome = i.nome();
                    int qtd = i.quantidade();
                    return qtd > 0 ? nome + " (" + qtd + ")" : nome;
                })
                .collect(Collectors.joining(", "));
    }

    public Mono<String> generateRecipeText(List<DetailFoodData> ingredientes) {
        String itens = ingredientesToText(ingredientes);

        String prompt = """
                Faça uma receita usando estes ingredientes: %s

                Regras:
                - Use APENAS os ingredientes enviados, e temperos básicos (sal, pimenta, água, óleo) se precisar.
                - Formato:
                  1) Nome da receita
                  2) Ingredientes - Não é necessário usar TODO o estoque, use somente o necessário para UMA pessoa.
                  3) Modo de preparo (passo a passo)
                  4) Tempo estimado
                  5) Dicas
                  6) Descrição visual do prato para gerar uma imagem para o usuário (Será um prompt para outra IA, fale coisas SIMPLES não quero exagero).
                """.formatted(itens);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-5-mini",
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content",
                                "Você é um chefe de cozinha. Você só responde com receitas baseadas nos ingredientes e sem exagerar nas criações " +
                                    "Não é necessário utilizar TODOS ingredientes e nem TODA a quantidade, todos pedidos serão feito para somente UMA pessoa se alimentar" +
                                    "Se receber uma requisição fora desse contexto, diga que é feito SOMENTE para escrever receitas."
                        ),
                        Map.of("role", "user", "content", prompt)
                )
        );

        return webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var choices = (List<Map<String, Object>>) response.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return message.get("content").toString();
                    }
                    return "Nenhuma receita foi gerada.";
                });
    }

    private Mono<String> generateRecipeImageFromPrompt(String promptImagem) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-image-1.5",
                "prompt", promptImagem,
                "size", "1536x1024",
                "n", 1
        );

        return webClient.post()
                .uri("/images/generations")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var data = (List<Map<String, Object>>) response.get("data");
                    if (data != null && !data.isEmpty()) {
                        Map<String, Object> imageData = data.get(0);
                        Object url = imageData.get("url");
                        if (url != null && !url.toString().isBlank()) {
                            return url.toString();
                        }
                        Object base64 = imageData.get("b64_json");
                        if (base64 != null && !base64.toString().isBlank()) {
                            return "data:image/png;base64," + base64;
                        }
                    }
                    return "";
                });
    }

    public Mono<RecipeWithImageData> generateReceitaImagem(List<DetailFoodData> ingredientes) {
        return generateRecipeText(ingredientes)
                .flatMap(receita -> {
                    String promptImagem = """
                            A receita será essa: %s
                            E a imagem deve SEGUIR EXATAMENTE o que está escrito no tópico a seguir:
                            6) Descrição visual do prato para gerar uma imagem para o usuário (Será um prompt para outra IA).
                            A imagem deve ser o prato finalizado, não exagere na imagem, faça algo comum.
                            Extremamente proibido: NÃO COLOCAR INGREDIENTES DE SOBREMESA NO PRATO PRINCIPAL!!!
                            Extremamente proibido: NÃO MISTURAR INGREDIENTES DO PRATO PRINCIPAL E SOBREMESA NO MESMO RECIPIENTE!
                            """.formatted(receita);

                    return generateRecipeImageFromPrompt(promptImagem)
                            .map(url -> new RecipeWithImageData(receita, url));
                });
    }

    public String buildHtml(RecipeWithImageData data) {
        String receitaHtml = data.receita()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>");

        String imgTag = (data.imageUrl() != null && !data.imageUrl().isBlank())
                ? "<img src=\"" + data.imageUrl() + "\" style=\"max-width: 600px; border-radius: 12px;\"/>"
                : "<p><b>Imagem não foi gerada.</b></p>";

        return """
                <html>
                  <body style="font-family: Arial; padding: 16px;">
                    <h1>🍽️ Receita</h1>
                    <div style="margin-bottom: 16px; line-height: 1.5;">%s</div>
                    <h2>📸 Como vai ficar</h2>
                    %s
                  </body>
                </html>
                """.formatted(receitaHtml, imgTag);
    }
}
