package com.oficina.backend.relatorio;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.oficina.backend.DTO.ProdutoServicoFinalizadoDTO;
import com.oficina.backend.DTO.ServicoFinalizadoDTO;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RelatorioServicoPdfGenerator {
    private static final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Relatório geral
    public static ByteArrayInputStream gerarRelatorio(List<ServicoFinalizadoDTO> servicosFinalizados) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph cabecalho = new Paragraph("Relatório de Serviços Finalizados", titulo);
            cabecalho.setAlignment(Element.ALIGN_CENTER);
            document.add(cabecalho);
            document.add(Chunk.NEWLINE);

            PdfPTable tabela = new PdfPTable(11); // 11 colunas
            tabela.setWidthPercentage(100);
            tabela.setWidths(new int[]{1, 3, 2, 2, 2, 3, 3, 3, 3, 2, 4});

            Font cabecalhoFonte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            String[] colunas = {"ID", "Descrição", "Preço", "Data Início", "Data Finalização",
                    "Nome Cliente", "CPF Cliente", "Veículo", "Produtos Usados",
                    "Data Garantia", "Detalhes Finalização"};

            for (String col : colunas) {
                PdfPCell header = new PdfPCell(new Phrase(col, cabecalhoFonte));
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setBackgroundColor(Color.LIGHT_GRAY);
                tabela.addCell(header);
            }

            for (ServicoFinalizadoDTO s : servicosFinalizados) {
                tabela.addCell(String.valueOf(s.getId()));
                tabela.addCell(s.getDescricao() != null ? s.getDescricao() : "");
                tabela.addCell(s.getPreco() != null ? String.format("R$ %.2f", s.getPreco()) : "");
                tabela.addCell(s.getDataInicio() != null ? s.getDataInicio().format(formatadorData) : "");
                tabela.addCell(s.getDataFinalizacao() != null ? s.getDataFinalizacao().format(formatadorData) : "");
                tabela.addCell(s.getCliente() != null ? s.getCliente().getNome() : "");
                tabela.addCell(s.getCliente() != null ? s.getCliente().getCpf() : "");
                tabela.addCell(s.getVeiculo() != null ? s.getVeiculo().getPlaca() : "");

                // produtos usados com quantidade
                String produtos = s.getQuantidadeProdutosUsados() != null ?
                        s.getQuantidadeProdutosUsados().stream()
                                .map(p -> p.getNome() + " (x" + p.getQuantidade() + ")")
                                .collect(Collectors.joining(", "))
                        : "";
                tabela.addCell(produtos);

                tabela.addCell(s.getDataGarantia() != null ? s.getDataGarantia().format(formatadorData) : "");
                tabela.addCell(s.getDetalhesFinalizacao() != null ? s.getDetalhesFinalizacao() : "");
            }

            document.add(tabela);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // Relatório único
    public static ByteArrayInputStream gerarRelatorioUnico(ServicoFinalizadoDTO s) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph cabecalho = new Paragraph("Relatório de Serviço Finalizado", titulo);
            cabecalho.setAlignment(Element.ALIGN_CENTER);
            document.add(cabecalho);
            document.add(Chunk.NEWLINE);

            Font fonteNormal = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("ID: " + s.getId(), fonteNormal));
            document.add(new Paragraph("Descrição: " + s.getDescricao(), fonteNormal));
            document.add(new Paragraph("Preço: " + (s.getPreco() != null ? String.format("R$ %.2f", s.getPreco()) : ""), fonteNormal));
            document.add(new Paragraph("Data Início: " + (s.getDataInicio() != null ? s.getDataInicio().format(formatadorData) : ""), fonteNormal));
            document.add(new Paragraph("Data Finalização: " + (s.getDataFinalizacao() != null ? s.getDataFinalizacao().format(formatadorData) : ""), fonteNormal));
            document.add(new Paragraph(
                    "Nome Cliente: " + (s.getCliente() != null ? s.getCliente().getNome() : ""),
                    fonteNormal
            ));
            document.add(new Paragraph(
                    "CPF Cliente: " + (s.getCliente() != null ? s.getCliente().getCpf() : ""),
                    fonteNormal
            ));
            document.add(new Paragraph("Veículo: " + (s.getVeiculo() != null ? s.getVeiculo().getPlaca() : ""), fonteNormal));

            String produtos = s.getQuantidadeProdutosUsados() != null ?
                    s.getQuantidadeProdutosUsados().stream()
                            .map(p -> p.getNome() + " (x" + p.getQuantidade() + ")")
                            .collect(Collectors.joining(", "))
                    : "";
            document.add(new Paragraph("Produtos Usados: " + produtos, fonteNormal));

            document.add(new Paragraph("Data Garantia: " + (s.getDataGarantia() != null ? s.getDataGarantia().format(formatadorData) : ""), fonteNormal));
            document.add(new Paragraph("Detalhes Finalização: " + (s.getDetalhesFinalizacao() != null ? s.getDetalhesFinalizacao() : ""), fonteNormal));

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
