package com.oficina.backend.controller;

import com.oficina.backend.DTO.ServicoFinalizadoDTO;
import com.oficina.backend.model.Produto;
import com.oficina.backend.model.ServicoFinalizado;
import com.oficina.backend.relatorio.RelatorioProdutoPdfGenerator;
import com.oficina.backend.relatorio.RelatorioServicoPdfGenerator;
import com.oficina.backend.service.ProdutoService;
import com.oficina.backend.service.ServicoFinalizadoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private ServicoFinalizadoService servicoFinalizadoService;

    @Autowired
    private ProdutoService produtoService;

    // Relatório geral de serviços finalizados com filtros
    @GetMapping("/servicos-finalizados")
    public ResponseEntity<byte[]> gerarRelatorioServicosFinalizadosFiltrado(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String periodo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho
    ) {
        Pageable pageable = PageRequest.of(pagina, tamanho);

        Page<ServicoFinalizado> servicosPage =
                servicoFinalizadoService.buscarServicosFinalizados(termo, inicio, fim, periodo, pageable);

        List<ServicoFinalizadoDTO> servicosDTO = servicosPage.getContent().stream()
                .map(ServicoFinalizadoDTO::new) // transforma cada entidade em DTO
                .collect(Collectors.toList());

        ByteArrayInputStream pdf = RelatorioServicoPdfGenerator.gerarRelatorio(servicosDTO);

        String nomeArquivo = "relatorio-servicos-finalizados";
        if (periodo != null && !periodo.isBlank()) {
            nomeArquivo += "-" + periodo.toLowerCase();
        }
        nomeArquivo += ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nomeArquivo)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.readAllBytes());
    }

    // Relatório individual do serviço finalizado por ID
    @GetMapping("/servico-finalizado/{id}")
    public ResponseEntity<byte[]> gerarRelatorioUnico(@PathVariable Long id) {
        Optional<ServicoFinalizado> servicoOpt = servicoFinalizadoService.buscarPorId(id);

        if (servicoOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        ServicoFinalizadoDTO servicoDTO = new ServicoFinalizadoDTO(servicoOpt.get());

        ByteArrayInputStream pdf = RelatorioServicoPdfGenerator.gerarRelatorioUnico(servicoDTO);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-servico-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.readAllBytes());
    }

}
