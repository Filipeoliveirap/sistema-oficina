package com.oficina.backend.relatorio;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.oficina.backend.model.Produto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class RelatorioProdutoPdfGenerator {

    public static ByteArrayInputStream gerarRelatorio(List<Produto> produtos) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph cabecalho = new Paragraph("Relatório de Produtos com Estoque Baixo", titulo);
            cabecalho.setAlignment(Element.ALIGN_CENTER);
            document.add(cabecalho);
            document.add(Chunk.NEWLINE);

            PdfPTable tabela = new PdfPTable(3);
            tabela.setWidthPercentage(100);
            tabela.setWidths(new int[]{1, 4, 2});

            tabela.addCell("ID");
            tabela.addCell("Nome");
            tabela.addCell("Quantidade");

            for (Produto p : produtos) {
                tabela.addCell(String.valueOf(p.getId()));
                tabela.addCell(p.getNome());
                tabela.addCell(String.valueOf(p.getQuantidade()));
            }

            document.add(tabela);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
