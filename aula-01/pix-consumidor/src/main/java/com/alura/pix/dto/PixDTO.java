package com.alura.pix.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PixDTO {
    private String identifier;
    private String chaveOrigem;
    private String chaveDestino;
    private Double valor;
    private LocalDateTime dataTransferencia;
    private PixStatus status;

    @Override
    public String toString() {
        return "PixDTO{" +
                "identifier='" + identifier + '\'' +
                ", chaveOrigem='" + chaveOrigem + '\'' +
                ", chaveDestino='" + chaveDestino + '\'' +
                ", valor=" + valor +
                ", dataTransferencia=" + dataTransferencia +
                ", status=" + status +
                '}';
    }
}
