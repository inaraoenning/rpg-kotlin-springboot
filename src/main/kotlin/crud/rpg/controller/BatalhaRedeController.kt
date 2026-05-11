package crud.rpg.controller

import crud.rpg.service.BatalhaService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rede")
class BatalhaRedeController(private val batalhaService: BatalhaService) {

    // Recebe o ataque do computador rival
    @PostMapping("/receber-ataque")
    fun receberAtaque(@RequestBody payload: Map<String, Any>): String {
        val idBatalha = (payload["idBatalha"] as? Number)?.toLong() ?: return "Erro: idBatalha inválido"
        val dano = (payload["dano"] as? Number)?.toInt() ?: return "Erro: dano inválido"
        val atacanteNome = payload["atacanteNome"] as? String ?: return "Erro: atacanteNome inválido"
        
        return batalhaService.processarAtaqueRecebido(idBatalha, dano, atacanteNome)
    }

    // Endpoint para você disparar um ataque ao rival via Postman
    @PostMapping("/atacar")
    fun atacarRival(@RequestBody payload: Map<String, Any>): String {
        val idBatalha = (payload["idBatalha"] as? Number)?.toLong() ?: return "Erro: idBatalha inválido"
        val dano = (payload["dano"] as? Number)?.toInt() ?: return "Erro: dano inválido"
        val atacanteNome = payload["atacanteNome"] as? String ?: return "Erro: atacanteNome inválido"
        val urlRival = payload["urlRival"] as? String // opcional
        
        batalhaService.enviarAtaqueParaRival(idBatalha, dano, atacanteNome, urlRival)
        return "Tentativa de ataque enviada ao rival."
    }
}
